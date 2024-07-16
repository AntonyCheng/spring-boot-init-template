package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF字体样式
 * none：无
 * dotted：点线
 * dashed：虚线
 * solid：实线
 * double：双实线
 * groove：槽线
 * ridge：脊线
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum SplitLineStyle {

    /**
     * 无
     */
    NONE("none"),

    /**
     * 点线
     */
    DOTTED("dotted"),

    /**
     * 虚线
     */
    DASHED("dashed"),

    /**
     * 实线
     */
    SOLID("solid"),

    /**
     * 双实线
     */
    DOUBLE("double"),

    /**
     * 槽线
     */
    GROOVE("groove"),

    /**
     * 脊线
     */
    RIDGE("ridge");

    private final String name;

}
