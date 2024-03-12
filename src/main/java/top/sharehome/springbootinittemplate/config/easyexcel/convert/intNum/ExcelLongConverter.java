package top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * EasyExcel Long转换类
 *
 * @author AntonyCheng
 */
public class ExcelLongConverter implements Converter<Long> {

    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public Long convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = readCellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return Long.parseLong(stringValue);
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Long value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtils.isNotEmpty(value)) {
            String stringValue = String.valueOf(value);
            // 当数字长度大于15（在Excel中15位之后开始丢失精度）时使用字符串存储
            if (stringValue.length() > 15) {
                return new WriteCellData<Object>(CellDataTypeEnum.STRING, stringValue);
            } else {
                WriteCellData<Object> cellData = new WriteCellData<>(new BigDecimal(value));
                cellData.setType(CellDataTypeEnum.NUMBER);
                return cellData;
            }
        }
        return new WriteCellData<Object>(CellDataTypeEnum.STRING,"");
    }

}