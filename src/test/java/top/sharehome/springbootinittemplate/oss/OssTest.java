package top.sharehome.springbootinittemplate.oss;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.utils.oss.ali.OssAliUtils;
import top.sharehome.springbootinittemplate.utils.oss.local.OssLocalUtils;
import top.sharehome.springbootinittemplate.utils.oss.minio.OssMinioUtils;
import top.sharehome.springbootinittemplate.utils.oss.tencent.OssTencentUtils;

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
     * 测试本地COS工具类——上传
     */
    @Test
    void testLocalUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(OssLocalUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(OssLocalUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(OssLocalUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试本地COS工具类——删除
     */
    @Test
    void testLocalUtilsDelete() {
        OssLocalUtils.delete(1988886322658824194L);
        OssLocalUtils.delete(1988886353872834561L);
        OssLocalUtils.delete(1988886355105959938L);
    }

    /**
     * 测试腾讯云COS工具类——上传
     */
    @Test
    void testTencentUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(OssTencentUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(OssTencentUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(OssTencentUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testTencentUtilsDelete() {
        OssTencentUtils.delete(1831860566961299458L);
        OssTencentUtils.delete(1831860567590445057L);
        OssTencentUtils.delete(1831860567653359617L);
    }

    /**
     * 测试阿里云OSS工具类——上传
     */
    @Test
    void testAliUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(OssAliUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(OssAliUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(OssAliUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试阿里云OSS工具类——删除
     */
    @Test
    void testAliUtilsDelete() {
        OssAliUtils.delete(1831862560094642177L);
        OssAliUtils.delete(1831862560686039041L);
        OssAliUtils.delete(1831862560820256770L);
    }

    /**
     * 测试MinIO工具类——上传
     */
    @Test
    void testMinioUtilsUpload() throws IOException {
        File file = new File("README.md");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(OssMinioUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(OssMinioUtils.upload(fileInputStream1, file.getName(), suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(OssMinioUtils.upload(bytes, file.getName(), suffix, "test/init"));
    }

    /**
     * 测试MinIO工具类——删除
     */
    @Test
    void testMinioUtilsDelete() {
        OssMinioUtils.delete(1831893437080133633L);
        OssMinioUtils.delete(1831893438057406466L);
        OssMinioUtils.delete(1831893438258733058L);
    }

}