package top.sharehome.springbootinittemplate.document.excel;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import shade.powerjob.org.apache.commons.lang3.RandomUtils;
import top.sharehome.springbootinittemplate.config.easyexcel.core.ExcelResult;
import top.sharehome.springbootinittemplate.document.excel.entity.ExcelUser;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 直接使用main函数对ExcelUtils进行测试
 *
 * @author AntonyCheng
 */
public class TestExcelUtils {

    private static final String EXCEL1_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/excel/file/test.xlsx";

    private static final String EXCEL2_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/excel/file/test_bak.xlsx";

    private static final String EXCEL_TEMPLATE_PATH_NAME = System.getProperty("user.dir") + "/src/test/java/top/sharehome/springbootinittemplate/document/excel/file/template.xlsx";

    private static final List<ExcelUser> USER_LIST = new ArrayList<ExcelUser>() {
        {
            for (int i = 0; i < 100; i++) {
                add(new ExcelUser(
                        RandomUtils.nextLong(),
                        RandomStringUtils.random(6),
                        RandomStringUtils.random(10),
                        RandomStringUtils.random(3),
                        RandomStringUtils.random(15),
                        RandomUtils.nextInt(0, 2) == 0 ? "user" : "admin",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(10),
                        0
                ));
            }
        }
    };

    @SneakyThrows
    public static void main(String[] args) {
        // 导出用户表到本地服务器./file/test.xlsx中
        ExcelUtils.exportLocalFile(USER_LIST, "用户1表", ExcelUser.class, EXCEL1_PATH_NAME);
        // 将./file/test.xlsx内容导入为userList
        ExcelResult<ExcelUser> excelResult = ExcelUtils.importFileWithListener(new File(EXCEL1_PATH_NAME), ExcelUser.class);
        List<ExcelUser> userList = excelResult.getList();
        // 将userList导出到本地服务器./file/test_bak.xlsx中
        ExcelUtils.exportFileOutputStreamAndClose(userList, "用户2表", ExcelUser.class, new FileOutputStream(EXCEL2_PATH_NAME));
        // 根据ExcelUser类型导出一个模板文件到本地服务器./file/template.xlsx中
        ExcelUtils.exportTemplateOutputStreamAndClose("用户模板表", ExcelUser.class, new FileOutputStream(EXCEL_TEMPLATE_PATH_NAME));
    }

}
