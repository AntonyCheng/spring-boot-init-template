package top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * EasyExcel Byte转换类
 *
 * @author AntonyCheng
 */
public class ExcelByteConverter implements Converter<Byte> {

    @Override
    public Class<Byte> supportJavaTypeKey() {
        return Byte.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public Byte convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return Byte.parseByte(stringValue);
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Byte value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtils.isNotEmpty(value)) {
            String stringValue = String.valueOf(value);
            return new WriteCellData<Object>(CellDataTypeEnum.NUMBER, stringValue);
        }
        return new WriteCellData<Object>(CellDataTypeEnum.STRING, "");
    }

}