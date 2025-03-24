package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.AiTtsService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.TtsModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.entity.OpenAiTtsEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

/**
 * AI TTS服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiTtsServiceImpl implements AiTtsService {

    @Override
    public byte[] toSpeech(TtsModelBase model, String text) {
        if (StringUtils.isBlank(text)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[text]不能为空");
        }
        if (model instanceof OpenAiTtsEntity entity) {
           return this.getOpenAiAudioSpeechModel(entity).call(text);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiAudioSpeechModel
     */
    private OpenAiAudioSpeechModel getOpenAiAudioSpeechModel(OpenAiTtsEntity entity) {
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
