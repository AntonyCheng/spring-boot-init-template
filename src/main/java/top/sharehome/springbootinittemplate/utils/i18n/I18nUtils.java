package top.sharehome.springbootinittemplate.utils.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化工具类
 *
 * @author AntonyCheng
 */
@Component
public class I18nUtils {

    private static MessageSource messageSource;

    @Autowired
    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        return getMessage(key, null);
    }

    public static String getMessage(String key, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

}