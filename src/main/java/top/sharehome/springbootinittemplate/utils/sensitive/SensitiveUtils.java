package top.sharehome.springbootinittemplate.utils.sensitive;

import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.sensitive.SensitiveConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 * 敏感词检查器
 *
 * @author AntonyCheng
 */
@Slf4j
public class SensitiveUtils {

    private static final SensitiveConfiguration SENSITIVE = SpringContextHolder.getBean("SensitiveConfiguration0", SensitiveConfiguration.class);

    public static Boolean hasSensitive(String target) {
        return SENSITIVE.hasSensitive(target);
    }

    public static String getFirstSensitive(String target) {
        return SENSITIVE.getFirstSensitive(target);
    }

    public static List<String> getSensitiveList(String target) {
        return SENSITIVE.getSensitiveList(target);
    }

    public static void addSensitiveAllow(String... words) {
        SENSITIVE.addWordAllow(Arrays.stream(words).toList());
    }

    public static void addSensitiveAllow(List<String> words) {
        SENSITIVE.addWordAllow(words);
    }

    public static void removeSensitiveAllow(String... words) {
        SENSITIVE.removeWordAllow(Arrays.stream(words).toList());
    }

    public static void removeSensitiveAllow(List<String> words) {
        SENSITIVE.removeWordAllow(words);
    }

    public static void addSensitiveDeny(String... words) {
        SENSITIVE.addWordDeny(Arrays.stream(words).toList());
    }

    public static void addSensitiveDeny(List<String> words) {
        SENSITIVE.addWordDeny(words);
    }

    public static void removeSensitiveDeny(String... words) {
        SENSITIVE.removeWordDeny(Arrays.stream(words).toList());
    }

    public static void removeSensitiveDeny(List<String> words) {
        SENSITIVE.removeWordDeny(words);
    }

}
