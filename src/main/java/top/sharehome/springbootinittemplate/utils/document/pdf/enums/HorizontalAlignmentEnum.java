package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF对齐方式
 * left：居左
 * center：居中
 * right：居右
 * justify：两端对齐
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum HorizontalAlignmentEnum {

    /**
     * 居左
     */
    LEFT("left"),

    /**
     * 居中
     */
    CENTER("center"),

    /**
     * 居右
     */
    RIGHT("right"),

    /**
     * 两端对齐
     */
    JUSTIFY("justify");

    private final String name;

}
