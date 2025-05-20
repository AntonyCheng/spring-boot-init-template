package top.sharehome.springbootinittemplate.config.ai.spring.service.image.impl;

import com.aliyun.core.utils.Base64Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.AiImageService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.manager.ImageManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
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
    public List<String> imageToTempUrl(ImageModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        List<String> res = new ArrayList<>();
        for (ImageGeneration result : ImageManager.getImageModel(model).call(new ImagePrompt(prompt)).getResults()) {
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
        List<byte[]> res = new ArrayList<>();
        for (ImageGeneration result : ImageManager.getImageModel(model).call(new ImagePrompt(prompt)).getResults()) {
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

}
