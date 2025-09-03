package top.sharehome.springbootinittemplate.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import top.sharehome.springbootinittemplate.config.ai.spring.enums.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 模型类型服务
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ModelTypeService {

    CHAT(1, "chat", Map.of(
            1, ChatServiceType.DeepSeek.getValue(),
            2, ChatServiceType.OpenAI.getValue(),
            3, ChatServiceType.Ollama.getValue(),
            4, ChatServiceType.ZhiPuAI.getValue(),
            5, ChatServiceType.MistralAI.getValue(),
            6, ChatServiceType.MiniMax.getValue(),
            7, ChatServiceType.AzureOpenAI.getValue()
    )),

    EMBEDDING(2, "embedding", Map.of(
            1, EmbeddingServiceType.OpenAI.getValue(),
            2, EmbeddingServiceType.Ollama.getValue(),
            3, EmbeddingServiceType.ZhiPuAI.getValue(),
            4, EmbeddingServiceType.MistralAI.getValue(),
            5, EmbeddingServiceType.MiniMax.getValue(),
            6, EmbeddingServiceType.AzureOpenAI.getValue()
    )),

    IMAGE(3, "image", Map.of(
            1, ImageServiceType.OpenAI.getValue(),
            2, ImageServiceType.Stability.getValue(),
            3, ImageServiceType.ZhiPuAI.getValue(),
            4, ImageServiceType.AzureOpenAI.getValue()
    )),

    TRANSCRIPTION(4, "transcription", Map.of(
            1, TranscriptionServiceType.OpenAI.getValue(),
            2, TranscriptionServiceType.AzureOpenAI.getValue()
    )),

    TTS(5, "tts", Map.of(
            1, TtsServiceType.OpenAI.getValue()
    ));

    private final Integer id;

    private final String name;

    private final Map<Integer, String> services;

    public static ModelTypeService getEnumById(Integer id) {
        List<ModelTypeService> list = Arrays.stream(ModelTypeService.values()).filter(modelTypeService -> Objects.equals(modelTypeService.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static Map<Integer, String> getServicesById(Integer id) {
        ModelTypeService enumById = getEnumById(id);
        return Objects.nonNull(enumById) ? enumById.getServices() : Map.of();
    }

    public static ModelTypeService getEnumByName(String name) {
        List<ModelTypeService> list = Arrays.stream(ModelTypeService.values()).filter(modelTypeService -> Objects.equals(modelTypeService.getName(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static Map<Integer, String> getServicesByName(String name) {
        ModelTypeService enumByName = getEnumByName(name);
        return Objects.nonNull(enumByName) ? enumByName.getServices() : Map.of();
    }

    public static Boolean hasServiceByTypeName(String typeName, String serviceName) {
        return getServicesByName(typeName).containsValue(serviceName);
    }

    public static Boolean hasServiceByTypeId(Integer typeId, String serviceName) {
        return getServicesById(typeId).containsValue(serviceName);
    }

}
