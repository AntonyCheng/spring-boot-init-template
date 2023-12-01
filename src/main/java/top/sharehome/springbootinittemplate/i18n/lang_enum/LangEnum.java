package top.sharehome.springbootinittemplate.i18n.lang_enum;

import lombok.Getter;

import java.util.Locale;

/**
 * 国际化语言枚举类
 *
 * @author AntonyCheng
 */
@Getter
public enum LangEnum {

    zh_CN(new Locale("zh", "CN")),

    zh_TW(new Locale("zh", "TW")),

    en_US(new Locale("en", "US"));

    final private Locale locale;

    LangEnum(Locale locale) {
        this.locale = locale;
    }

}