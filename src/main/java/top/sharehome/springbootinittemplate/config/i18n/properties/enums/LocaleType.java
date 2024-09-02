package top.sharehome.springbootinittemplate.config.i18n.properties.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

/**
 * 国际化语言枚举类
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum LocaleType {

    // 注意格式：
    // 1、Locale类中构造参数：language全小写，country全大写
    // 2、枚举中枚举名称均大写并以"_"隔开
    EN_US(new Locale("en", "US")),

    ZH_CN(new Locale("zh", "CN")),

    ZH_TW(new Locale("zh", "TW"));

    final private Locale locale;

}