package top.sharehome.springbootinittemplate.config.ai.spring.service.tts;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsResult;

import java.io.OutputStream;

/**
 * AI TTS服务接口
 *
 * @author AntonyCheng
 */
public interface AiTtsService {

    /**
     * AI TTS功能
     *
     * @param model tts模型信息
     * @param text  文本内容
     */
    byte[] speechBytes(TtsModelBase model, String text);

    /**
     * AI TTS功能
     *
     * @param model tts模型信息
     * @param text  文本内容
     */
    OutputStream speechStream(TtsModelBase model, String text);

    /**
     * AI TTS功能
     *
     * @param sseEmitter    Sse连接器
     * @param model         tts模型信息
     * @param text          文本内容
     */
    TtsResult speechFlux(SseEmitter sseEmitter, TtsModelBase model, String text);

    /**
     * AI TTS功能
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param text          文本内容
     */
    TtsResult speechFlux(Long userId, TtsModelBase model, String text);

    /**
     * AI TTS功能
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param text          文本内容
     */
    TtsResult speechFlux(Long userId, String token, TtsModelBase model, String text);

}
