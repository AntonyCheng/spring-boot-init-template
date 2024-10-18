package top.sharehome.springbootinittemplate.oss;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
public class OssTest {

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
        System.out.println(TencentUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(TencentUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testTencentUtilsDelete() {
        TencentUtils.delete(1831860566961299458L);
        TencentUtils.delete(1831860567590445057L);
        TencentUtils.delete(1831860567653359617L);
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
        System.out.println(AliUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(AliUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试阿里云OSS工具类——删除
     */
    @Test
    void testAliUtilsDelete() {
        AliUtils.delete(1831862560094642177L);
        AliUtils.delete(1831862560686039041L);
        AliUtils.delete(1831862560820256770L);
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
        System.out.println(MinioUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(MinioUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试MinIO工具类——删除
     */
    @Test
    void testMinioUtilsDelete() {
        MinioUtils.delete(1831893437080133633L);
        MinioUtils.delete(1831893438057406466L);
        MinioUtils.delete(1831893438258733058L);
    }

}