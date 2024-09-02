package top.sharehome.springbootinittemplate.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeEncryptException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

/**
 * AES加密工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class AESUtils {

    /**
     * 生成AES密钥
     *
     * @param keySize 密钥长度（长度只能是16/24/32）
     */
    public static SecretKey generateKey(int keySize) {
        try {
            if (!Objects.equals(keySize * 8, 128)
                    && !Objects.equals(keySize * 8, 192)
                    && !Objects.equals(keySize * 8, 256)) {
                throw new CustomizeEncryptException(ReturnCode.FAIL, "无效的AES密钥长度（需要16/24/32字节）");
            }
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize * 8);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "生成AES密钥出错");
        }
    }

    /**
     * 从字符串获取密钥
     *
     * @param keyString 密钥字符串（字符串长度只能是16/24/32）
     */
    public static SecretKey getKeyFromString(String keyString) {
        if (!Objects.equals(keyString.length(), 16)
                && !Objects.equals(keyString.length(), 24)
                && !Objects.equals(keyString.length(), 32)) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "无效的AES密钥长度（需要16/24/32字节）");
        }
        byte[] decodedKey = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * 将密钥转换为字符串
     *
     * @param secretKey 密钥
     */
    public static String getStringFromKey(SecretKey secretKey) {
        if (Objects.isNull(secretKey)) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥不能为空");
        }
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 使用密钥加密
     *
     * @param plaintext 明文
     * @param secretKey 密钥
     */
    public static String encrypt(String plaintext, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥加密出错", e);
        }
    }

    /**
     * 使用密钥解密
     *
     * @param ciphertext 密文
     * @param secretKey  密钥
     */
    public static String decrypt(String ciphertext, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥解密出错", e);
        }
    }

    /**
     * 自定义密钥加密
     *
     * @param plaintext    明文
     * @param secretKeyStr 密钥字符串（字符串长度只能是16/24/32）
     */
    public static String encrypt(String plaintext, String secretKeyStr) {
        try {
            if (!Objects.equals(secretKeyStr.length(), 16)
                    && !Objects.equals(secretKeyStr.length(), 24)
                    && !Objects.equals(secretKeyStr.length(), 32)) {
                throw new CustomizeEncryptException(ReturnCode.FAIL, "无效的AES密钥长度（需要16/24/32字节）");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥加密出错", e);
        }
    }

    /**
     * 自定义密钥解密
     *
     * @param ciphertext   密文
     * @param secretKeyStr 密钥字符串（字符串长度只能是16/24/32）
     */
    public static String decrypt(String ciphertext, String secretKeyStr) {
        try {
            if (!Objects.equals(secretKeyStr.length(), 16)
                    && !Objects.equals(secretKeyStr.length(), 24)
                    && !Objects.equals(secretKeyStr.length(), 32)) {
                throw new CustomizeEncryptException(ReturnCode.FAIL, "无效的AES密钥长度（需要16/24/32字节）");
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyStr.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CustomizeEncryptException(ReturnCode.FAIL, "密钥解密出错", e);
        }
    }

}
