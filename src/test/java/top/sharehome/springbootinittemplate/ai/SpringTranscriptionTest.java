package top.sharehome.springbootinittemplate.ai;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionModel;
import org.springframework.ai.azure.openai.AzureOpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.AiTranscriptionService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.AzureOpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.entity.OpenAiTranscriptionEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.AzureOpenAiTranscriptionType;
import top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums.OpenAiTranscriptionType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Spring AI Transcription测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class SpringTranscriptionTest {

    @Resource
    private AiTranscriptionService aiTranscriptionService;

    @Test
    public void testOpenAiTranscription() throws FileNotFoundException {
        OpenAiTranscriptionEntity entity = new OpenAiTranscriptionEntity(OpenAiTranscriptionType.Whisper, "sk-xxx");
        OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        OpenAiAudioTranscriptionModel model = new OpenAiAudioTranscriptionModel(openAiAudioApi, OpenAiAudioTranscriptionOptions.builder()
                .language(Objects.isNull(entity.getLanguage()) ? null : entity.getLanguage().getValue())
                .model(entity.getOpenAiTranscriptionType().getModel())
                .temperature(entity.getTemperature().floatValue())
                .responseFormat(entity.getFormat())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
        InputStreamResource resource = new InputStreamResource(new FileInputStream("D:\\tts.mp3"));
        System.out.println(model.call(resource));
    }

    @Test
    public void testAzureOpenAiTranscription() throws FileNotFoundException {
        AzureOpenAiTranscriptionEntity entity = new AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType.Whisper, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        OpenAIClient openAiClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .buildClient();
        AzureOpenAiAudioTranscriptionModel model = new AzureOpenAiAudioTranscriptionModel(openAiClient, AzureOpenAiAudioTranscriptionOptions.builder()
                .language(Objects.isNull(entity.getLanguage()) ? null : entity.getLanguage().getValue())
                .deploymentName(entity.getAzureOpenAiTranscriptionType().getModel())
                .temperature(entity.getTemperature().floatValue())
                .responseFormat(entity.getFormat())
                .build());
        InputStreamResource resource = new InputStreamResource(new FileInputStream("D:\\tts.mp3"));
        System.out.println(model.call(resource));
    }

    @Test
    public void testTranscribe() throws FileNotFoundException {
        OpenAiTranscriptionEntity openAiTranscriptionEntity = new OpenAiTranscriptionEntity(OpenAiTranscriptionType.Whisper, "sk-xxx");
        AzureOpenAiTranscriptionEntity azureOpenAiTranscriptionEntity = new AzureOpenAiTranscriptionEntity(AzureOpenAiTranscriptionType.Whisper, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        System.out.println(aiTranscriptionService.transcribe(openAiTranscriptionEntity, new FileInputStream("D:\\tts.mp3"), "tts.mp3"));
        System.out.println(aiTranscriptionService.transcribe(azureOpenAiTranscriptionEntity, new FileInputStream("D:\\tts.mp3"), "tts.mp3"));
    }

}
