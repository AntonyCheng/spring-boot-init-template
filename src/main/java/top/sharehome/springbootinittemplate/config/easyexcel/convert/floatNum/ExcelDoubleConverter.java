package top.sharehome.springbootinittemplate.config.easyexcel.convert.floatNum;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * EasyExcel Double转换类
 *
 * @author AntonyCheng
 */
public class ExcelDoubleConverter implements Converter<Double> {

    @Override
    public Class<Double> supportJavaTypeKey() {
        return Double.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public Double convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return Double.parseDouble(stringValue);
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Double value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (Objects.nonNull(value)) {
            String stringValue = String.valueOf(value);
            // 当数字长度大于15（在Excel中15位之后开始丢失精度）时使用字符串存储
            if (stringValue.length() > 15) {
                return new WriteCellData<Object>(CellDataTypeEnum.STRING, stringValue);
            } else {
                WriteCellData<Object> cellData = new WriteCellData<Object>(new BigDecimal(stringValue));
                cellData.setType(CellDataTypeEnum.NUMBER);
                return cellData;
            }
        }
        return new WriteCellData<Object>(CellDataTypeEnum.STRING, "");
    }

}