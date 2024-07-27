package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF表格水平对齐方式
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum TableHorizontal {

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
