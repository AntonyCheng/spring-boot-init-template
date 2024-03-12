package top.sharehome.springbootinittemplate.config.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import top.sharehome.springbootinittemplate.config.easyexcel.entity.ExcelUser;

import java.io.File;

/**
 * Excel测试类
 *
 * @author AntonyCheng
 */
public class Main {
    public static void main(String[] args) {

        String projectRootPath = System.getProperty("user.dir");
//
        File file = new File(projectRootPath + "/src/main/java/top/sharehome/springbootinittemplate/config/easyexcel/file/test.xlsx");
//        ArrayList<ExcelUser> excelUsers = new ArrayList<>() {
//            {
//                add(new ExcelUser()
//                        .setId(1767110638703833090L)
//                        .setName("test1")
//                        .setRole("admin")
//                        .setAccount("admin")
//                        .setAvatar("https://www.baidu.com")
//                        .setPassword("admin")
//                        .setCreateTime(new Date())
//                        .setUpdateTime(new Date())
//                        .setIsDeleted(0)
//                );
//            }
//        };
//        EasyExcel.write(file, ExcelUser.class).sheet("用户表").doWrite(excelUsers);

        EasyExcel.read(file, ExcelUser.class, new PageReadListener<ExcelUser>(list -> {
            list.forEach(data -> {
                System.out.println(JSON.toJSONString(data));
            });
        },1));
    }
}
