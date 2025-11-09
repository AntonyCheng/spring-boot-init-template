package top.sharehome.springbootinittemplate.utils.oss.minio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.service.minio.OssMinioConfiguration;
import top.sharehome.springbootinittemplate.model.entity.File;

import java.io.InputStream;

/**
 * MinIO工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class OssMinioUtils {

    /**
     * 被封装的MinIO对象
     */
    private static final OssMinioConfiguration OSS_MINIO_CONFIGURATION = SpringContextHolder.getBean(OssMinioConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
     */
    public static File upload(MultipartFile file, String rootPath) {
        return OSS_MINIO_CONFIGURATION.uploadToMinio(file, rootPath);
    }

    /**
     * 上传文件
     *
     * @param bytes    待上传的文件字节数组
     * @param suffix   文件后缀
     * @param rootPath 上传的路径
     */
    public static File upload(byte[] bytes, String suffix, String rootPath) {
        return OSS_MINIO_CONFIGURATION.uploadToMinio(bytes, null, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param bytes    待上传的文件字节数组
     * @param suffix   文件后缀
     * @param rootPath 上传的路径
     */
    public static File upload(byte[] bytes, String originalName, String suffix, String rootPath) {
        return OSS_MINIO_CONFIGURATION.uploadToMinio(bytes, originalName, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     */
    public static File upload(InputStream inputStream, String suffix, String rootPath) {
        return OSS_MINIO_CONFIGURATION.uploadToMinio(inputStream, null, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     */
    public static File upload(InputStream inputStream, String originalName, String suffix, String rootPath) {
        return OSS_MINIO_CONFIGURATION.uploadToMinio(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    public static void delete(Long id) {
        OSS_MINIO_CONFIGURATION.deleteInMinio(id);
    }

}