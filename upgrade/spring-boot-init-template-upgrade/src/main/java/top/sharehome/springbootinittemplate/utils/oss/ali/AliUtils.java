package top.sharehome.springbootinittemplate.utils.oss.ali;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.ali.AliConfiguration;

import java.io.InputStream;

/**
 * 阿里云OSS工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class AliUtils {

    /**
     * 被封装的OSS对象
     */
    private static final AliConfiguration ALI_CONFIGURATION = SpringContextHolder.getBean(AliConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
     * @return 文件所在路径
     */
    public static String upload(MultipartFile file, String rootPath) {
        return ALI_CONFIGURATION.uploadToOss(file, rootPath);
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
        return ALI_CONFIGURATION.uploadToOss(inputStream, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param bytes    待上传的文件字节数据
     * @param suffix   文件后缀
     * @param rootPath 上传的路径
     * @return 文件所在路径
     */
    public static String upload(byte[] bytes, String suffix, String rootPath) {
        return ALI_CONFIGURATION.uploadToOss(bytes, suffix, rootPath);
    }

    /**
     * 删除文件
     *
     * @param url 文件所在地址
     */
    public static void delete(String url) {
        ALI_CONFIGURATION.deleteInOss(url);
    }

}