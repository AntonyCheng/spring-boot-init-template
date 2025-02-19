package top.sharehome.springbootinittemplate.config.sse;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.sse.condition.SseCondition;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;
import top.sharehome.springbootinittemplate.config.sse.enums.SseStatus;
import top.sharehome.springbootinittemplate.config.sse.properties.SseProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * SSE配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(SseProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(SseCondition.class)
public class SseConfiguration {

    private final SseProperties sseProperties;

    /**
     * 用户凭证连接池
     */
    private final static Map<Long, Map<String, SseEmitter>> TOKEN_SSE_EMITTER_POOL = new ConcurrentHashMap<>();

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseStream(Stream<String> messages) {
        return quickSseFlux(Flux.fromStream(messages), null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public SseEmitter quickSseStream(Stream<String> messages, Long timeout) {
        return quickSseFlux(Flux.fromStream(messages), timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseStrings(List<String> messages) {
        return quickSseFlux(Flux.fromIterable(messages), null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public SseEmitter quickSseStrings(List<String> messages, Long timeout) {
        return quickSseFlux(Flux.fromIterable(messages), timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseFlux(Flux<String> messages) {
        return quickSseFlux(messages, null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接等待超时时间
     */
    public SseEmitter quickSseFlux(Flux<String> messages, Long timeout) {
        SseEmitter sseEmitter = new SseEmitter(Objects.isNull(timeout) || timeout <= 0L ? 0L : timeout);
        sseEmitter.onCompletion(sseEmitter::complete);
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError(e -> {
            sseEmitter.complete();
            log.error(e.getMessage());
            throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
        });
        messages.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .startWith(new SseMessage().setStatus(SseStatus.CONNECTED.getName()))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName())))
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                })
                .doFinally(signalType -> sseEmitter.complete())
                .subscribe(message -> {
                    try {
                        sseEmitter.send(message);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                    }
                });
        return sseEmitter;
    }

    /**
     * 根据用户ID获取Sse连接
     *
     * @param userId 用户ID
     */
    public Map<String, SseEmitter> getSseEmitter(Long userId) {
        if (Objects.isNull(userId)) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "用户ID不能为空");
        }
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_POOL.get(userId);
        if (MapUtils.isEmpty(sseEmitterMap)) {
            TOKEN_SSE_EMITTER_POOL.remove(userId);
            return null;
        } else {
            return sseEmitterMap;
        }
    }

    /**
     * 根据用户ID和用户登录会话Token获取Sse连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     */
    public SseEmitter getSseEmitter(Long userId, String token) {
        if (ObjectUtils.anyNull(userId, token)) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "用户ID和凭证不能为空");
        }
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_POOL.get(userId);
        if (MapUtils.isEmpty(sseEmitterMap)) {
            TOKEN_SSE_EMITTER_POOL.remove(userId);
            return null;
        } else {
            return sseEmitterMap.get(token);
        }
    }

    /**
     * 建立SSE连接
     */
    public SseEmitter connect() {
        return connect(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken(), null);
    }

    /**
     * 建立SSE连接
     *
     * @param timeout   连接超时时间
     */
    public SseEmitter connect(Long timeout) {
        return connect(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken(), timeout);
    }

    /**
     * 建立SSE连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     */
    public SseEmitter connect(Long userId, String token) {
        return connect(userId, token, null);
    }

    /**
     * 建立SSE连接
     *
     * @param userId    用户ID
     * @param token     用户登录会话Token
     * @param timeout   连接超时时间
     */
    public SseEmitter connect(Long userId, String token, Long timeout) {
        if (ObjectUtils.anyNull(userId, token)) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "用户ID和凭证不能为空");
        }
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_POOL.computeIfAbsent(userId, key -> new ConcurrentHashMap<>());
        SseEmitter sseEmitter = new SseEmitter(Objects.isNull(timeout) || timeout <= 0 ? sseProperties.getTimeout() : timeout);
        sseEmitterMap.put(token, sseEmitter);
        sseEmitter.onCompletion(() -> {
            sseEmitterMap.remove(token);
            log.info("SSE连接已断开，用户[ id:{} | token:{} ]离线", userId, Objects.isNull(token) ? "ALL" : token);
        });
        sseEmitter.onTimeout(() -> {
            sseEmitterMap.remove(token);
            log.error("SSE连接超时，用户[ id:{} | token:{} ]离线", userId, token);
        });
        sseEmitter.onError(error -> {
            sseEmitterMap.remove(token);
            log.error("SSE传输异常，用户[ id:{} | token:{} ]离线", userId, token);
        });
        try {
            sseEmitter.send(new SseMessage().setStatus(SseStatus.CONNECTED.getName()));
            log.info("SSE连接已建立，用户[ id:{} | token:{} ]上线", userId, token);
        } catch (IOException e) {
            sseEmitterMap.remove(token);
            throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
        }
        return sseEmitter;
    }

    /**
     * 切断SSE连接
     */
    public void disconnect() {
        disconnect(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken());
    }

    /**
     * 切断SSE连接
     *
     * @param userId 用户ID
     * @param token  用户登录会话Token
     */
    public void disconnect(Long userId, String token) {
        if (ObjectUtils.anyNull(userId, token)) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "用户ID和凭证不能为空");
        }
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_POOL.get(userId);
        if (MapUtils.isNotEmpty(sseEmitterMap)) {
            try {
                SseEmitter sseEmitter = sseEmitterMap.get(token);
                sseEmitter.send(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName()));
                sseEmitter.complete();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            sseEmitterMap.remove(token);
        } else {
            TOKEN_SSE_EMITTER_POOL.remove(userId);
        }
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStreamToCurrentUser(Stream<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), null, Flux.fromStream(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStreamToCurrentToken(Stream<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken(), Flux.fromStream(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendStream(Long userId, Stream<String> messages) {
        send0(userId, null, Flux.fromStream(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendStream(Long userId, String token, Stream<String> messages) {
        send0(userId, token, Flux.fromStream(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendStream(Long userId, String token, Stream<String> messages, Boolean isDisconnect) {
        send0(userId, token, Flux.fromStream(messages), isDisconnect);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStringsToCurrentUser(List<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), null, Flux.fromIterable(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStringsToCurrentToken(List<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken(), Flux.fromIterable(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendStrings(Long userId, List<String> messages) {
        send0(userId, null, Flux.fromIterable(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendStrings(Long userId, String token, List<String> messages) {
        send0(userId, token, Flux.fromIterable(messages), false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendStrings(Long userId, String token, List<String> messages, Boolean isDisconnect) {
        send0(userId, token, Flux.fromIterable(messages), isDisconnect);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendMessagesToCurrentUser(Flux<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), null, messages, false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendFluxToCurrentToken(Flux<String> messages) {
        send0(LoginUtils.getLoginUserIdOrThrow(), LoginUtils.getLoginUserToken(), messages, false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendFlux(Long userId, Flux<String> messages) {
        send0(userId, null, messages, false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendFlux(Long userId, String token, Flux<String> messages) {
        send0(userId, token, messages, false);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendFlux(Long userId, String token, Flux<String> messages, Boolean isDisconnect) {
        send0(userId, token, messages, isDisconnect);
    }

    /**
     * 向凭证连接池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    private void send0(Long userId, String token, Flux<String> messages, Boolean isDisconnect) {
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_POOL.get(userId);
        if (MapUtils.isNotEmpty(sseEmitterMap)) {
            if (Objects.isNull(token)) {
                messages.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                        .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                        .doOnError(e -> {
                            log.error(e.getMessage());
                            throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                        })
                        .doFinally(signalType -> {
                            try {
                                if (Objects.nonNull(isDisconnect) && isDisconnect) {
                                    for (SseEmitter sseEmitter : sseEmitterMap.values()) {
                                        sseEmitter.send(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName()));
                                        sseEmitter.complete();
                                    }
                                }
                            } catch (IOException e) {
                                log.error(e.getMessage());
                                throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                            }
                        })
                        .subscribe(message -> {
                            try {
                                for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitterMap.entrySet()) {
                                    sseEmitterEntry.getValue().send(message);
                                }
                            } catch (IOException e) {
                                log.error(e.getMessage());
                                throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                            }
                        });
            } else {
                SseEmitter sseEmitter = sseEmitterMap.get(token);
                if (Objects.isNull(sseEmitter)) {
                    throw new CustomizeReturnException(ReturnCode.FAIL, "用户凭证连接池中无此凭证");
                } else {
                    messages.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                            .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                            .doOnError(e -> {
                                log.error(e.getMessage());
                                throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                            })
                            .doFinally(signalType -> {
                                try {
                                    if (Objects.nonNull(isDisconnect) && isDisconnect) {
                                        sseEmitter.send(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName()));
                                        sseEmitter.complete();
                                    }
                                } catch (IOException e) {
                                    log.error(e.getMessage());
                                    throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                                }
                            })
                            .subscribe(message -> {
                                try {
                                    sseEmitter.send(message);
                                } catch (IOException e) {
                                    log.error(e.getMessage());
                                    throw new CustomizeReturnException(ReturnCode.FAIL, "数据传输异常");
                                }
                            });
                }
            }
        } else {
            log.warn("SSE连接已断开，用户[ id:{} | token:{} ]离线", userId, token);
            TOKEN_SSE_EMITTER_POOL.remove(userId);
            throw new CustomizeReturnException(ReturnCode.FAIL, "连接已断开");
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
