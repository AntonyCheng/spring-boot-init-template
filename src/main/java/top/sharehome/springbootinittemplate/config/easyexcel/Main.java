package top.sharehome.springbootinittemplate.config.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import top.sharehome.springbootinittemplate.config.easyexcel.entity.ExcelUser;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Excel测试类
 *
 * @author AntonyCheng
 */
public class Main {
    public static void main(String[] args) {

        String projectRootPath = System.getProperty("user.dir");

        File file = new File(projectRootPath + "/src/main/java/top/sharehome/springbootinittemplate/config/easyexcel/file/test.xlsx");
        ArrayList<ExcelUser> excelUsers = new ArrayList<>() {
            {
                for (int i = 0; i < 5; i++) {
                    add(new ExcelUser()
                            .setId(1767110638703833090L)
                            .setName("test1")
                            .setRole("admin")
                            .setAccount("admin")
                            .setAvatar("https://www.baidu.com")
                            .setPassword("admin")
                            .setCreateTime(LocalDateTime.now())
                            .setUpdateTime(LocalDateTime.now())
                            .setIsDeleted(0)
                    );
                }
            }
        };
        EasyExcel.write(file, ExcelUser.class).sheet("用户表").doWrite(excelUsers);

        EasyExcel.read(file, ExcelUser.class, new PageReadListener<ExcelUser>(list -> {
            list.forEach(data -> {
                System.out.println(JSON.toJSONString(data));
            });
        })).sheet().doRead();
    }
}
