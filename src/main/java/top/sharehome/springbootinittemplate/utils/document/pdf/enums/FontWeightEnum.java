package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF字体字重
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum FontWeightEnum {

    /**
     * 正常
     */
    NORMAL("normal", 400),

    /**
     * 粗体
     */
    BOLD("bold", 700),

    /**
     * 加粗
     */
    BOLDER("bolder", 900),

    /**
     * 细体
     */
    LIGHTER("lighter", 100);

    private final String name;

    private final Integer value;

}
