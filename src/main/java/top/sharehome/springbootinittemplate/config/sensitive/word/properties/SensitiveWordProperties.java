package top.sharehome.springbootinittemplate.config.sensitive.word.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 敏感词配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "sensitive.word")
public class SensitiveWordProperties {

    /**
     * 忽略大小写
     */
    private Boolean isIgnoreCase = true;

    /**
     * 忽略半角圆角
     */
    private Boolean isIgnoreWidth = true;

    /**
     * 忽略数字的写法
     */
    private Boolean isIgnoreNumStyle = true;

    /**
     * 忽略中文的书写格式
     */
    private Boolean isIgnoreChineseStyle = true;

    /**
     * 忽略英文的书写格式
     */
    private Boolean isIgnoreEnglishStyle = true;

    /**
     * 忽略重复词
     */
    private Boolean isIgnoreRepeat = false;

    /**
     * 是否启用数字检测
     */
    private Boolean enableNumCheck = false;

    /**
     * 是否启用邮箱检测
     */
    private Boolean enableEmailCheck = false;

    /**
     * 是否启用链接检测
     */
    private Boolean enableUrlCheck = false;

    /**
     * 是否启用IPv4检测
     */
    private Boolean enableIpv4Check = false;

    /**
     * 是否启用敏感单词检测
     */
    private Boolean enableWordCheck = true;

    /**
     * 是否启用默认黑名单
     */
    private Boolean enableDefaultDenys = false;

    /**
     * 数字检测，自定义指定长度
     */
    private Integer numCheckLen = 8;

}
