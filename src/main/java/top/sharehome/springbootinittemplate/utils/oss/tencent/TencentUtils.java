package top.sharehome.springbootinittemplate.utils.oss.tencent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.tencent.TencentConfiguration;
import top.sharehome.springbootinittemplate.model.entity.File;

import java.io.InputStream;

/**
 * 腾讯云COS工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class TencentUtils {

    /**
     * 被封装的COS对象
     */
    private static final TencentConfiguration TENCENT_CONFIGURATION = SpringContextHolder.getBean(TencentConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
     * @return 返回结果
     */
    public static File upload(MultipartFile file, String rootPath) {
        return TENCENT_CONFIGURATION.uploadToCos(file, rootPath);
    }

    /**
     * 上传文件
     *
     * @param bytes    待上传的文件字节数组
     * @param suffix   文件后缀
     * @param rootPath 上传的路径
     * @return 文件所在路径
     */
    public static File upload(byte[] bytes, String suffix, String rootPath) {
        return TENCENT_CONFIGURATION.uploadToCos(bytes, null, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param bytes        待上传的文件字节数组
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     * @return 文件所在路径
     */
    public static File upload(byte[] bytes, String originalName, String suffix, String rootPath) {
        return TENCENT_CONFIGURATION.uploadToCos(bytes, originalName, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     * @return 文件所在路径
     */
    public static File upload(InputStream inputStream, String suffix, String rootPath) {
        return TENCENT_CONFIGURATION.uploadToCos(inputStream, null, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream  待上传的文件流
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     * @return 文件所在路径
     */
    public static File upload(InputStream inputStream, String originalName, String suffix, String rootPath) {
        return TENCENT_CONFIGURATION.uploadToCos(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 删除文件
     *
     * @param url 文件所在地址
     */
    public static void delete(String url) {
        TENCENT_CONFIGURATION.deleteInCos(url);
    }

}