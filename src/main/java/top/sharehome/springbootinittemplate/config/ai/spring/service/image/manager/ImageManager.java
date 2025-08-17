package top.sharehome.springbootinittemplate.config.ai.spring.service.image.manager;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.util.HttpClientOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
import org.springframework.ai.image.ImageModel;
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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.AzureOpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.OpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.StabilityAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.ZhiPuAiImageEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.time.Duration;
import java.util.Objects;

/**
 * AI Image管理器
 *
 * @author AntonyCheng
 */
public class ImageManager {

    /**
     * 获取图像响应对象
     */
    public static ImageModel getImageModel(ImageModelBase model) {
        if (model instanceof OpenAiImageEntity entity) {
            return getOpenAiImageModel(entity);
        } else if (model instanceof StabilityAiImageEntity entity) {
            return getStabilityAiImageModel(entity);
        } else if (model instanceof ZhiPuAiImageEntity entity) {
            return getZhiPuAiImageModel(entity);
        } else if (model instanceof AzureOpenAiImageEntity entity) {
            return getAzureOpenAiImageModel(entity);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    /**
     * 获取OpenAiImageModel
     */
    private static OpenAiImageModel getOpenAiImageModel(OpenAiImageEntity entity) {
        OpenAiImageApi openAiImageApi = OpenAiImageApi.builder()
                .restClientBuilder(getRestClient(entity))
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        return new OpenAiImageModel(openAiImageApi, OpenAiImageOptions.builder()
                .model(entity.getModel())
                .quality(entity.getQuality())
                .N(entity.getN())
                .height(entity.getHeight())
                .width(entity.getWidth())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取AzureOpenAiImageModel
     */
    private static AzureOpenAiImageModel getAzureOpenAiImageModel(AzureOpenAiImageEntity entity) {
        OpenAIClient openAiClient = new OpenAIClientBuilder()
                .httpClient(HttpClient.createDefault(
                        new HttpClientOptions().setReadTimeout(Duration.ofMillis(entity.getReadTimeout()))
                ))
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .buildClient();
        return new AzureOpenAiImageModel(openAiClient, AzureOpenAiImageOptions.builder()
                .deploymentName(entity.getModel())
                .N(entity.getN())
                .height(entity.getHeight())
                .width(entity.getWidth())
                .build());
    }

    /**
     * 获取StabilityAiImageModel
     *
     * @apiNote 如果直接使用Stability官方的模型，则不支持仅包含中文的提示词（会触发审核系统），推荐直接使用英文提示词
     */
    private static StabilityAiImageModel getStabilityAiImageModel(StabilityAiImageEntity entity) {
        StabilityAiApi stabilityAiApi = new StabilityAiApi(
                entity.getApiKey(),
                entity.getModel(),
                entity.getBaseUrl(),
                getRestClient(entity)
        );
        return new StabilityAiImageModel(stabilityAiApi, StabilityAiImageOptions.builder()
                .model(entity.getModel())
                .N(entity.getN())
                .height(entity.getHeight())
                .width(entity.getWidth())
                .stylePreset(Objects.isNull(entity.getStyleEnum()) ? null : entity.getStyleEnum().toString())
                .build());
    }

    /**
     * 获取ZhiPuAiImageModel
     */
    private static ZhiPuAiImageModel getZhiPuAiImageModel(ZhiPuAiImageEntity entity) {
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(entity.getApiKey());
        return new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(entity.getModel())
                .user(StringUtils.isBlank(entity.getUser()) ? null : entity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取请求构造类
     */
    private static RestClient.Builder getRestClient(ImageModelBase model) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (Objects.nonNull(model.getReadTimeout())) {
            requestFactory.setReadTimeout(Duration.ofMillis(model.getReadTimeout()));
        }
        return RestClient.builder().requestFactory(requestFactory);
    }

}
