package top.sharehome.springbootinittemplate.config.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.utils.encrypt.AESUtils;
import top.sharehome.springbootinittemplate.utils.encrypt.RSAUtils;

import javax.annotation.PostConstruct;
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
@Slf4j
public class EncryptConfiguration {

    public static String RSAPublicKey = null;

    public static String RSAPrivateKey = null;

    public static String AESSecretKey = null;

    static {
        CompletableFuture.runAsync(() -> {
            KeyPair keyPair = RSAUtils.generateKeyPair(4096);
            RSAPublicKey = RSAUtils.getStringFromPublicKey(keyPair.getPublic());
            RSAPrivateKey = RSAUtils.getStringFromPrivateKey(keyPair.getPrivate());
            SecretKey secretKey = AESUtils.generateKey(32);
            AESSecretKey = AESUtils.getStringFromKey(secretKey);
            log.info("############ Encryption Key is Generated");
        });
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
