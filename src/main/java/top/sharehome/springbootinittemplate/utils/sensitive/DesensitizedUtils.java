package top.sharehome.springbootinittemplate.utils.sensitive;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.config.sensitive.desensitive.enums.DesensitizedType;

import java.util.Objects;

/**
 * 脱敏工具类
 * 解释：脱敏实际上是对字符串进行信息遮挡，通常遮挡符号为”*“
 *
 * @author AntonyCheng
 */
@Slf4j
public class DesensitizedUtils {

    public static String maskLeft(String str, Double proportion) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        if (Objects.isNull(proportion) || proportion <= 0 || proportion >= 1) {
            proportion = 0.5;
        }
        int length = str.length();
        if (Objects.equals(length, 1)) {
            return "*";
        }
        int maskLength = (int) Math.ceil(length * proportion);
        if (maskLength >= length) {
            maskLength = length - 1;
        }
        String maskedPart = StringUtils.repeat("*", maskLength);
        return maskedPart + str.substring(maskLength);
    }

    public static String maskMiddle(String str, Double proportion) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        if (Objects.isNull(proportion) || proportion <= 0 || proportion >= 1) {
            proportion = 0.5;
        }
        int length = str.length();
        if (Objects.equals(length, 1)) {
            return "*";
        }
        if (Objects.equals(length, 2)) {
            return str.charAt(0) + "*";
        }
        int maskLength = (int) Math.ceil(length * proportion);
        if (maskLength >= length - 2) {
            maskLength = length - 2;
        }
        int startIndex = (length - maskLength) / 2;
        String maskedPart = StringUtils.repeat("*", maskLength);
        return str.substring(0, startIndex) + maskedPart + str.substring(startIndex + maskLength);
    }

    public static String maskRight(String str, Double proportion) {
        if (StringUtils.isBlank(str)) {
            return "";
        }
        if (Objects.isNull(proportion) || proportion <= 0 || proportion >= 1) {
            proportion = 0.5;
        }
        int length = str.length();
        if (Objects.equals(length, 1)) {
            return "*";
        }
        int maskLength = (int) Math.ceil(length * proportion);
        if (maskLength >= length) {
            maskLength = length - 1;
        }
        String maskedPart = StringUtils.repeat("*", maskLength);
        return str.substring(0, length - maskLength) + maskedPart;
    }

    public static String all(String str) {
        return DesensitizedType.ALL.getFunction().apply(str);
    }

    public static String toEmpty(String str) {
        return DesensitizedType.TO_EMPTY.getFunction().apply(str);
    }

    public static String toNull(String str) {
        return DesensitizedType.TO_NULL.getFunction().apply(str);
    }

    public static String userId(Object id) {
        return DesensitizedType.USER_ID.getFunction().apply(id);
    }

    public static String chineseName(String str) {
        return DesensitizedType.CHINESE_NAME.getFunction().apply(str);
    }

    public static String idCard(String str) {
        return DesensitizedType.ID_CARD.getFunction().apply(str);
    }

    public static String phone(String str) {
        return DesensitizedType.PHONE.getFunction().apply(str);
    }

    public static String address(String str) {
        return DesensitizedType.ADDRESS.getFunction().apply(str);
    }

    public static String email(String str) {
        return DesensitizedType.EMAIL.getFunction().apply(str);
    }

    public static String password(String str) {
        return DesensitizedType.PASSWORD.getFunction().apply(str);
    }

    public static String licensePlate(String str) {
        return DesensitizedType.LICENSE_PLATE.getFunction().apply(str);
    }

    public static String bankCard(String str) {
        return DesensitizedType.BANK_CARD.getFunction().apply(str);
    }

    public static String ipv4(String str) {
        return DesensitizedType.IPV_4.getFunction().apply(str);
    }

    public static String ipv6(String str) {
        return DesensitizedType.IPV_6.getFunction().apply(str);
    }

    public static String mac(String str) {
        return DesensitizedType.MAC.getFunction().apply(str);
    }

}
