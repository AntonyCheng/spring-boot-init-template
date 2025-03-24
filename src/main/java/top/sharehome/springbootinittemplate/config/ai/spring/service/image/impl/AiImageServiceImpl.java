package top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl;

import com.aliyun.core.utils.Base64Util;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.azure.openai.AzureOpenAiImageModel;
import org.springframework.ai.azure.openai.AzureOpenAiImageOptions;
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
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.AiImageService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.entity.*;
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
        } else if (model instanceof AzureOpenAiImageEntity entity) {
            imageResponse = this.getAzureOpenAiImageModel(entity).call(new ImagePrompt(prompt));
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
        } else if (model instanceof AzureOpenAiImageEntity entity) {
            imageResponse = this.getAzureOpenAiImageModel(entity).call(new ImagePrompt(prompt));
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
    private OpenAiImageModel getOpenAiImageModel(OpenAiImageEntity entity) {
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
    private AzureOpenAiImageModel getAzureOpenAiImageModel(AzureOpenAiImageEntity entity) {
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
    private StabilityAiImageModel getStabilityAiImageModel(StabilityAiImageEntity entity) {
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
    private ZhiPuAiImageModel getZhiPuAiImageModel(ZhiPuAiImageEntity entity) {
        ZhiPuAiImageApi zhiPuAiImageApi = new ZhiPuAiImageApi(entity.getApiKey());
        return new ZhiPuAiImageModel(zhiPuAiImageApi, ZhiPuAiImageOptions.builder()
                .model(entity.getZhiPuAiImageType().getImageModel())
                .user(StringUtils.isBlank(entity.getUser()) ? null : entity.getUser())
                .build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    /**
     * 获取QianFanImageModel
     */
    private QianFanImageModel getQianFanImageModel(QianFanImageEntity entity) {
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
