package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF条码水平对齐方式
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum BarcodeHorizontal {

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
    RIGHT("right");

    private final String name;

}
