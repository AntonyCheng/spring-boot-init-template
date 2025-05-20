package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.AiTtsService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.manager.TtsManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.entity.OpenAiTtsEntity;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;
import top.sharehome.springbootinittemplate.config.sse.enums.SseStatus;
import top.sharehome.springbootinittemplate.config.sse.utils.SseUtils;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * AI TTS服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
@Slf4j
public class AiTtsServiceImpl implements AiTtsService {

    @Override
    public byte[] speechBytes(TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        if (model instanceof OpenAiTtsEntity entity) {
            return TtsManager.getTtsModel(entity).call(text);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public OutputStream speechStream(TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        try {
            if (model instanceof OpenAiTtsEntity entity) {
                ByteArrayOutputStream res = new ByteArrayOutputStream();
                res.write(TtsManager.getTtsModel(entity).call(text));
                return res;
            } else {
                throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TtsResult speechFlux(SseEmitter sseEmitter, TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "参数[sseEmitter]不能为空");
        }
        // 结果总汇
        AtomicReference<byte[]> result = new AtomicReference<>(null);
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<byte[]> flux;
        if (model instanceof OpenAiTtsEntity entity) {
            flux = TtsManager.getTtsModel(entity).stream(text)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        byte[] messageData = (byte[]) message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(ArrayUtils.addAll(result.get(), messageData));
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new TtsResult(result.get(), takeTime.get(), text);
    }

    @Override
    public TtsResult speechFlux(Long userId, TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        if (!LoginUtils.isLogin(userId)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未登录");
        }
        Map<String, SseEmitter> sseEmitters = SseUtils.getSseEmitter(userId);
        if (Objects.isNull(sseEmitters)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未连接");
        }
        // 结果总汇
        AtomicReference<byte[]> result = new AtomicReference<>(null);
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<byte[]> flux;
        if (model instanceof OpenAiTtsEntity entity) {
            flux = TtsManager.getTtsModel(entity).stream(text)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        byte[] messageData = (byte[]) message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(ArrayUtils.addAll(result.get(), messageData));
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new TtsResult(result.get(), takeTime.get(), text);
    }

    @Override
    public TtsResult speechFlux(Long userId, String token, TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        if (!LoginUtils.isLogin(userId)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未登录");
        }
        SseEmitter sseEmitter = SseUtils.getSseEmitter(userId, token);
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + ":" + token + "]未连接");
        }
        // 结果总汇
        AtomicReference<byte[]> result = new AtomicReference<>(null);
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        TtsManager.getTtsModel(model).stream(text)
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    takeTime.set(sw.getDuration().toMillis());
                }).index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        byte[] messageData = (byte[]) message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(ArrayUtils.addAll(result.get(), messageData));
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new TtsResult(result.get(), takeTime.get(), text);
    }

}
