package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ZhiPuAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ZhiPuAiImageType {

    CogView_4("cogview-4"),

    CogView_3("cogview-3"),

    CogView_3_Plus("cogview-3-plus"),

    CogView_3_Flash("cogview-3-flash");

    /**
     * ZhiPuAI图像模型
     */
    private final String model;

}
