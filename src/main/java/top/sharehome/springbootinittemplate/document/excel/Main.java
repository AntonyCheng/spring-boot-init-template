package top.sharehome.springbootinittemplate.document.excel;

import lombok.SneakyThrows;
import top.sharehome.springbootinittemplate.utils.excel.ExcelUtils;

/**
 * Excel测试类
 *
 * @author AntonyCheng
 */
public class Main {
    @SneakyThrows
    public static void main(String[] args) {

        ExcelUtils.importTemplateStream(null,null);
//        String projectRootPath = System.getProperty("user.dir");
//
//        File file = new File(projectRootPath + "/src/main/java/top/sharehome/springbootinittemplate/document/excel/file/.xlsx");
//        String path = file.getPath();
//
//        ArrayList<ExcelUser> excelUsers = new ArrayList<>() {
//            {
//                for (int i = 0; i < 20; i++) {
//                    add(new ExcelUser(1767110638703833090L, "admin" + i, "admin", "test1", "https://www.baidu.com", "admin", LocalDateTime.now(), LocalDateTime.now(), 0));
//                }
//            }
//        };
//        String s = ExcelUtils.exportLocalFile(excelUsers, "用户表", ExcelUser.class, file.getPath());
//        System.out.println(s);
//        EasyExcel.write(file, ExcelUser.class).sheet("用户表").doWrite(excelUsers);
//        FileInputStream fileInputStream = new FileInputStream(file);
//
//        List<ExcelUser> excelUsers = ExcelUtils.importStreamSync(fileInputStream, ExcelUser.class);
//
//        excelUsers.forEach(System.out::println);
//        System.out.println(JavaVersion.majorVersion());

    }
}
