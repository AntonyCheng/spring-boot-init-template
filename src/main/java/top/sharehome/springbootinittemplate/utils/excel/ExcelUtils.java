package top.sharehome.springbootinittemplate.utils.excel;

import com.alibaba.excel.EasyExcel;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelListener;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;
import top.sharehome.springbootinittemplate.config.easyexcel.core.impl.DefaultExcelListener;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Excel工具类
 * 注意：
 * 1、下面的“同步”方法适用于“小型”Excel，因为文件过大耗时，就会很长。
 * 2、非“同步”思想只适用于带有监听器的方法，因为是异步的，就需要一个回调机制响应结果。
 *
 * @author AntonyCheng
 */
public class ExcelUtils {

    /**
     * 同步导入小型Excel数据流，但不关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importStreamSync(InputStream inputStream, Class<T> clazz) {
        return EasyExcel
                .read(inputStream)
                .head(clazz)
                .autoCloseStream(false)
                .sheet()
                .doReadSync();
    }

    /**
     * 同步导入小型Excel数据流，同时关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importStreamSyncAndClose(InputStream inputStream, Class<T> clazz) {
        return EasyExcel
                .read(inputStream)
                .head(clazz)
                .sheet()
                .doReadSync();
    }

    /**
     * 同步导入小型Excel文件
     *
     * @param excelFile 输入Excel文件
     * @param clazz     Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importFileSync(File excelFile, Class<T> clazz) {
        return EasyExcel
                .read(excelFile)
                .head(clazz)
                .sheet()
                .doReadSync();
    }

    /**
     * 异步导入Excel数据流，并且使用默认监听器，但不关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamWithListener(InputStream inputStream, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet()
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel数据流，并且使用自定义监听器，但不关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamWithListener(InputStream inputStream, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet()
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用默认监听器，但不关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncWithListener(InputStream inputStream, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel数据流，并且使用默认监听器，同时关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamAndCloseWithListener(InputStream inputStream, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet()
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用默认监听器，同时关闭流
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncAndCloseWithListener(InputStream inputStream, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel文件，并且使用默认监听器
     *
     * @param excelFile 输入Excel文件
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileWithListener(File excelFile, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel文件，并且使用默认监听器
     *
     * @param excelFile 输入Excel文件
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileSyncWithListener(File excelFile, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

}
