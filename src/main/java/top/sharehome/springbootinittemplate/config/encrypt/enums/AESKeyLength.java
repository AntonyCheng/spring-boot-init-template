package top.sharehome.springbootinittemplate.config.encrypt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AES密钥长度
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum AESKeyLength {

    LENGTH16(16),

    LENGTH24(24),

    LENGTH32(32);

    private final Integer keyLength;

}
