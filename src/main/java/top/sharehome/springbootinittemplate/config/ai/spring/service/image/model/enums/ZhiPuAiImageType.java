package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ZhiPuAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ZhiPuAiImageType {

    CogView_4(1, "cogview-4"),

    CogView_3(2, "cogview-3"),

    CogView_3_Plus(3, "cogview-3-plus"),

    CogView_3_Flash(4, "cogview-3-flash");

    /**
     * ID
     */
    private final Integer id;

    /**
     * ZhiPuAI图像模型
     */
    private final String model;

    public String toJsonStr() {
        return toJsonObject(this).toJSONString();
    }

    public static ZhiPuAiImageType getTypeById(Integer id) {
        List<ZhiPuAiImageType> list = Arrays.stream(ZhiPuAiImageType.values()).filter(zhiPuAiImageType -> Objects.equals(zhiPuAiImageType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static ZhiPuAiImageType getTypeByName(String name) {
        List<ZhiPuAiImageType> list = Arrays.stream(ZhiPuAiImageType.values()).filter(zhiPuAiImageType -> Objects.equals(zhiPuAiImageType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        ZhiPuAiImageType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        ZhiPuAiImageType type = getTypeByName(name);
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
        return Arrays.stream(ZhiPuAiImageType.values()).map(ZhiPuAiImageType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(ZhiPuAiImageType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel()
        );
    }

}
