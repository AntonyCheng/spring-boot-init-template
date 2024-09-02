package top.sharehome.springbootinittemplate.config.encrypt;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.config.encrypt.properties.EncryptProperties;
import top.sharehome.springbootinittemplate.utils.encrypt.AESUtils;
import top.sharehome.springbootinittemplate.utils.encrypt.RSAUtils;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.concurrent.CompletableFuture;

/**
 * 加密配置
 *
 * @author AntonyCheng
 */
@Configuration
@Conditional({EncryptCondition.class})
@EnableConfigurationProperties(EncryptProperties.class)
@Slf4j
@Getter
public class EncryptConfiguration {

    @Resource
    private EncryptProperties encryptProperties;

    private String rsaPublicKey = null;

    private String rsaPrivateKey = null;

    private String aesSecretKey = null;

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        CompletableFuture.runAsync(() -> {
            KeyPair keyPair = RSAUtils.generateKeyPair(encryptProperties.getRsaKeyLength().getKeyLength());
            rsaPublicKey = RSAUtils.getStringFromPublicKey(keyPair.getPublic());
            rsaPrivateKey = RSAUtils.getStringFromPrivateKey(keyPair.getPrivate());
            SecretKey secretKey = AESUtils.generateKey(encryptProperties.getAesKeyLength().getKeyLength());
            aesSecretKey = AESUtils.getStringFromKey(secretKey);
            log.info("############ Encryption Key is Generated");
        });
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
