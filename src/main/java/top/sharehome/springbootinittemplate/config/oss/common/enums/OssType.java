package top.sharehome.springbootinittemplate.config.oss.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对象存储类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum OssType {

    /**
     * MinIO
     */
    MINIO("minio"),

    /**
     * 阿里云OSS
     */
    ALI("ali"),

    /**
     * 腾讯云COS
     */
    TENCENT("tencent");

    private final String typeName;

}
