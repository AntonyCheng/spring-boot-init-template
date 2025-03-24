package top.sharehome.springbootinittemplate.config.ai.spring.service.transcription.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 语言类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum LanguageType {

    English("英语", "en"),

    Chinese("中文", "zh"),

    Spanish("西班牙语", "es"),

    French("法语", "fr"),

    German("德语", "de"),

    Japanese("日语", "ja"),

    Korean("韩语", "ko"),

    Russian("俄语", "ru"),

    Arabic("阿拉伯语", "ar"),

    Portuguese("葡萄牙语", "pt"),

    Italian("意大利语", "it"),

    Dutch("荷兰语", "nl"),

    Swedish("瑞典语", "sv"),

    Thai("泰语", "th"),

    Vietnamese("越南语", "vi");

    private final String label;

    private final String value;

}
