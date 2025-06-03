package top.sharehome.springbootinittemplate.ai;

import com.aliyun.core.utils.Base64Util;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl.AiImageServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.*;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * Spring AI Image测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class SpringImageTest {

    @Resource
    private AiImageServiceImpl aiImageService;

    @Test
    public void testOpenAiImage() {
        OpenAiImageEntity entity = new OpenAiImageEntity(OpenAiImageType.DallE2_256_256, "sk-xxx");
        OpenAiImageApi openAiImageApi = OpenAiImageApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        OpenAiImageModel imageModel = new OpenAiImageModel(openAiImageApi, OpenAiImageOptions.builder()
                .model(entity.getOpenAiImageType().getImageModel())
                .quality(entity.getOpenAiImageType().getQuality())
                .N(entity.getN())
                .height(entity.getOpenAiImageType().getHeight())
                .width(entity.getOpenAiImageType().getWidth())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("a dog and two cat"));
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                try {
                    String b64Json = result.getOutput().getB64Json();
                    byte[] bytes = Base64Util.decodeString(b64Json);
                    Path png = Files.createTempFile(UUID.randomUUID().toString(), ".png");
                    Files.write(png, bytes);
                    System.out.println(png);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String url = result.getOutput().getUrl();
                System.out.println(url);
            }
        }
    }

    @Test
    public void testAzureOpenaiImage() {
        AzureOpenAiImageEntity entity = new AzureOpenAiImageEntity(AzureOpenAiImageType.DallE3_1024_1024, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        OpenAIClient openAiClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .buildClient();
        AzureOpenAiImageModel imageModel = new AzureOpenAiImageModel(openAiClient, AzureOpenAiImageOptions.builder()
                .deploymentName(entity.getAzureOpenAiImageType().getImageModel())
                .N(1)
                .height(entity.getAzureOpenAiImageType().getHeight())
                .width(entity.getAzureOpenAiImageType().getWidth())
                .build());
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("a dog and two cat"));
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                try {
                    String b64Json = result.getOutput().getB64Json();
                    byte[] bytes = Base64Util.decodeString(b64Json);
                    Path png = Files.createTempFile(UUID.randomUUID().toString(), ".png");
                    Files.write(png, bytes);
                    System.out.println(png);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String url = result.getOutput().getUrl();
                System.out.println(url);
            }
        }
    }

    @Test
    public void testStabilityAiImage() {
        StabilityAiImageEntity stabilityAiImageEntity = new StabilityAiImageEntity(StabilityAiImageType.SD_1_6_1344_768, "sk-xxx");
        StabilityAiApi stabilityAiApi = new StabilityAiApi(stabilityAiImageEntity.getApiKey());
        StabilityAiImageModel imageModel = new StabilityAiImageModel(stabilityAiApi, StabilityAiImageOptions.builder()
                .model(stabilityAiImageEntity.getStabilityAiImageType().getImageModel())
                .N(stabilityAiImageEntity.getN())
                .height(stabilityAiImageEntity.getStabilityAiImageType().getHeight())
                .width(stabilityAiImageEntity.getStabilityAiImageType().getWidth())
                .stylePreset(Objects.isNull(stabilityAiImageEntity.getStyleEnum()) ? null : stabilityAiImageEntity.getStyleEnum().toString())
                .build());
        // 如果使用Stability官方的APIkey，则不支持中文提问
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("a dog and two cat"));
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                try {
                    String b64Json = result.getOutput().getB64Json();
                    byte[] bytes = Base64Util.decodeString(b64Json);
                    Path png = Files.createTempFile(UUID.randomUUID().toString(), ".png");
                    Files.write(png, bytes);
                    System.out.println(png);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String url = result.getOutput().getUrl();
                System.out.println(url);
            }
        }
    }

    @Test
    public void testZhiPuAiImage() {
        ZhiPuAiImageEntity zhiPuAiImageEntity = new ZhiPuAiImageEntity(ZhiPuAiImageType.CogView_3_Flash, "xxx.xxx", "xxx");
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(zhiPuAiImageEntity.getApiKey());
        ZhiPuAiImageModel imageModel = new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(zhiPuAiImageEntity.getZhiPuAiImageType().getImageModel())
                .user(StringUtils.isBlank(zhiPuAiImageEntity.getUser()) ? null : zhiPuAiImageEntity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
        ImageResponse imageResponse = imageModel.call(new ImagePrompt("a dog and two cat"));
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                try {
                    String b64Json = result.getOutput().getB64Json();
                    byte[] bytes = Base64Util.decodeString(b64Json);
                    Path png = Files.createTempFile(UUID.randomUUID().toString(), ".png");
                    Files.write(png, bytes);
                    System.out.println(png);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String url = result.getOutput().getUrl();
                System.out.println(url);
            }
        }
    }

    @Test
    public void testImageToTempUrl() {
        OpenAiImageEntity openAiImageEntity = new OpenAiImageEntity(OpenAiImageType.DallE2_256_256, "sk-xxx");
        StabilityAiImageEntity stabilityAiImageEntity = new StabilityAiImageEntity(StabilityAiImageType.SD_1_6_1344_768, "sk-xxx");
        ZhiPuAiImageEntity zhiPuAiImageEntity = new ZhiPuAiImageEntity(ZhiPuAiImageType.CogView_3_Flash, "xxx.xxx", "xxx");
        QianFanImageEntity qianFanImageEntity = new QianFanImageEntity(QianFanImageType.SD_XL_1024_1024, "xxx", "xxx");
        AzureOpenAiImageEntity azureOpenAiImageEntity = new AzureOpenAiImageEntity(AzureOpenAiImageType.DallE3_1024_1024, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        String prompt = "a dog and two cat";
        System.out.println(aiImageService.imageToTempUrl(openAiImageEntity, prompt));
        System.out.println(aiImageService.imageToTempUrl(stabilityAiImageEntity, prompt));
        System.out.println(aiImageService.imageToTempUrl(zhiPuAiImageEntity, prompt));
        System.out.println(aiImageService.imageToTempUrl(qianFanImageEntity, prompt));
        System.out.println(aiImageService.imageToTempUrl(azureOpenAiImageEntity, prompt));
    }

    @Test
    public void testImageToByteArray() {
        OpenAiImageEntity openAiImageEntity = new OpenAiImageEntity(OpenAiImageType.DallE2_256_256, "sk-xxx");
        StabilityAiImageEntity stabilityAiImageEntity = new StabilityAiImageEntity(StabilityAiImageType.SD_1_6_1344_768, "sk-xxx");
        ZhiPuAiImageEntity zhiPuAiImageEntity = new ZhiPuAiImageEntity(ZhiPuAiImageType.CogView_3_Flash, "xxx.xxx", "xxx");
        QianFanImageEntity qianFanImageEntity = new QianFanImageEntity(QianFanImageType.SD_XL_1024_1024, "xxx", "xxx");
        AzureOpenAiImageEntity azureOpenAiImageEntity = new AzureOpenAiImageEntity(AzureOpenAiImageType.DallE3_1024_1024, "xxx", "https://xxx-xxx-swedencentral.cognitiveservices.azure.com/");
        String prompt = "a dog and two cat";
        System.out.println(Arrays.toString(aiImageService.imageToByteArray(openAiImageEntity, prompt).get(0)));
        System.out.println(Arrays.toString(aiImageService.imageToByteArray(stabilityAiImageEntity, prompt).get(0)));
        System.out.println(Arrays.toString(aiImageService.imageToByteArray(zhiPuAiImageEntity, prompt).get(0)));
        System.out.println(Arrays.toString(aiImageService.imageToByteArray(qianFanImageEntity, prompt).get(0)));
        System.out.println(Arrays.toString(aiImageService.imageToByteArray(azureOpenAiImageEntity, prompt).get(0)));
    }

}
