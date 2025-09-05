package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * AzureOpenAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AzureOpenAiImageType {

    /**
     * DALL·E 3 1024×1024
     */
    DallE3_1024_1024(1, "dall-e-3", 1024, 1024),

    /**
     * DALL·E 3 1024×1792
     */
    DallE3_1024_1792(2, "dall-e-3", 1024, 1792),

    /**
     * DALL·E 3 1792×1024
     */
    DallE3_1792_1024(3, "dall-e-3", 1792, 1024);

    /**
     * ID
     */
    private final Integer id;

    /**
     * OpenAI图像模型
     */
    private final String model;

    /**
     * 图像宽度
     */
    private final Integer width;

    /**
     * 图像高度
     */
    private final Integer height;

    public String toJsonStr() {
        return toJsonObject(this).toJSONString();
    }

    public static AzureOpenAiImageType getTypeById(Integer id) {
        List<AzureOpenAiImageType> list = Arrays.stream(AzureOpenAiImageType.values()).filter(azureOpenAiImageType -> Objects.equals(azureOpenAiImageType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static AzureOpenAiImageType getTypeByName(String name) {
        List<AzureOpenAiImageType> list = Arrays.stream(AzureOpenAiImageType.values()).filter(azureOpenAiImageType -> Objects.equals(azureOpenAiImageType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        AzureOpenAiImageType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        AzureOpenAiImageType type = getTypeByName(name);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static String toJsonStrById(Integer id) {
        return toJsonObjectById(id).toJSONString();
    }

    public static String toJsonStrByName(String name) {
        return toJsonObjectByName(name).toJSONString();
    }

    public static List<JSONObject> toList() {
        return Arrays.stream(AzureOpenAiImageType.values()).map(AzureOpenAiImageType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(AzureOpenAiImageType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel(),
                "width", type.getWidth(),
                "height", type.getHeight()
        );
    }

}
