package top.sharehome.springbootinittemplate.utils.document.pdf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PDF导出数据源
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ExportDataSource {

    /**
     * freemarker数据源
     */
    FREEMARKER,

    /**
     * thymeleaf数据源
     */
    THYMELEAF,

    /**
     * jte数据源
     */
    JTE

}
