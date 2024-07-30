package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF表格垂直对齐方式
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum TableVertical {

    /**
     * 居上
     */
    BEFORE("before"),

    /**
     * 居中
     */
    CENTER("center"),

    /**
     * 居下
     */
    AFTER("after");

    private final String name;

}
