package top.sharehome.springbootinittemplate.config.ai.spring.service.image.model.enums;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * StabilityAI Image类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum StabilityAiImageType {

    SDXL_1_0_1024_1024(1, "stable-diffusion-xl-1024-v1-0", 1024, 1024),

    SDXL_1_0_1152_896(2, "stable-diffusion-xl-1024-v1-0", 1152, 896),

    SDXL_1_0_896_1152(3, "stable-diffusion-xl-1024-v1-0", 896, 1152),

    SDXL_1_0_1216_832(4, "stable-diffusion-xl-1024-v1-0", 1216, 832),

    SDXL_1_0_1344_768(5, "stable-diffusion-xl-1024-v1-0", 1344, 768),

    SDXL_1_0_768_1344(6, "stable-diffusion-xl-1024-v1-0", 768, 1344),

    SDXL_1_0_1536_640(7, "stable-diffusion-xl-1024-v1-0", 1536, 640),

    SDXL_1_0_640_1536(8, "stable-diffusion-xl-1024-v1-0", 640, 1536),

    SD_1_6_1024_1024(9, "stable-diffusion-v1-6", 1024, 1024),

    SD_1_6_1152_896(10, "stable-diffusion-v1-6", 1152, 896),

    SD_1_6_896_1152(11, "stable-diffusion-v1-6", 896, 1152),

    SD_1_6_1216_832(12, "stable-diffusion-v1-6", 1216, 832),

    SD_1_6_1344_768(13, "stable-diffusion-v1-6", 1344, 768),

    SD_1_6_768_1344(14, "stable-diffusion-v1-6", 768, 1344),

    SD_1_6_1536_640(15, "stable-diffusion-v1-6", 1536, 640),

    SD_1_6_640_1536(16, "stable-diffusion-v1-6", 640, 1536);

    /**
     * ID
     */
    private final Integer id;

    /**
     * StabilityAI图像模型
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

    public JSONObject toJsonObj(){
        return toJsonObject(this);
    }

    public static StabilityAiImageType getTypeById(Integer id) {
        List<StabilityAiImageType> list = Arrays.stream(StabilityAiImageType.values()).filter(stabilityAiImageType -> Objects.equals(stabilityAiImageType.getId(), id)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static StabilityAiImageType getTypeByName(String name) {
        List<StabilityAiImageType> list = Arrays.stream(StabilityAiImageType.values()).filter(stabilityAiImageType -> Objects.equals(stabilityAiImageType.name(), name)).toList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static JSONObject toJsonObjectById(Integer id) {
        StabilityAiImageType type = getTypeById(id);
        if (Objects.nonNull(type)) {
            return toJsonObject(type);
        }
        return JSONObject.of();
    }

    public static JSONObject toJsonObjectByName(String name) {
        StabilityAiImageType type = getTypeByName(name);
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
        return Arrays.stream(StabilityAiImageType.values()).map(StabilityAiImageType::toJsonObject).toList();
    }

    private static JSONObject toJsonObject(StabilityAiImageType type) {
        return JSONObject.of(
                "id", type.getId(),
                "model", type.getModel(),
                "width", type.getWidth(),
                "height", type.getHeight()
        );
    }

}
