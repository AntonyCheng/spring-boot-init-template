package top.sharehome.springbootinittemplate.document.excel;

import lombok.SneakyThrows;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;
import top.sharehome.springbootinittemplate.document.excel.entity.ExcelUser;
import top.sharehome.springbootinittemplate.utils.excel.ExcelUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Excel测试类
 *
 * @author AntonyCheng
 */
public class Main {
    @SneakyThrows
    public static void main(String[] args) {

        String projectRootPath = System.getProperty("user.dir");

        File file = new File(projectRootPath + "/src/main/java/top/sharehome/springbootinittemplate/document/excel/file/test.xlsx");
//        ArrayList<ExcelUser> excelUsers = new ArrayList<>() {
//            {
//                for (int i = 0; i < 10; i++) {
//                    add(new ExcelUser(1767110638703833090L, "admin" + i, "admin", "test1", "https://www.baidu.com", "admin", LocalDateTime.now(), LocalDateTime.now(), 0));
//                }
//            }
//        };
//        EasyExcel.write(file, ExcelUser.class).sheet("用户表").doWrite(excelUsers);

        ExcelResult<ExcelUser> excelResult = ExcelUtils.importFileSyncWithListener(file, ExcelUser.class);

        excelResult.getList().forEach(System.out::println);
    }
}
