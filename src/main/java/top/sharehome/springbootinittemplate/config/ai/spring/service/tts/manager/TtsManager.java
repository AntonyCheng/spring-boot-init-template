package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.manager;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.entity.OpenAiTtsEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

/**
 * AI Transcription管理器
 *
 * @author AntonyCheng
 */
public class TtsManager {

    public static OpenAiAudioSpeechModel getTtsModel(TtsModelBase model) {
        if (model instanceof OpenAiTtsEntity entity) {
            return getOpenAiAudioSpeechModel(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiAudioSpeechModel
     */
    private static OpenAiAudioSpeechModel getOpenAiAudioSpeechModel(OpenAiTtsEntity entity) {
        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        return new OpenAiAudioSpeechModel(openAiAudioApi, OpenAiAudioSpeechOptions.builder()
                .model(entity.getOpenAiTtsType().getTtsModel())
                .voice(entity.getVoice())
                .responseFormat(entity.getFormat())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

}
