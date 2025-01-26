package top.sharehome.springbootinittemplate.config.sse;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
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
     * 用户凭证池
     */
    private final static Map<Long, Map<String, SseEmitter>> TOKEN_SSE_EMITTER_MAP = new ConcurrentHashMap<>();

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseStream(Stream<String> messages) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        return quickSseMessages(messagelist, null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接超时时间
     */
    public SseEmitter quickSseStream(Stream<String> messages, Long timeout) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        return quickSseMessages(messagelist, timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseStrings(List<String> messages) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        return quickSseMessages(messagelist, null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接超时时间
     */
    public SseEmitter quickSseStrings(List<String> messages, Long timeout) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        return quickSseMessages(messagelist, timeout);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     */
    public SseEmitter quickSseMessages(List<SseMessage> messages) {
        return quickSseMessages(messages, null);
    }

    /**
     * 快速建立SSE，并发送消息
     *
     * @param messages      消息内容
     * @param timeout       SSE连接超时时间
     */
    public SseEmitter quickSseMessages(List<SseMessage> messages, Long timeout) {
        SseEmitter sseEmitter = new SseEmitter(Objects.isNull(timeout) || timeout <= 0L ? 0L : timeout);
        sseEmitter.onCompletion(sseEmitter::complete);
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onError(error -> sseEmitter.complete());
        try {
            // 建立连接
            sseEmitter.send(new SseMessage().setStatus(SseStatus.CONNECTED.getName()));
            // 预处理消息
            this.handleMessages(messages);
            // 发送消息
            for (SseMessage message : messages) {
                sseEmitter.send(message);
            }
            // 断开连接
            sseEmitter.send(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName()));
            sseEmitter.complete();
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "SSE数据传输异常");
        }
        return sseEmitter;
    }

    /**
     * 建立SSE连接
     */
    public SseEmitter connect() {
        return connect(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken(), null);
    }

    /**
     * 建立SSE连接
     *
     * @param timeout   连接超时时间
     */
    public SseEmitter connect(Long timeout) {
        return connect(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken(), timeout);
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
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_MAP.computeIfAbsent(userId, key -> new ConcurrentHashMap<>());
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
            throw new CustomizeReturnException(ReturnCode.FAIL, "SSE数据传输异常");
        }
        return sseEmitter;
    }

    /**
     * 切断SSE连接
     */
    public void disconnect() {
        disconnect(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken());
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
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_MAP.get(userId);
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
            TOKEN_SSE_EMITTER_MAP.remove(userId);
        }
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStreamToCurrentUser(Stream<String> messages) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        send0(LoginUtils.getLoginUserId(), null, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStreamToCurrentToken(Stream<String> messages) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        send0(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken(), messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendStream(Long userId, Stream<String> messages) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        send0(userId, null, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendStream(Long userId, String token, Stream<String> messages) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        send0(userId, token, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendStream(Long userId, String token, Stream<String> messages, Boolean isDisconnect) {
        List<SseMessage> messagelist = messages.map(str -> new SseMessage().setData(str)).toList();
        send0(userId, token, messagelist, isDisconnect);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStringsToCurrentUser(List<String> messages) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        send0(LoginUtils.getLoginUserId(), null, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendStringsToCurrentToken(List<String> messages) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        send0(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken(), messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendStrings(Long userId, List<String> messages) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        send0(userId, null, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendStrings(Long userId, String token, List<String> messages) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        send0(userId, token, messagelist, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendStrings(Long userId, String token, List<String> messages, Boolean isDisconnect) {
        List<SseMessage> messagelist = messages.stream().map(str -> new SseMessage().setData(str)).toList();
        send0(userId, token, messagelist, isDisconnect);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendMessagesToCurrentUser(List<SseMessage> messages) {
        send0(LoginUtils.getLoginUserId(), null, messages, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param messages      消息内容
     */
    public void sendMessagesToCurrentToken(List<SseMessage> messages) {
        send0(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken(), messages, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param messages      消息内容
     */
    public void sendMessages(Long userId, List<SseMessage> messages) {
        send0(userId, null, messages, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     */
    public void sendMessages(Long userId, String token, List<SseMessage> messages) {
        send0(userId, token, messages, false);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    public void sendMessages(Long userId, String token, List<SseMessage> messages, Boolean isDisconnect) {
        send0(userId, token, messages, isDisconnect);
    }

    /**
     * 向凭证池中的用户会话发送消息
     *
     * @param userId        用户ID
     * @param token         用户登录会话Token，如果为null则代表向userId用户所有会话发送消息
     * @param messages      消息内容
     * @param isDisconnect  是否需要断开连接，如果为null或者false，则表示不需要在发送消息后断开连接
     */
    private void send0(Long userId, String token, List<SseMessage> messages, Boolean isDisconnect) {
        if (CollectionUtils.isEmpty(messages)) {
            return;
        }
        Map<String, SseEmitter> sseEmitterMap = TOKEN_SSE_EMITTER_MAP.get(userId);
        if (MapUtils.isNotEmpty(sseEmitterMap)) {
            // 预处理消息
            this.handleMessages(messages);
            // 发送消息
            if (Objects.isNull(token)) {
                for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitterMap.entrySet()) {
                    SseEmitter sseEmitter = sseEmitterEntry.getValue();
                    this.sendMessages(userId, null, sseEmitter, messages, isDisconnect);
                }
            } else {
                SseEmitter sseEmitter = sseEmitterMap.get(token);
                this.sendMessages(userId, token, sseEmitter, messages, isDisconnect);
            }
        } else {
            log.warn("SSE连接已断开，用户[ id:{} | token:{} ]离线", userId, token);
            TOKEN_SSE_EMITTER_MAP.remove(userId);
            throw new CustomizeReturnException(ReturnCode.FAIL, "连接已断开");
        }
    }

    /**
     * 预处理消息
     */
    private void handleMessages(List<SseMessage> messages) {
        if (Objects.equals(messages.size(), 1)) {
            messages.get(0).setStatus(SseStatus.START.getName());
            messages.add(new SseMessage().setStatus(SseStatus.FINISH.getName()));
        } else {
            for (int i = 0; i < messages.size(); i++) {
                if (i == 0) {
                    messages.get(i).setStatus(SseStatus.START.getName());
                } else if (i != messages.size() - 1) {
                    messages.get(i).setStatus(SseStatus.PROCESS.getName());
                } else {
                    messages.get(i).setStatus(SseStatus.FINISH.getName());
                }
            }
        }
    }

    /**
     * 发送消息
     */
    private void sendMessages(Long userId, String token, SseEmitter sseEmitter, List<SseMessage> messages, Boolean isDisconnect) {
        try {
            for (SseMessage message : messages) {
                sseEmitter.send(message);
            }
            if (Objects.nonNull(isDisconnect) && isDisconnect) {
                sseEmitter.send(new SseMessage().setStatus(SseStatus.DISCONNECTED.getName()));
                sseEmitter.complete();
            }
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "SSE数据传输异常");
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
