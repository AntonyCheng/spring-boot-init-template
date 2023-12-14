package top.sharehome.springbootinittemplate.oss;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.utils.oss.ali.AliUtils;
import top.sharehome.springbootinittemplate.utils.oss.minio.MinioUtils;
import top.sharehome.springbootinittemplate.utils.oss.tencent.TencentUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 测试对象存储类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class TestOss {

    /**
     * 测试腾讯云COS工具类——上传
     */
    @Test
    void testTencentUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(TencentUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(TencentUtils.upload(fileInputStream1, suffix, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testTencentUtilsDelete() {
        TencentUtils.delete("https://test-1306588126.cos.ap-chengdu.myqcloud.com/test/init/61f122e335934970be3f2b14eeef37c8_README.md");
    }

    /**
     * 测试阿里云OSS工具类——上传
     */
    @Test
    void testAliUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(AliUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(AliUtils.upload(fileInputStream1, suffix, "test/init"));
    }

    /**
     * 测试阿里云OSS工具类——删除
     */
    @Test
    void testAliUtilsDelete() {
        AliUtils.delete("https://xxxxxx.oss-cn-beijing.aliyuncs.com/test/init/33ce4679377b48e9a733d95deaf43975_README.md");
    }

    /**
     * 测试MinIO工具类——上传
     */
    @Test
    void testMinioUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(MinioUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(MinioUtils.upload(fileInputStream1, suffix, "test/init"));
        fileInputStream.close();
    }

    /**
     * 测试MinIO工具类——删除
     */
    @Test
    void testMinioUtilsDelete() {
        MinioUtils.delete("http://xxx.xxx.xxx.xxx:39000/xxxxxxxx/test/init/9d6ff8990d3a47b5a2856dc1d06e97ac_README.md");
    }

}