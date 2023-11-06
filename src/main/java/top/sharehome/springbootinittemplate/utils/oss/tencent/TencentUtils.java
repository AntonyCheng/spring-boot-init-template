package top.sharehome.springbootinittemplate.utils.oss.tencent;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.oss.tencent.TencentConfiguration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 腾讯云COS工具类
 *
 * @author AntonyCheng
 */
@Component
@ConditionalOnProperty(prefix = "oss.tencent", name = "enable", havingValue = "true")
public class TencentUtils {

    /**
     * 被封装的COS对象
     */
    private static final TencentConfiguration COS_CONFIGURATION = SpringContextHolder.getBean(TencentConfiguration.class);

    /**
     * 上传文件
     *
     * @param file     上传的文件数据
     * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件家中，只需要传入"root/a"字符串即可）
     * @return 返回结果
     */
    public static String upload(MultipartFile file, String rootPath) {
        return COS_CONFIGURATION.uploadToCos(file, rootPath);
    }

    /**
     * 删除文件
     *
     * @param url 文件所在地址
     */
    public static void delete(String url) {
        COS_CONFIGURATION.deleteInCos(url);
    }

}