package top.sharehome.springbootinittemplate.config.i18n;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.i18n.condition.I18nCondition;

import javax.annotation.PostConstruct;
import java.util.Locale;

/**
 * 国际化工具类
 *
 * @author AntonyCheng
 */
@Component
@Conditional(I18nCondition.class)
@Slf4j
public class I18nManager {

    private static MessageSource messageSource;

    @Autowired
    public I18nManager(MessageSource messageSource) {
        I18nManager.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        return getMessage(key, null);
    }

    public static String getMessage(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName());
    }

}