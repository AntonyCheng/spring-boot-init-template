package top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl;

import com.aliyun.core.utils.Base64Util;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.AiImageService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.OpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.StabilityAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.ZhiPuAiImageEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AI Image服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiImageServiceImpl implements AiImageService {

    @Override
    public List<String> quickImage(ImageModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        ImageResponse imageResponse;
        if (model instanceof OpenAiImageEntity entity) {
            imageResponse = this.getOpenAiImageModel(entity).call(new ImagePrompt(prompt));
        } else if (model instanceof StabilityAiImageEntity entity) {
            imageResponse = this.getStabilityAiImageModel(entity).call(new ImagePrompt(prompt));
        } else if (model instanceof ZhiPuAiImageEntity entity) {
            imageResponse = this.getZhiPuAiImageModel(entity).call(new ImagePrompt(prompt));
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        List<String> res = new ArrayList<>();
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                try {
                    String b64Json = result.getOutput().getB64Json();
                    byte[] bytes = Base64Util.decodeString(b64Json);
                    Path url = Files.createTempFile(UUID.randomUUID().toString(), ".png");
                    Files.write(url, bytes);
                    res.add(url.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                res.add(result.getOutput().getUrl());
            }
        }
        return res;
    }

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
     * 获取StabilityAiImageModel
     */
    private StabilityAiImageModel getStabilityAiImageModel(StabilityAiImageEntity stabilityAiImageEntity) {
        StabilityAiApi stabilityAiApi = new StabilityAiApi(stabilityAiImageEntity.getApiKey());
        return new StabilityAiImageModel(stabilityAiApi, StabilityAiImageOptions.builder()
                .model(stabilityAiImageEntity.getStabilityAiImageType().getImageModel())
                .N(stabilityAiImageEntity.getN())
                .height(stabilityAiImageEntity.getStabilityAiImageType().getHeight())
                .width(stabilityAiImageEntity.getStabilityAiImageType().getWidth())
                .stylePreset(stabilityAiImageEntity.getStyleEnum())
                .build());
    }

    /**
     * 获取ZhiPuAiImageModel
     */
    private ZhiPuAiImageModel getZhiPuAiImageModel(ZhiPuAiImageEntity zhiPuAiImageEntity) {
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(zhiPuAiImageEntity.getApiKey());
        return new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(zhiPuAiImageEntity.getZhiPuAiImageType().getImageModel())
                .user(StringUtils.isBlank(zhiPuAiImageEntity.getUser()) ? null : zhiPuAiImageEntity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }
}
