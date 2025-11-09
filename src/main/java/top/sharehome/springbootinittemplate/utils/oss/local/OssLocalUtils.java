package top.sharehome.springbootinittemplate.utils.oss.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.service.local.OssLocalConfiguration;
import top.sharehome.springbootinittemplate.model.entity.File;

import java.io.InputStream;

/**
 * 本地OSS工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class OssLocalUtils {

    /**
     * 被封装的OSS对象
     */
    private static final OssLocalConfiguration OSS_LOCAL_CONFIGURATION = SpringContextHolder.getBean(OssLocalConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
     * @return 文件所在路径
     */
    public static File upload(MultipartFile file, String rootPath) {
        return OSS_LOCAL_CONFIGURATION.uploadToOss(file, rootPath);
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
        return OSS_LOCAL_CONFIGURATION.uploadToOss(bytes, null, suffix, rootPath);
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
        return OSS_LOCAL_CONFIGURATION.uploadToOss(bytes, originalName, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     */
    public static File upload(InputStream inputStream, String suffix, String rootPath) {
        return OSS_LOCAL_CONFIGURATION.uploadToOss(inputStream, null, suffix, rootPath);
    }

    /**
     * 上传文件
     *
     * @param inputStream  待上传的文件流
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     */
    public static File upload(InputStream inputStream, String originalName, String suffix, String rootPath) {
        return OSS_LOCAL_CONFIGURATION.uploadToOss(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    public static void delete(Long id) {
        OSS_LOCAL_CONFIGURATION.deleteInOss(id);
    }

}