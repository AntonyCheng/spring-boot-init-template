package top.sharehome.springbootinittemplate.config.easyexcel.core;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * Excel导入监听
 *
 * @param <T> 泛型T
 * @author AntonyCheng
 */
public interface ExcelListener<T> extends ReadListener<T> {

    ExcelResult<T> getExcelResult();

}
