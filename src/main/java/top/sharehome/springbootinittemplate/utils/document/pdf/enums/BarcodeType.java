package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF条码类型
 * todo：目前仅支持二维码，其他条码类型有固定的内容要求，后续可以作为一个扩展点进行模板适配
 * 一维码：
 * codabar：库德巴码
 * code_39：标准39码
 * code_93：标准93码
 * code_128：标准128码
 * ean_8：缩短版国际商品条码
 * ean_13：标准版国际商品条码
 * itf：交叉码
 * upc_a：美国商品码a
 * upc_e：美国商品码e
 * upc_ean_extension：美国商品码扩展码
 * 二维码：
 * qr_code：qr码
 * aztec：阿兹特克码
 * data_matrix：dm码
 * maxi_code：maxi码
 * pdf_417：pdf-417码
 * rss_14：rss-14码
 * rss_expanded：rss扩展码
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum BarcodeType {

    /**
     * QR码（二维码）
     */
    QR_CODE("qr_code");

    private final String name;

}
