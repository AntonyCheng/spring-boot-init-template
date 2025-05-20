package top.sharehome.springbootinittemplate.config.ai.spring.service.image.manager;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.qianfan.QianFanImageModel;
import org.springframework.ai.qianfan.QianFanImageOptions;
import org.springframework.ai.qianfan.api.QianFanImageApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.*;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

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
        } else if (model instanceof QianFanImageEntity entity) {
            return getQianFanImageModel(entity);
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
                .baseUrl(entity.getBaseUrl())
                .apiKey(entity.getApiKey())
                .restClientBuilder(RestClient.builder())
                .responseErrorHandler(new DefaultResponseErrorHandler())
                .build();
        return new OpenAiImageModel(openAiImageApi, OpenAiImageOptions.builder()
                .model(entity.getOpenAiImageType().getImageModel())
                .quality(entity.getOpenAiImageType().getQuality())
                .N(entity.getN())
                .height(entity.getOpenAiImageType().getHeight())
                .width(entity.getOpenAiImageType().getWidth())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取AzureOpenAiImageModel
     */
    private static AzureOpenAiImageModel getAzureOpenAiImageModel(AzureOpenAiImageEntity entity) {
        OpenAIClient openAiClient = new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(entity.getApiKey()))
                .endpoint(entity.getEndpoint())
                .buildClient();
        return new AzureOpenAiImageModel(openAiClient, AzureOpenAiImageOptions.builder()
                .deploymentName(entity.getAzureOpenAiImageType().getImageModel())
                .N(entity.getN())
                .height(entity.getAzureOpenAiImageType().getHeight())
                .width(entity.getAzureOpenAiImageType().getWidth())
                .build());
    }

    /**
     * 获取StabilityAiImageModel
     *
     * @apiNote 如果直接使用Stability官方的模型，则不支持仅包含中文的提示词（会触发审核系统），推荐直接使用英文提示词
     */
    private static StabilityAiImageModel getStabilityAiImageModel(StabilityAiImageEntity entity) {
        StabilityAiApi stabilityAiApi = new StabilityAiApi(entity.getApiKey(), entity.getStabilityAiImageType().getImageModel(), entity.getBaseUrl(), RestClient.builder());
        return new StabilityAiImageModel(stabilityAiApi, StabilityAiImageOptions.builder()
                .model(entity.getStabilityAiImageType().getImageModel())
                .N(entity.getN())
                .height(entity.getStabilityAiImageType().getHeight())
                .width(entity.getStabilityAiImageType().getWidth())
                .stylePreset(Objects.isNull(entity.getStyleEnum()) ? null : entity.getStyleEnum().toString())
                .build());
    }

    /**
     * 获取ZhiPuAiImageModel
     */
    private static ZhiPuAiImageModel getZhiPuAiImageModel(ZhiPuAiImageEntity entity) {
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(entity.getApiKey());
        return new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(entity.getZhiPuAiImageType().getImageModel())
                .user(StringUtils.isBlank(entity.getUser()) ? null : entity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取QianFanImageModel
     */
    private static QianFanImageModel getQianFanImageModel(QianFanImageEntity entity) {
        QianFanImageApi qianFanImageApi = new QianFanImageApi(entity.getApiKey(), entity.getSecretKey());
        return new QianFanImageModel(qianFanImageApi, QianFanImageOptions.builder()
                .model(entity.getQianFanImageType().getImageModel())
                .N(entity.getN())
                .height(entity.getQianFanImageType().getHeight())
                .width(entity.getQianFanImageType().getWidth())
                .style(Objects.isNull(entity.getStyleEnum()) ? null : entity.getStyleEnum().toString())
                .user(StringUtils.isBlank(entity.getUser()) ? null : entity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

}
