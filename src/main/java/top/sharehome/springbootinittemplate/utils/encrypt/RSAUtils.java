package top.sharehome.springbootinittemplate.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeEncryptException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

/**
 * RSA加密工具类
 * 使用前需要确保自己有RSA算法公钥和私钥，可以通过generateKeyPair方法生成密钥对
 *
 * @author AntonyCheng
 */
@Slf4j
public class RSAUtils {

    /**
     * 生成RSA密钥对
     *
     * @param keySize 密钥长度（必须是512的倍数，且不小于512，推荐2048或4096）
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            if (!Objects.equals(keySize % 512, 0) || keySize < 512) {
                throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥长度必须是512的倍数，且不小于512位");
            }
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "生成RSA密钥对出错");
        }
    }

    /**
     * 从字符串获取公钥
     *
     * @param keyString 公钥字符串
     */
    public static PublicKey getPublicKeyFromString(String keyString) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(keyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "从字符串获取公钥出错", e);
        }
    }

    /**
     * 从字符串获取私钥
     *
     * @param keyString 私钥字符串
     */
    public static PrivateKey getPrivateKeyFromString(String keyString) {
        try {
            byte[] byteKey = Base64.getDecoder().decode(keyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(byteKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "从字符串获取私钥出错", e);
        }
    }

    /**
     * 将公钥转换为字符串
     *
     * @param publicKey 公钥
     */
    public static String getStringFromPublicKey(PublicKey publicKey) {
        try {
            return Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "将公钥转换为字符串出错", e);
        }
    }

    /**
     * 将私钥转换为字符串
     *
     * @param privateKey 私钥
     */
    public static String getStringFromPrivateKey(PrivateKey privateKey) {
        try {
            return Base64.getEncoder().encodeToString(privateKey.getEncoded());
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "将私钥转换为字符串出错", e);
        }
    }

    /**
     * 使用公钥加密
     *
     * @param plaintext 明文
     * @param publicKey 公钥
     */
    public static String encrypt(String plaintext, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "公钥加密出错", e);
        }
    }

    /**
     * 使用私钥解密
     *
     * @param ciphertext 密文
     * @param privateKey 私钥
     */
    public static String decrypt(String ciphertext, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "私钥解密出错", e);
        }
    }

    /**
     * 自定义公钥加密
     *
     * @param plaintext    明文
     * @param publicKeyStr 公钥字符串
     */
    public static String encrypt(String plaintext, String publicKeyStr) {
        try {
            PublicKey publicKey = getPublicKeyFromString(publicKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "公钥加密出错", e);
        }
    }

    /**
     * 自定义私钥解密
     *
     * @param ciphertext    密文
     * @param privateKeyStr 私钥字符串
     */
    public static String decrypt(String ciphertext, String privateKeyStr) {
        try {
            PrivateKey privateKey = getPrivateKeyFromString(privateKeyStr);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "私钥解密出错", e);
        }
    }

}
