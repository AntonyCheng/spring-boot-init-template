package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.manager;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionModel;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import org.springframework.ai.model.Model;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.AzureOpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.OpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.util.Objects;

/**
 * AI Transcription管理器
 *
 * @author AntonyCheng
 */
public class TranscriptionManager {

    /**
     * 获取图像响应对象
     */
    public static Model<AudioTranscriptionPrompt, AudioTranscriptionResponse> getImageModel(TranscriptionModelBase model) {
        if (model instanceof OpenAiTranscriptionEntity entity) {
            return getOpenAiAudioTranscriptionModel(entity);
        } else if (model instanceof AzureOpenAiTranscriptionEntity entity) {
            return getAzureOpenAiAudioTranscriptionModel(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiAudioTranscriptionModel
     */
    private static OpenAiAudioTranscriptionModel getOpenAiAudioTranscriptionModel(OpenAiTranscriptionEntity entity) {
        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        return new OpenAiAudioTranscriptionModel(openAiAudioApi, OpenAiAudioTranscriptionOptions.builder()
                .language(Objects.isNull(entity.getLanguage()) ? null : entity.getLanguage().getValue())
                .model(entity.getOpenAiTranscriptionType().getTranscriptionModel())
                .temperature(entity.getTemperature())
                .responseFormat(entity.getFormat())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取AzureOpenAiAudioTranscriptionModel
     */
    private static AzureOpenAiAudioTranscriptionModel getAzureOpenAiAudioTranscriptionModel(AzureOpenAiTranscriptionEntity entity) {
        OpenAIClient openAiClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .buildClient();
        return new AzureOpenAiAudioTranscriptionModel(openAiClient, AzureOpenAiAudioTranscriptionOptions.builder()
                .language(Objects.isNull(entity.getLanguage()) ? null : entity.getLanguage().getValue())
                .deploymentName(entity.getAzureOpenAiTranscriptionType().getTranscriptionModel())
                .temperature(entity.getTemperature())
                .responseFormat(entity.getFormat())
                .build());
    }

}
