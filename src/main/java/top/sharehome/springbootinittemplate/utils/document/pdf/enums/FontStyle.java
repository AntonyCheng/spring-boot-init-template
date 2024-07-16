package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF字体样式
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum FontStyle {

    /**
     * 正常
     */
    NORMAL("normal"),

    /**
     * 斜体
     */
    ITALIC("italic");

    private final String name;

}
