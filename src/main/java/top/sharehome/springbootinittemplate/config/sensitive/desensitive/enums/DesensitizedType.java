package top.sharehome.springbootinittemplate.config.sensitive.desensitive.enums;

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
        String first = temp.substring(0, 1);
        String second = maskCount <= 6 ? "*".repeat(maskCount) : "******";
        return first + second;
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
        String end = temp.substring(14, 18);
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
        String end = temp.substring(7, 11);
        return start + "*".repeat(maskCount) + end;
    }),

    /**
     * 地址脱敏示例："中国北京市海淀区翻斗花园一栋1单元" ==> "中国北京市海淀区翻斗花园一****"
     */
    ADDRESS(s -> {
        String temp = s.toString();
        if (!REGEX_NUMBER_AND_LETTER_AND_CHINESE_CHARACTER_PATTERN.matcher(temp).matches()) {
            return temp;
        }
        int maskCount = (int) Math.ceil((double) temp.length() / 2);
        String start = temp.substring(0, temp.length() - maskCount);
        return start + "*".repeat(maskCount);
    }),

    /**
     * 电子邮箱脱敏示例："antonycheng@gmail.com" ==> "a**@gmail.com"
     */
//    EMAIL(DesensitizedUtil::email),

    /**
     * 密码脱敏示例："123456" ==> "******"
     */
    PASSWORD(s -> "******"),

    /**
     * 车牌脱敏示例：”京A00001" ==> "京A****1"
     */
//    LICENSE_PLATE_NUMBER(DesensitizedUtil::carLicense),

    /**
     * 银行卡脱敏示例："1111 2222 3333 4444 5555 6" ==> "1111 **** **** **** 6"
     */
//    BANK_CARD(DesensitizedUtil::bankCard);
    ;
    private final Function<Object, String> desensitizeFunction;

}
