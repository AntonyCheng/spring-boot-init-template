package top.sharehome.springbootinittemplate.config.encrypt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RSA密钥长度
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum RSAKeyLength {

    LENGTH512(512),

    LENGTH1024(1024),

    LENGTH2048(2048),

    LENGTH4096(4096);

    private final Integer keyLength;

}
