package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF条码类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum BarcodeType {

    /**
     * 库德巴码（一维码）
     */
    CODABAR("codabar"),

    /**
     * QR码（二维码）
     */
    QR_CODE("qr_code");

    private final String name;

}
