package top.sharehome.springbootinittemplate.config.easyexcel.core.impl;

import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.event.AnalysisEventListener;
import cn.idev.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson2.JSON;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelListener;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeDocumentException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Excel默认导入监听
 *
 * @param <T> 泛型T
 * @author AntonyCheng
 */
@Slf4j
public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {

    /**
     * Excel表头数据
     */
    @Getter
    private Map<Integer, String> headMap;

    /**
     * 导入回执
     */
    private final ExcelResult<T> excelResult = new DefautExcelResult<T>();

    /**
     * 获取导入回执
     */
    @Override
    public ExcelResult<T> getExcelResult() {
        return excelResult;
    }

    /**
     * 当读取到表格数据时的操作
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        excelResult.getList().add(data);
    }

    /**
     * 当读取到表头时的操作
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.info("解析到表头数据：{}", JSON.toJSONString(headMap));
    }

    /**
     * 当所有数据解析完的操作
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完毕，一共{}条数据！", excelResult.getList().size());
    }

    /**
     * 处理异常
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        String errorMsg = null;
        // 抓到ExcelDataConvertException进行相关日志记录
        if (exception instanceof ExcelDataConvertException excelDataConvertException) {
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errorMsg = String.format("第%d行-第%d列-表头[%s]：解析异常！", rowIndex + 1, columnIndex + 1, headMap.get(columnIndex));
            log.error(errorMsg);
        }
        // 抓住ConstraintViolationException进行相关日志记录
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            errorMsg = constraintViolations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(", "));
            log.error(errorMsg);
        }
        excelResult.getErrorList().add(errorMsg);
        throw new CustomizeDocumentException(ReturnCode.EXCEL_FILE_ERROR, errorMsg);
    }
}
