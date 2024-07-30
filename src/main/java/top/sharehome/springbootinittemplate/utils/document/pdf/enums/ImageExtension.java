package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PDF图像文件扩展名
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ImageExtension {

    /**
     * .jpg格式
     */
    JPG(".jpg"),

    /**
     * .png格式
     */
    PNG(".png"),

    /**
     * .svg格式
     */
    SVG(".svg");

    private final String name;

    public static ImageExtension getEnumByName(String name) {
        List<ImageExtension> list = Arrays.stream(ImageExtension.values()).filter(imageExtension -> Objects.equals(imageExtension.getName(), name)).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
