package top.sharehome.springbootinittemplate.config.ai.spring.service.image;

import top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.ImageModelBase;

import java.util.List;

/**
 * AI Image服务接口
 *
 * @author AntonyCheng
 */
public interface AiImageService {

    /**
     * 生成AI Image功能，快速生成图片，返回临时图片URL
     *
     * @param model     image模型信息
     * @param prompt    提示词
     */
    List<String> imageToTempUrl(ImageModelBase model, String prompt);

    /**
     * 生成AI Image功能，快速生成图片，返回图片byte[]数组
     */
    List<byte[]> imageToByteArray(ImageModelBase model, String prompt);

}
