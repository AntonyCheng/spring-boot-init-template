package top.sharehome.springbootinittemplate.utils.document.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.date.ExcelDateConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.date.ExcelLocalDateTimeConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.floatNum.ExcelDoubleConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.floatNum.ExcelFloatConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum.ExcelByteConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum.ExcelIntegerConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum.ExcelLongConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.convert.intNum.ExcelShortConverter;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelListener;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;
import top.sharehome.springbootinittemplate.config.easyexcel.core.impl.DefaultExcelListener;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeExcelException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Excel工具类
 * 注意：
 * 1、下面的“同步”方法适用于“小型”Excel，因为文件过大耗时，就会很长。
 * 2、非“同步”思想只适用于带有监听器的方法，因为是异步的，就需要一个回调机制响应结果。
 * 3、import表示从Excel->内存，export表示从内存->Excel
 *
 * @author AntonyCheng
 */
@Slf4j
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
     * 同步导入小型Excel数据流，但不关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importStreamSync(InputStream inputStream, String sheetName, Class<T> clazz) {
        return EasyExcel
                .read(inputStream)
                .head(clazz)
                .autoCloseStream(false)
                .sheet(sheetName)
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
     * 同步导入小型Excel数据流，同时关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importStreamSyncAndClose(InputStream inputStream, String sheetName, Class<T> clazz) {
        return EasyExcel
                .read(inputStream)
                .head(clazz)
                .sheet(sheetName)
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
     * 同步导入小型Excel文件
     *
     * @param excelFile 输入Excel文件
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @return Excel -> Java 结果
     */
    public static <T> List<T> importFileSync(File excelFile, String sheetName, Class<T> clazz) {
        return EasyExcel
                .read(excelFile)
                .head(clazz)
                .sheet(sheetName)
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
     * 异步导入Excel数据流，并且使用默认监听器，但不关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamWithListener(InputStream inputStream, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet(sheetName)
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
     * 异步导入Excel数据流，并且使用自定义监听器，但不关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamWithListener(InputStream inputStream, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet(sheetName)
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
     * 同步导入小型Excel数据流，并且使用默认监听器，但不关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncWithListener(InputStream inputStream, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet(sheetName)
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用自定义监听器，但不关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncWithListener(InputStream inputStream, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用自定义监听器，但不关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncWithListener(InputStream inputStream, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .autoCloseStream(false)
                .sheet(sheetName)
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
     * 异步导入Excel数据流，并且使用默认监听器，同时关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamAndCloseWithListener(InputStream inputStream, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet(sheetName)
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel数据流，并且使用自定义监听器，同时关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamAndCloseWithListener(InputStream inputStream, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet()
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel数据流，并且使用自定义监听器，同时关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamAndCloseWithListener(InputStream inputStream, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet(sheetName)
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
     * 同步导入小型Excel数据流，并且使用默认监听器，同时关闭流
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncAndCloseWithListener(InputStream inputStream, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet(sheetName)
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用自定义监听器，同时关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncAndCloseWithListener(InputStream inputStream, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel数据流，并且使用自定义监听器，同时关闭流
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param inputStream 输入流
     * @param sheetName   工作表名
     * @param clazz       Excel转换实体类
     * @param <T>         泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importStreamSyncAndCloseWithListener(InputStream inputStream, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(inputStream, clazz, listener)
                .sheet(sheetName)
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
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel文件，并且使用默认监听器
     *
     * @param excelFile 输入Excel文件
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileWithListener(File excelFile, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet(sheetName)
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel文件，并且使用自定义监听器
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param excelFile 输入Excel文件
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileWithListener(File excelFile, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet()
                .doRead();
        return listener.getExcelResult();
    }

    /**
     * 异步导入Excel文件，并且使用自定义监听器
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param excelFile 输入Excel文件
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileWithListener(File excelFile, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet(sheetName)
                .doRead();
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

    /**
     * 同步导入小型Excel文件，并且使用默认监听器
     *
     * @param excelFile 输入Excel文件
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileSyncWithListener(File excelFile, String sheetName, Class<T> clazz) {
        DefaultExcelListener<T> listener = new DefaultExcelListener<T>();
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet(sheetName)
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel文件，并且使用自定义监听器
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param excelFile 输入Excel文件
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileSyncWithListener(File excelFile, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet()
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 同步导入小型Excel文件，并且使用自定义监听器
     * 自定义监听器需要实现模板中的ExcelListener接口，构造出类似于DefaultExcelListener类的回执参数
     *
     * @param excelFile 输入Excel文件
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param <T>       泛型T
     * @return Excel -> Java 结果
     */
    public static <T> ExcelResult<T> importFileSyncWithListener(File excelFile, String sheetName, Class<T> clazz, ExcelListener<T> listener) {
        EasyExcel
                .read(excelFile, clazz, listener)
                .sheet(sheetName)
                .doReadSync();
        return listener.getExcelResult();
    }

    /**
     * 导出Excel本地文件（智能）
     *
     * @param list      输出Excel数据集合
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param pathName  输出文件名有三种情况：
     *                  1、filePath如果不带有后缀，那就表示该File是一个目录，系统会自动将Excel命名为sheetName.xlsx存储在该File目录下。
     *                  2、filePath如果带有后缀，那就表示该File是一个文件，后缀不是“xlsx”，就会将后缀转换成“xlsx”。
     *                  3、filePath如果带有后缀，那就表示该File是一个文件，后缀是“xlsx”，那么就存放于该File文件中。
     *                  注意：上述情况目录名都不允许带有“.”！如果有这样的需求，请自行使用exportFileOutputStream或者exportFileOutputStreamAndClose进行导出
     * @param <T>       泛型T
     * @return 返回本地文件绝对路径
     */
    public static <T> String exportLocalFile(List<T> list, String sheetName, Class<T> clazz, String pathName) {
        try {
            if (StringUtils.isEmpty(pathName)) {
                throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "未找到该Excel文件所在路径");
            }
            String extension = FilenameUtils.getExtension(pathName);
            File file = new File(pathName);
            if (extension.isEmpty()) {
                if (!file.exists()) {
                    Files.createDirectory(file.toPath());
                }
                pathName = pathName + "/" + (StringUtils.isEmpty(sheetName) ? "defaultName" : sheetName) + ".xlsx";
            } else {
                String fullName = file.getName();
                String parent = file.getParent();
                String name = fullName.substring(0, fullName.lastIndexOf("."));
                if (name.isEmpty()) {
                    name = (StringUtils.isEmpty(sheetName) ? "defaultName" : sheetName);
                }
                pathName = parent + "/" + name + ".xlsx";
            }
            file = new File(pathName);
            EasyExcel
                    .write(file, clazz)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new ExcelByteConverter())
                    .registerConverter(new ExcelShortConverter())
                    .registerConverter(new ExcelIntegerConverter())
                    .registerConverter(new ExcelLongConverter())
                    .registerConverter(new ExcelFloatConverter())
                    .registerConverter(new ExcelDoubleConverter())
                    .registerConverter(new ExcelDateConverter())
                    .registerConverter(new ExcelLocalDateTimeConverter())
                    .sheet(sheetName)
                    .doWrite(list);
            return file.getAbsolutePath();
        } catch (IOException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "Excel文件路径[" + pathName + "]异常");
        }
    }

    /**
     * 导出Excel文件输出流，但不关闭文件输出流
     *
     * @param list             输出Excel数据集合
     * @param sheetName        工作表名
     * @param clazz            Excel转换实体类
     * @param fileOutputStream 文件输出流
     * @param <T>              泛型T
     */
    public static <T> void exportFileOutputStream(List<T> list, String sheetName, Class<T> clazz, FileOutputStream fileOutputStream) {
        exportOutputStream(list, sheetName, clazz, fileOutputStream);
    }

    /**
     * 导出Excel文件输出流，同时关闭文件输出流
     *
     * @param list             输出Excel数据集合
     * @param sheetName        工作表名
     * @param clazz            Excel转换实体类
     * @param fileOutputStream 文件输出流
     * @param <T>              泛型T
     */
    public static <T> void exportFileOutputStreamAndClose(List<T> list, String sheetName, Class<T> clazz, FileOutputStream fileOutputStream) {
        exportOutputStreamAndClose(list, sheetName, clazz, fileOutputStream);
    }

    /**
     * 导出Excel请求响应流，同时关闭请求响应流并提交响应
     * 在Controller中建议直接返回void，如果想要统一返回响应类型R<T>，可以使用R.empty()方法。
     *
     * @param list      输出Excel数据集合
     * @param sheetName 工作表名
     * @param clazz     Excel转换实体类
     * @param response  请求响应流
     * @param <T>       泛型T
     */
    public static <T> void exportHttpServletResponse(List<T> list, String sheetName, Class<T> clazz, HttpServletResponse response) {
        try {
            handleResponse(sheetName, response);
            ServletOutputStream outputStream = response.getOutputStream();
            exportOutputStreamAndClose(list, sheetName, clazz, outputStream);
        } catch (IOException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR);
        }
    }

    /**
     * 根据类型导出Excel模板输出流，但不关闭输出流
     *
     * @param templateName 工作表名
     * @param clazz        Excel转换实体类
     * @param outputStream 输出流
     * @param <T>          泛型T
     */
    public static <T> void exportTemplateOutputStream(String templateName, Class<T> clazz, OutputStream outputStream) {
        exportOutputStream(new ArrayList<T>(), templateName, clazz, outputStream);
    }

    /**
     * 根据类型导出Excel模板输出流，同时关闭输出流
     *
     * @param templateName 工作表名
     * @param clazz        Excel转换实体类
     * @param outputStream 输出流
     * @param <T>          泛型T
     */
    public static <T> void exportTemplateOutputStreamAndClose(String templateName, Class<T> clazz, OutputStream outputStream) {
        exportOutputStreamAndClose(new ArrayList<T>(), templateName, clazz, outputStream);
    }


    /**
     * 根据类型导出Excel模板请求响应流，同时关闭请求响应流并提交响应
     * 在Controller中建议直接返回void，如果想要统一返回响应类型R<T>，可以使用R.empty()方法。
     *
     * @param templateName 工作表名
     * @param clazz        Excel转换实体类
     * @param response     请求响应流
     * @param <T>          泛型T
     */
    public static <T> void exportTemplateHttpServletResponse(String templateName, Class<T> clazz, HttpServletResponse response) {
        try {
            handleResponse(templateName, response);
            ServletOutputStream outputStream = response.getOutputStream();
            exportOutputStreamAndClose(new ArrayList<T>(), templateName, clazz, outputStream);
        } catch (IOException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR);
        }
    }

    /**
     * 导出Excel模板目录下的模板文件，同时关闭请求响应流并提交响应，模板目录一定是resources文件夹下templates/excel目录
     * 在Controller中建议直接返回void，如果想要统一返回响应类型R<T>，可以使用R.empty()方法。
     *
     * @param templateName resource模板名称（不带后缀）
     * @param response     请求响应流
     * @param <T>          泛型T
     */
    public static <T> void exportTemplateHttpServletResponse(String templateName, HttpServletResponse response) {
        try {
            File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/excel/" + templateName + ".xlsx");
            if (!file.isFile()) {
                throw new FileNotFoundException();
            }
            handleResponse(templateName, response);
            FileInputStream fileInputStream = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            ServletOutputStream outputStream = response.getOutputStream();
            while ((len = fileInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "模板文件[" + templateName + ".xlsx]未找到");
        } catch (IOException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR);
        }

    }

    /**
     * 导出Excel输出流，但不关闭输出流
     *
     * @param list         输出Excel数据集合
     * @param sheetName    工作表名
     * @param clazz        Excel转换实体类
     * @param outputStream 输出流
     * @param <T>          泛型T
     */
    public static <T> void exportOutputStream(List<T> list, String sheetName, Class<T> clazz, OutputStream outputStream) {
        EasyExcel
                .write(outputStream, clazz)
                .autoCloseStream(false)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new ExcelByteConverter())
                .registerConverter(new ExcelShortConverter())
                .registerConverter(new ExcelIntegerConverter())
                .registerConverter(new ExcelLongConverter())
                .registerConverter(new ExcelFloatConverter())
                .registerConverter(new ExcelDoubleConverter())
                .registerConverter(new ExcelDateConverter())
                .registerConverter(new ExcelLocalDateTimeConverter())
                .sheet(sheetName)
                .doWrite(list);
    }

    /**
     * 导出Excel输出流，同时关闭输出流
     *
     * @param list         输出Excel数据集合
     * @param sheetName    工作表名
     * @param clazz        Excel转换实体类
     * @param outputStream 输出流
     * @param <T>          泛型T
     */
    public static <T> void exportOutputStreamAndClose(List<T> list, String sheetName, Class<T> clazz, OutputStream outputStream) {
        EasyExcel
                .write(outputStream, clazz)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerConverter(new ExcelByteConverter())
                .registerConverter(new ExcelShortConverter())
                .registerConverter(new ExcelIntegerConverter())
                .registerConverter(new ExcelLongConverter())
                .registerConverter(new ExcelFloatConverter())
                .registerConverter(new ExcelDoubleConverter())
                .registerConverter(new ExcelDateConverter())
                .registerConverter(new ExcelLocalDateTimeConverter())
                .sheet(sheetName)
                .doWrite(list);
    }

    /**
     * 处理响应
     *
     * @param fileName 文件名
     * @param response 响应
     */
    private static void handleResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        String realName = null;
        if (StringUtils.isBlank(fileName)) {
            realName = UUID.randomUUID().toString().replace("-", "") + ".xlsx";
        }else {
            realName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName + ".xlsx";
        }
        String encodeName = URLEncoder
                .encode(realName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        String contentDispositionValue = "attachment; filename=" + encodeName + ";filename*=utf-8''" + encodeName;
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue);
        response.setHeader("download-filename", encodeName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }

}
