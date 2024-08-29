package top.sharehome.springbootinittemplate.config.encrypt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.sharehome.springbootinittemplate.config.encrypt.enums.AESKeyLength;
import top.sharehome.springbootinittemplate.config.encrypt.enums.RSAKeyLength;

/**
 * 请求参数加密配置
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "encrypt")
public class EncryptProperties {

    /**
     * 请求参数加密开关
     */
    private Boolean enable = false;

    /**
     * RSA密钥长度
     */
    private RSAKeyLength rsaKeyLength = RSAKeyLength.LENGTH2048;

    /**
     * AES密钥长度
     */
    private AESKeyLength aesKeyLength = AESKeyLength.LENGTH32;

}