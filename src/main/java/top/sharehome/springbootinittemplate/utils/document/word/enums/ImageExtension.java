package top.sharehome.springbootinittemplate.utils.document.word.enums;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Word图像文件扩展名
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ImageExtension {

    /**
     * .jpg格式
     */
    JPG(".jpg", 5),

    /**
     * .png格式
     */
    PNG(".png", 6);

    private final String name;

    private final int ooxmlId;

    public static ImageExtension getEnumByName(String name) {
        List<ImageExtension> list = Arrays.stream(ImageExtension.values()).filter(imageExtension -> Objects.equal(imageExtension.getName(), name)).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
