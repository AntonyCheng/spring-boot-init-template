package top.sharehome.springbootinittemplate.config.encrypt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.utils.encrypt.AESUtils;
import top.sharehome.springbootinittemplate.utils.encrypt.RSAUtils;

import javax.crypto.SecretKey;
import java.security.KeyPair;

/**
 * 加密配置
 *
 * @author AntonyCheng
 */
@Configuration
@Slf4j
public class EncryptConfiguration {

    public static String RSAPublicKey = null;

    public static String RSAPrivateKey = null;

    public static String AESSecretKey = null;

    static {
        KeyPair keyPair = RSAUtils.generateKeyPair(4096);
        RSAPublicKey = RSAUtils.getStringFromPublicKey(keyPair.getPublic());
        RSAPrivateKey = RSAUtils.getStringFromPrivateKey(keyPair.getPrivate());
        SecretKey secretKey = AESUtils.generateKey(32);
        AESSecretKey = AESUtils.getStringFromKey(secretKey);
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
