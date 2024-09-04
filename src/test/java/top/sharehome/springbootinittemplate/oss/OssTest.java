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
        File file = new File("pom.xml");
        FileInputStream fileInputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), fileInputStream);
        System.out.println(TencentUtils.upload(multipartFile, "test/init"));

        String suffix = FilenameUtils.getExtension(file.getName());
        FileInputStream fileInputStream1 = new FileInputStream(file);
        System.out.println(TencentUtils.upload(fileInputStream1, suffix, "test/init"));

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(TencentUtils.upload(bytes, suffix, "test/init"));
    }

    /**
     * 测试腾讯云COS工具类——删除
     */
    @Test
    void testTencentUtilsDelete() {
        TencentUtils.delete("https://xxxxxxxx.cos.xx-xxx.myqcloud.com/test/init/e62fe3589cd44957865a77da7b9f1db91725288883715.md");
        TencentUtils.delete("https://xxxxxxxx.cos.xx-xxx.myqcloud.com/test/init/334b33d3f6e448dfa48363e54ae32c641725288885957.md");
        TencentUtils.delete("https://xxxxxxxx.cos.xx-xxx.myqcloud.com/test/init/074fb57812b54740b9c11ac40044d0f81725288886966.md");
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

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(AliUtils.upload(bytes, suffix, "test/init"));
    }

    /**
     * 测试阿里云OSS工具类——删除
     */
    @Test
    void testAliUtilsDelete() {
        AliUtils.delete("https://xxxxxxxx.oss-cn-beijing.aliyuncs.com/test/init/6acdb8e0ba3547129c5f3a01e359e5fc1725289306886.md");
        AliUtils.delete("https://xxxxxxxx.oss-cn-beijing.aliyuncs.com/test/init/bbce61c48969415299b5ba30339a044d1725289307614.md");
        AliUtils.delete("https://xxxxxxxx.oss-cn-beijing.aliyuncs.com/test/init/3c0cd5a48c0e482e9c59e2309bd3c4a81725289307969.md");
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

        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(MinioUtils.upload(bytes, suffix, "test/init"));
    }

    /**
     * 测试MinIO工具类——删除
     */
    @Test
    void testMinioUtilsDelete() {
        MinioUtils.delete("http://127.0.0.1:9000/xxxxxxxx/test/init/2b88be9a7fa44e78ae0df33e9d7df3391725288694737.md");
        MinioUtils.delete("http://127.0.0.1:9000/xxxxxxxx/test/init/d6a325c9df184bb4a2451c0733b8a1001725288695159.md");
        MinioUtils.delete("http://127.0.0.1:9000/xxxxxxxx/test/init/1967793e9bb642a48815f62f72a55ddb1725288695188.md");
    }

}