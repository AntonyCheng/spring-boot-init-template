package top.sharehome.springbootinittemplate.utils.oss.minio;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.minio.MinioConfiguration;
import top.sharehome.springbootinittemplate.config.oss.minio.condition.OssMinioCondition;

import java.io.InputStream;

/**
 * MinIO工具类
 *
 * @author AntonyCheng
 */
@Component
@Conditional(OssMinioCondition.class)
public class MinioUtils {

    /**
     * 被封装的MinIO对象
     */
    private static final MinioConfiguration MINIO_CONFIGURATION = SpringContextHolder.getBean(MinioConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
     * @return 文件所在路径
     */
    public static String upload(MultipartFile file, String rootPath) {
        return MINIO_CONFIGURATION.uploadToMinio(file, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     * @return 文件所在路径
     */
    public static String upload(InputStream inputStream, String suffix, String rootPath) {
        return MINIO_CONFIGURATION.uploadToMinio(inputStream, suffix, rootPath);
    }

    /**
     * 删除文件
     *
     * @param url 文件所在地址
     */
    public static void delete(String url) {
        MINIO_CONFIGURATION.deleteInMinio(url);
    }

}