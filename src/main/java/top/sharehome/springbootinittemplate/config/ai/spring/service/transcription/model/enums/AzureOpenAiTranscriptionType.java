package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * AzureOpenAI Transcription类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AzureOpenAiTranscriptionType {

    /**
     * Whisper
     */
    Whisper(1, "whisper");

    /**
     * ID
     */
    private final Integer id;

    /**
     * AzureOpenAI语音转文字模型
     */
    private final String model;

    public String toJsonStr() {
        return toJsonObject(this).toJSONString();
    }

    public JSONObject toJsonObj() {
        return toJsonObject(this);
    }

    public static AzureOpenAiTranscriptionType getTypeById(Integer id) {
        List<AzureOpenAiTranscriptionType> list = Arrays.stream(AzureOpenAiTranscriptionType.values()).filter(azureOpenAiTranscriptionType -> Objects.equals(azureOpenAiTranscriptionType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static AzureOpenAiTranscriptionType getTypeByName(String name) {
        List<AzureOpenAiTranscriptionType> list = Arrays.stream(AzureOpenAiTranscriptionType.values()).filter(azureOpenAiTranscriptionType -> Objects.equals(azureOpenAiTranscriptionType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        AzureOpenAiTranscriptionType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        AzureOpenAiTranscriptionType type = getTypeByName(name);
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
        return Arrays.stream(AzureOpenAiTranscriptionType.values()).map(AzureOpenAiTranscriptionType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(AzureOpenAiTranscriptionType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel()
        );
    }

}
