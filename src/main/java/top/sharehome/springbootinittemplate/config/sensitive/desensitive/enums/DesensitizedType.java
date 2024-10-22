package top.sharehome.springbootinittemplate.config.sensitive.desensitive.enums;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

import static top.sharehome.springbootinittemplate.common.base.Constants.*;

/**
 * 脱敏类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum DesensitizedType {

    /**
     * 全字符串脱敏示例："123abc!@#ABC一二三安东尼" ==> "******************"
     */
    All(s -> {
        String temp = s.toString();
        return temp.length() <= 20 ? "*".repeat(temp.length()) : "********************";
    }),

    /**
     * 用户ID脱敏示例：187382374829948437 ==> "0"
     */
    USER_ID(s -> "0"),

    /**
     * 中文名脱敏示例："王某某" ==> "王**"
     */
    CHINESE_NAME(s -> {
        String temp = s.toString();
        if (!REGEX_CHINESE_NAME_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = temp.length() - 1;
        String start = temp.substring(0, 1);
        String end = maskCount <= 6 ? "*".repeat(maskCount) : "******";
        return start + end;
    }),

    /**
     * 身份证脱敏示例："XXXXXXXXXXXXXXXXXX" ==> "XXX***********XXXX"
     */
    ID_CARD(s -> {
        String temp = s.toString();
        if (!REGEX_ID_CARD_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = temp.length() - 7;
        String start = temp.substring(0, 3);
        String end = temp.substring(14);
        return start + "*".repeat(maskCount) + end;
    }),

    /**
     * 中国手机号码脱敏示例："1XXXXXXXXXX" ==> "1XX****XXXX"
     */
    PHONE(s -> {
        String temp = s.toString();
        if (!REGEX_CHINESE_PHONE_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = 4;
        String start = temp.substring(0, 3);
        String end = temp.substring(7);
        return start + "*".repeat(maskCount) + end;
    }),

    /**
     * 地址脱敏示例："中国北京市海淀区翻斗花园一栋1单元" ==> "中国北京市海淀区*********"
     */
    ADDRESS(s -> {
        String temp = s.toString();
        if (!REGEX_NUMBER_AND_LETTER_AND_CHINESE_CHARACTER_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        if (temp.length() >= 20) {
            temp = temp.substring(0, 21);
        }
        int maskCount = (int) Math.ceil((double) temp.length() / 2);
        String start = temp.substring(0, temp.length() - maskCount);
        return start + "*".repeat(maskCount);
    }),

    /**
     * 电子邮箱脱敏示例："antonycheng@gmail.com" ==> "a**@gmail.com"
     */
    EMAIL(s -> {
        String temp = s.toString();
        if (!REGEX_EMAIL_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        String domain = temp.split("@")[0];
        String suffix = "@" + temp.split("@")[1];
        if (Objects.equal(domain.length(), 1)) {
            return "*" + suffix;
        }
        int maskCount = domain.length() <= 10 ? domain.length() - 1 : 10;
        String prefix = domain.substring(0, 1);
        return prefix + "*".repeat(maskCount) + suffix;
    }),

    /**
     * 密码脱敏示例："123456" ==> "******"
     */
    PASSWORD(s -> "******"),

    /**
     * 车牌脱敏示例：”京A00001" ==> "京A****1"
     */
    LICENSE_PLATE(s -> {
        String temp = s.toString();
        if (!REGEX_LICENSE_PLATE_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = temp.length() - 3;
        String start = temp.substring(0, 2);
        String end = temp.substring(temp.length() - 1);
        return start + "*".repeat(maskCount) + end;
    }),

    /**
     * 银行卡脱敏示例："1111222233334444555" ==> "1111************555"
     */
    BANK_CARD(s -> {
        String temp = s.toString();
        if (!REGEX_BANK_CARD_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = 12;
        String start = temp.substring(0, 4);
        String end = temp.substring(16);
        return start + "*".repeat(maskCount) + end;
    }),

    /**
     * IPv4地址脱敏示例："127.0.0.1" ==> "127.*.*.*"
     */
    IPV_4(s -> {
        String temp = s.toString();
        if (Objects.equal(temp, "localhost")) {
            return "*.*.*.*";
        }
        if (!REGEX_IPV_4_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        String start = temp.split("\\.")[0];
        return start + ".*.*.*";
    }),

    /**
     * IPv6地址脱敏示例：
     * "1234:1234:1234:1234:1234:1234:1234:1234" ==> "1234:*:*:*:*:*:*:*"
     * "::"/"::1" ==> "*:*:*:*:*:*:*:*"
     */
    IPV_6(s -> {
        String temp = s.toString();
        if (Objects.equal(temp, "localhost")) {
            return "*:*:*:*:*:*:*:*";
        }
        if (!REGEX_IPV_6_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        if (temp.startsWith("::")) {
            return "*:*:*:*:*:*:*:*";
        }
        String start = temp.split(":")[1];
        return start + ":*:*:*:*:*:*:*";
    }),

    /**
     * MAC地址脱敏示例：
     * "00:1A:C2:7B:00:47" ==> "00:*:*:*:*:*"
     * "00-1A-C2-7B-00-47" ==> "00-*-*-*-*-*"
     */
    MAC(s -> {
        String temp = s.toString();
        if (!REGEX_MAC_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        String[] split1 = temp.split(":");
        String[] split2 = temp.split("-");
        if (split1.length > 1) {
            String start = split1[0];
            return start + ":*:*:*:*:*";
        } else {
            String start = split2[0];
            return start + "-*-*-*-*-*";
        }
    });

    private final Function<Object, String> desensitizeFunction;

}
