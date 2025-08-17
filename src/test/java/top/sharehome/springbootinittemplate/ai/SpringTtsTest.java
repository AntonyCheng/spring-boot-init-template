package top.sharehome.springbootinittemplate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.AiTtsService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.entity.OpenAiTtsEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums.OpenAiTtsType;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Spring AI TTS测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class SpringTtsTest {

    @Resource
    private AiTtsService aiTtsService;

    @Test
    public void testOpenAiTts() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("D:\\tts.mp3");) {
            OpenAiTtsEntity entity = new OpenAiTtsEntity(OpenAiTtsType.TTS, "sk-xxx", OpenAiAudioApi.SpeechRequest.Voice.NOVA);
            OpenAiAudioApi openAiAudioApi = OpenAiAudioApi.builder()
                    .baseUrl(entity.getBaseUrl())
                    .apiKey(entity.getApiKey())
                    .restClientBuilder(RestClient.builder())
                    .responseErrorHandler(new DefaultResponseErrorHandler())
                    .build();
            OpenAiAudioSpeechModel model = new OpenAiAudioSpeechModel(openAiAudioApi, OpenAiAudioSpeechOptions.builder()
                    .model(entity.getOpenAiTtsType().getModel())
                    .voice(entity.getVoice())
                    .responseFormat(entity.getFormat())
                    .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
            byte[] result = model.call("您好！我是您的人工智能小助手，有什么可以帮助您的么？");
            fileOutputStream.write(result);
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSpeech() {
        try (FileOutputStream fileOutputStream = new FileOutputStream("D:\\tts.mp3");) {
            OpenAiTtsEntity entity = new OpenAiTtsEntity(OpenAiTtsType.TTS, "sk-xxx");
            byte[] result = aiTtsService.speechBytes(entity, "您好！我是您的人工智能小助手，有什么可以帮助您的么？");
            fileOutputStream.write(result);
            fileOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
