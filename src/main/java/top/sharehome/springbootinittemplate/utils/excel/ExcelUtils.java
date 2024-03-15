package top.sharehome.springbootinittemplate.utils.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
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
import java.util.List;
import java.util.UUID;

/**
 * Excel工具类
 * todo: 做做测试，关于Response的
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
     *                  注意：上述情况目录名都不允许带有“.”！如果有这样的需求，请自行使用
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
                // todo 处理一下如果sheetName为空的情况
                pathName = pathName + "/" + sheetName + ".xlsx";
            } else {
                String fullName = file.getName();
                String parent = file.getParent();
                String name = fullName.substring(0, fullName.lastIndexOf("."));
                if (name.isEmpty()) {
                    name = sheetName;
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
     * 导出Excel请求响应流，但不关闭请求响应流
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
            exportOutputStream(list, sheetName, clazz, outputStream);
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
     * 导入Excel模板，但是不关闭
     * 模板文件夹必须是src/main/resources/templates/excel目录
     * 如果有多个模板，模板名称不能相同，如果传入模板名为空，那么会自动命名为template.xlsx
     *
     * @param inputStream  模板数据流
     * @param templateName 模板名（不带后缀）
     * @param <T>          泛型T
     */
    public static <T> void importTemplateStream(InputStream inputStream, String templateName) {
        try {
            String templatePath = ResourceUtils.CLASSPATH_URL_PREFIX + "templates/excel";
            if (!ResourceUtils.getFile(templatePath).isDirectory()) {
                throw new FileNotFoundException();
            }
            if (ObjectUtils.isEmpty(inputStream)) {
                throw new IOException();
            }
            // todo
            if (StringUtils.isEmpty(templateName)) {

            }
            new File(templatePath + "/" + templateName);
        } catch (FileNotFoundException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "没有找到模板文件夹");
        } catch (IOException e) {
            throw new CustomizeExcelException(ReturnCode.EXCEL_FILE_ERROR, "读取文件异常");
        }
    }

    /**
     * 处理响应
     *
     * @param fileName 工作表名
     * @param response 响应
     */
    private static void handleResponse(String fileName, HttpServletResponse response) throws UnsupportedEncodingException {
        String realName = UUID.randomUUID().toString().replace("-", "") + "_" + fileName + ".xlsx";
        String encodeName = URLEncoder
                .encode(realName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        String contentDispositionValue = "attachment; filename=" + encodeName + "; filename*=utf-8''" + encodeName;
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue);
        response.setHeader("download-filename", encodeName);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
    }

}
