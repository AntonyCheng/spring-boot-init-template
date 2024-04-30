package top.sharehome.springbootinittemplate.config.easyexcel.convert.date;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * EasyExcel Date转换类
 *
 * @author AntonyCheng
 */
public class ExcelDateConverter implements Converter<Date> {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public Date convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = readCellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return simpleDateFormat.parse(stringValue, new ParsePosition(0));
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(Date value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (Objects.nonNull(value)) {
            return new WriteCellData<>(CellDataTypeEnum.STRING, simpleDateFormat.format(value));
        }
        return new WriteCellData<Object>(CellDataTypeEnum.STRING, "");
    }

}