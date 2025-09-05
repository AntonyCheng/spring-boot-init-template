package top.sharehome.springbootinittemplate.config.ai.spring.service.tts.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OpenAI TTS类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OpenAiTtsType {

    /**
     * TTS
     */
    TTS(1, "tts-1"),

    /**
     * TTS HD
     */
    TTS_HD(2, "tts-1-hd");

    /**
     * ID
     */
    private final Integer id;

    /**
     * OpenAI文字转语音模型
     */
    private final String model;

    public String toJsonStr() {
        return toJsonObject(this).toJSONString();
    }

    public static OpenAiTtsType getTypeById(Integer id) {
        List<OpenAiTtsType> list = Arrays.stream(OpenAiTtsType.values()).filter(openAiTtsType -> Objects.equals(openAiTtsType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static OpenAiTtsType getTypeByName(String name) {
        List<OpenAiTtsType> list = Arrays.stream(OpenAiTtsType.values()).filter(openAiTtsType -> Objects.equals(openAiTtsType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        OpenAiTtsType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        OpenAiTtsType type = getTypeByName(name);
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
        return Arrays.stream(OpenAiTtsType.values()).map(OpenAiTtsType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(OpenAiTtsType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel()
        );
    }

}
