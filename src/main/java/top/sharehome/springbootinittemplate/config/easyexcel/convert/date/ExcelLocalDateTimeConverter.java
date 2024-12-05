package top.sharehome.springbootinittemplate.config.easyexcel.convert.date;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * EasyExcel LocalDateTime转换类
 *
 * @author AntonyCheng
 */
public class ExcelLocalDateTimeConverter implements Converter<LocalDateTime> {

    @Override
    public Class<LocalDateTime> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 读Excel时转换方法
     */
    @Override
    public LocalDateTime convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = readCellData.getStringValue();
        if (StringUtils.isEmpty(stringValue)) {
            return null;
        }
        return LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 写Excel时转换方法
     */
    @Override
    public WriteCellData<Object> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (Objects.nonNull(value)) {
            return new WriteCellData<Object>(CellDataTypeEnum.STRING, value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return new WriteCellData<Object>(CellDataTypeEnum.STRING, "");
    }

}