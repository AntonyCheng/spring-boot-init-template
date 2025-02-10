package top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl;

import com.aliyun.core.utils.Base64Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.AiImageService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.OpenAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.QianFanImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.StabilityAiImageEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.ZhiPuAiImageEntity;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * AI Image服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
public class AiImageServiceImpl implements AiImageService {

    @Override
    public List<String> imageToTempUrl(ImageModelBase model, String prompt) {
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
        } else if (model instanceof QianFanImageEntity entity) {
            imageResponse = this.getQianFanImageModel(entity).call(new ImagePrompt(prompt));
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

    @Override
    public List<byte[]> imageToByteArray(ImageModelBase model, String prompt) {
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
        } else if (model instanceof QianFanImageEntity entity) {
            imageResponse = this.getQianFanImageModel(entity).call(new ImagePrompt(prompt));
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        List<byte[]> res = new ArrayList<>();
        for (ImageGeneration result : imageResponse.getResults()) {
            if (StringUtils.isBlank(result.getOutput().getUrl())) {
                String b64Json = result.getOutput().getB64Json();
                byte[] decode = Base64.getDecoder().decode(b64Json);
                res.add(decode);
            } else {
                try (InputStream inputStream = new URL(result.getOutput().getUrl()).openStream(); ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                    }
                    res.add(byteArrayOutputStream.toByteArray());
                } catch (IOException e) {
                    throw new CustomizeAiException(ReturnCode.SYSTEM_FILE_ADDRESS_IS_ABNORMAL, "图片链接解析异常");
                }
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
                .model(openAiImageEntity.getOpenAiImageType().getImageModel())
                .quality(openAiImageEntity.getOpenAiImageType().getQuality())
                .N(openAiImageEntity.getN())
                .height(openAiImageEntity.getOpenAiImageType().getHeight())
                .width(openAiImageEntity.getOpenAiImageType().getWidth())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取StabilityAiImageModel
     *
     * @apiNote 如果直接使用Stability官方的模型，则不支持仅包含中文的提示词（会触发审核系统），推荐直接使用英文提示词
     */
    private StabilityAiImageModel getStabilityAiImageModel(StabilityAiImageEntity stabilityAiImageEntity) {
        StabilityAiApi stabilityAiApi = new StabilityAiApi(stabilityAiImageEntity.getApiKey());
        return new StabilityAiImageModel(stabilityAiApi, StabilityAiImageOptions.builder()
                .model(stabilityAiImageEntity.getStabilityAiImageType().getImageModel())
                .N(stabilityAiImageEntity.getN())
                .height(stabilityAiImageEntity.getStabilityAiImageType().getHeight())
                .width(stabilityAiImageEntity.getStabilityAiImageType().getWidth())
                .stylePreset(Objects.isNull(stabilityAiImageEntity.getStyleEnum()) ? null : stabilityAiImageEntity.getStyleEnum().toString())
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

    /**
     * 获取QianFanImageModel
     */
    private QianFanImageModel getQianFanImageModel(QianFanImageEntity qianFanImageEntity) {
        QianFanImageApi qianFanImageApi = new QianFanImageApi(qianFanImageEntity.getApiKey(), qianFanImageEntity.getSecretKey());
        return new QianFanImageModel(qianFanImageApi, QianFanImageOptions.builder()
                .model(qianFanImageEntity.getQianFanImageType().getImageModel())
                .N(qianFanImageEntity.getN())
                .height(qianFanImageEntity.getQianFanImageType().getHeight())
                .width(qianFanImageEntity.getQianFanImageType().getWidth())
                .style(Objects.isNull(qianFanImageEntity.getStyleEnum()) ? null : qianFanImageEntity.getStyleEnum().toString())
                .user(StringUtils.isBlank(qianFanImageEntity.getUser()) ? null : qianFanImageEntity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }
}
