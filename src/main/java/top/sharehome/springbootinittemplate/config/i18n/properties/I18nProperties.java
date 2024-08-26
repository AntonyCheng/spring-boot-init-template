package top.sharehome.springbootinittemplate.config.i18n.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.sharehome.springbootinittemplate.config.i18n.properties.enums.LocaleType;

/**
 * 国际化配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "spring.messages")
public class I18nProperties {

    /**
     * 默认语言，默认简体中文
     */
    private LocaleType defaultLocale = LocaleType.ZH_CN;

}