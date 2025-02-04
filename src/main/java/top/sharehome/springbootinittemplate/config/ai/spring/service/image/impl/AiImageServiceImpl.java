package top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.zhipuai.ZhiPuAiImageModel;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiImageApi;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.AiImageService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.OpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.ZhiPuAiImageEntity;

/**
 * AI Image服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiImageServiceImpl implements AiImageService {

    /**
     * 获取OpenAiImageModel
     */
    private OpenAiImageModel getOpenAiImageModel(OpenAiImageEntity openAiImageEntity) {
        OpenAiImageApi openAiImageApi = new OpenAiImageApi(openAiImageEntity.getBaseUrl(), openAiImageEntity.getApiKey(), RestClient.builder());
        return new OpenAiImageModel(openAiImageApi, OpenAiImageOptions.builder()
                .model(openAiImageEntity.getOpenAiImageType().getImageModel().getValue())
                .quality(openAiImageEntity.getOpenAiImageType().getQuality())
                .N(openAiImageEntity.getN())
                .height(openAiImageEntity.getOpenAiImageType().getHeight())
                .width(openAiImageEntity.getOpenAiImageType().getWidth())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取ZhiPuAiImageModel
     */
    private ZhiPuAiImageModel getZhiPuAiImageModel(ZhiPuAiImageEntity zhiPuAiImageEntity) {
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(zhiPuAiImageEntity.getApiKey());
        return new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(zhiPuAiImageEntity.getModel())
                .user(StringUtils.isBlank(zhiPuAiImageEntity.getUser()) ? null : zhiPuAiImageEntity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

}
