package top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * EasyExcel Integer转换类
 *
 * @author AntonyCheng
 */
public class ExcelIntegerConverter implements Converter<Integer> {

    @Override
    public Class<Integer> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public Integer convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return Integer.parseInt(stringValue);
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        WriteCellData<Object> cellData = new WriteCellData<>(new BigDecimal(value));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }

}