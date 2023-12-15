package top.sharehome.springbootinittemplate.config.oss.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.oss.minio.condition.OssMinioCondition;
import top.sharehome.springbootinittemplate.config.oss.minio.properties.MinioProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeFileException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(OssMinioCondition.class)
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    /**
     * 上传文件到MinIO
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     * @return 文件所在路径
     */
    public String uploadToMinio(MultipartFile file, String rootPath) {
        MinioClient minioClient = getMinioClient();
        String key;
        try {
            if (file == null) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
            String suffix = FilenameUtils.getExtension(originalName);
            if (StringUtils.isEmpty(suffix)) {
                suffix = "." + Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            } else {
                suffix = "." + suffix;
            }
            // 创建一个随机文件名称
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + suffix;
            // 对象键(Key)是对象在存储桶中的唯一标识。
            key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? fileName : rootPath + "/" + fileName;
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(key)
                    .stream(inputStream, inputStream.available(), 5 * 1024 * 1024).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        return (minioProperties.isEnableTls() ? Constants.HTTPS : Constants.HTTP)
                + minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + key;
    }

    /**
     * 上传文件到MinIO
     *
     * @param inputStream 待上传的文件流
     * @param suffix      文件后缀
     * @param rootPath    上传的路径
     * @return 文件所在路径
     */
    public String uploadToMinio(InputStream inputStream, String suffix, String rootPath) {
        MinioClient minioClient = getMinioClient();
        String key;
        try {
            if (ObjectUtils.isEmpty(inputStream)) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            if (StringUtils.isEmpty(suffix)) {
                suffix = "." + Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            } else {
                suffix = "." + suffix;
            }
            // 创建一个随机文件名称
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + suffix;
            // 对象键(Key)是对象在存储桶中的唯一标识。
            key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? fileName : rootPath + "/" + fileName;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(key)
                    .stream(inputStream, inputStream.available(), 5 * 1024 * 1024).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        return (minioProperties.isEnableTls() ? Constants.HTTPS : Constants.HTTP)
                + minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + key;
    }

    public String uploadToMinio(byte[] bytes, String suffix, String rootPath) {
        MinioClient minioClient = getMinioClient();
        String key;
        try {
            if (ObjectUtils.isEmpty(bytes)) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            InputStream inputStream = new ByteArrayInputStream(bytes);
            if (StringUtils.isEmpty(suffix)) {
                suffix = "." + Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            } else {
                suffix = "." + suffix;
            }
            // 创建一个随机文件名称
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + suffix;
            // 对象键(Key)是对象在存储桶中的唯一标识。
            key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? fileName : rootPath + "/" + fileName;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(key)
                    .stream(inputStream, inputStream.available(), 5 * 1024 * 1024).build());
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        return (minioProperties.isEnableTls() ? Constants.HTTPS : Constants.HTTP)
                + minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + key;
    }

    /**
     * 从MinIO中删除文件
     *
     * @param url 文件URL
     */
    public void deleteInMinio(String url) {
        MinioClient minioClient = getMinioClient();
        String[] split = url.split(minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/");
        if (split.length != 2) {
            throw new CustomizeReturnException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL);
        }
        String key = split[1];
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(key).build());
        } catch (Exception e) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
    }

    /**
     * 获取MinioClient客户端
     *
     * @return 返回MinioClient客户端
     */
    private MinioClient getMinioClient() {
        try {
            String[] ipAndPort = minioProperties.getEndpoint().split(":");
            return MinioClient.builder()
                    .endpoint(ipAndPort[0], Integer.parseInt(ipAndPort[1]), minioProperties.isEnableTls())
                    .credentials(minioProperties.getSecretId(), minioProperties.getSecretKey())
                    .build();
        } catch (Exception e) {
            log.error("MinIO服务器构建异常：{}", e.getMessage());
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ minio oss config DI.");
    }
}