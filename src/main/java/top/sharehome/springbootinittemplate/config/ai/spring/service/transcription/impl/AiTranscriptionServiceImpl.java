package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.impl;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.bouncycastle.util.Arrays;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionModel;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.AiTranscriptionService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.TranscriptionModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.AzureOpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.OpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.*;
import java.util.Objects;

/**
 * AI Transcription服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiTranscriptionServiceImpl implements AiTranscriptionService {

    @Override
    public String transcribe(TranscriptionModelBase model, File file) {
        if (Objects.isNull(file) || !file.exists()) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]不能为空");
        }
        try (FileInputStream inputStream = new FileInputStream(file);) {
            if (model instanceof OpenAiTranscriptionEntity entity) {
                return this.getOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else if (model instanceof AzureOpenAiTranscriptionEntity entity) {
                return this.getAzureOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else {
                throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
            }
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]不能为空");
        }
        try (InputStream inputStream = file.getInputStream()) {
            if (model instanceof OpenAiTranscriptionEntity entity) {
                return this.getOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else if (model instanceof AzureOpenAiTranscriptionEntity entity) {
                return this.getAzureOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else {
                throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
            }
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[file]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, byte[] bytes) {
        if (Arrays.isNullOrEmpty(bytes)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[bytes]不能为空");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            if (model instanceof OpenAiTranscriptionEntity entity) {
                return this.getOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else if (model instanceof AzureOpenAiTranscriptionEntity entity) {
                return this.getAzureOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
            } else {
                throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
            }
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[bytes]存在异常");
        }
    }

    @Override
    public String transcribe(TranscriptionModelBase model, InputStream inputStream) {
        if (Objects.isNull(inputStream)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[inputStream]不能为空");
        }
        if (model instanceof OpenAiTranscriptionEntity entity) {
            return this.getOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
        } else if (model instanceof AzureOpenAiTranscriptionEntity entity) {
            return this.getAzureOpenAiAudioTranscriptionModel(entity).call(new InputStreamResource(inputStream));
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiAudioTranscriptionModel
     */
    private OpenAiAudioTranscriptionModel getOpenAiAudioTranscriptionModel(OpenAiTranscriptionEntity entity) {
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
    private AzureOpenAiAudioTranscriptionModel getAzureOpenAiAudioTranscriptionModel(AzureOpenAiTranscriptionEntity entity) {
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
