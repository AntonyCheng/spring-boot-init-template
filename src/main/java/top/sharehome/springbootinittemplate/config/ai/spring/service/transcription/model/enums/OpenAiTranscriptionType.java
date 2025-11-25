package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * OpenAI Transcription类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OpenAiTranscriptionType {

    /**
     * Whisper
     */
    Whisper(1, "whisper-1");

    /**
     * ID
     */
    private final Integer id;

    /**
     * OpenAI语音转文字模型
     */
    private final String model;

    public String toJsonStr() {
        return toJsonObject(this).toJSONString();
    }

    public JSONObject toJsonObj() {
        return toJsonObject(this);
    }

    public static OpenAiTranscriptionType getTypeById(Integer id) {
        List<OpenAiTranscriptionType> list = Arrays.stream(OpenAiTranscriptionType.values()).filter(openAiTranscriptionType -> Objects.equals(openAiTranscriptionType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static OpenAiTranscriptionType getTypeByName(String name) {
        List<OpenAiTranscriptionType> list = Arrays.stream(OpenAiTranscriptionType.values()).filter(openAiTranscriptionType -> Objects.equals(openAiTranscriptionType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        OpenAiTranscriptionType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        OpenAiTranscriptionType type = getTypeByName(name);
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
        return Arrays.stream(OpenAiTranscriptionType.values()).map(OpenAiTranscriptionType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(OpenAiTranscriptionType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel()
        );
    }

}
