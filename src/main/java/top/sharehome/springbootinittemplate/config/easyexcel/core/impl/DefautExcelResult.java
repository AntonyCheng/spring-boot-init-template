package top.sharehome.springbootinittemplate.config.easyexcel.core.impl;

import lombok.Data;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel默认返回对象
 *
 * @param <T> 泛型T
 * @author AntonyCheng
 */
@Data
public class DefautExcelResult<T> implements ExcelResult<T> {

    /**
     * 数据对象list
     */
    private List<T> list;

    /**
     * 错误信息列表
     */
    private List<String> errorList;

    public DefautExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
    }

    public DefautExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    public DefautExcelResult(ExcelResult<T> excelResult) {
        this.list = excelResult.getList();
        this.errorList = excelResult.getErrorList();
    }

    /**
     * 获取导入回执
     *
     * @return 导入回执
     */
    @Override
    public String getResponse() {
        int successCount = list.size();
        int errorCount = errorList.size();
        if (successCount == 0 || errorCount != 0) {
            return "读取失败，未解析到表格数据或者发生异常！";
        } else {
            return String.format("恭喜您，全部读取成功！共%d条", successCount);
        }
    }

}
