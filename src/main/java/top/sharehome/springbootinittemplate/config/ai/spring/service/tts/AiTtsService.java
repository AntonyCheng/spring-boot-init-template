package top.sharehome.springbootinittemplate.config.ai.spring.service.tts;

import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;

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
    byte[] toSpeech(TtsModelBase model,String text);

}
