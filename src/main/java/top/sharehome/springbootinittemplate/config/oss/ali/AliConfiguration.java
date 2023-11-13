package top.sharehome.springbootinittemplate.config.oss.ali;

import cn.hutool.core.io.FileUtil;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.oss.ali.condition.OssAliCondition;
import top.sharehome.springbootinittemplate.config.oss.ali.properties.AliProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeFileException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(AliProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(OssAliCondition.class)
public class AliConfiguration {

    private final AliProperties aliProperties;

    /**
     * 获取OSSClient客户端
     *
     * @return 返回OSSClient客户端
     */
    private OSS getOssClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(aliProperties.getSecretId(), aliProperties.getSecretKey());
        // 获取配置类中的域名
        String endpoint = aliProperties.getEndpoint();
        // 创建ClientBuilderConfiguration。
        // ClientBuilderConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setProtocol(Protocol.HTTPS);
        return new OSSClientBuilder().build(endpoint, credentialsProvider, conf);
    }

    /**
     * 上传文件到OSS
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     */
    public String uploadToOss(MultipartFile file, String rootPath) {
        OSS ossClient = getOssClient();
        String key;
        try {
            if (file == null) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            String filename = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
            if (StringUtils.isBlank(FileUtil.getSuffix(filename))) {
                filename = filename + Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            }
            String namePrefix = UUID.randomUUID().toString().replaceAll("-", "");
            key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? namePrefix + "_" + filename : rootPath + "/" + namePrefix + "_" + filename;
            InputStream inputStream = file.getInputStream();
            // 上传文件的同时指定进度条参数。此处PutObjectProgressListenerDemo为调用类的类名，请在实际使用时替换为相应的类名。
            ossClient.putObject(new PutObjectRequest(aliProperties.getBucketName(), key, inputStream).<PutObjectRequest>withProgressListener(new PutObjectProgressListener()));
        } catch (OSSException | ClientException | IOException oe) {
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return Constants.HTTPS + aliProperties.getBucketName() + "." + aliProperties.getEndpoint().split(Constants.HTTPS)[1] + "/" + key;
    }

    /**
     * 从OSS中删除文件
     *
     * @param url 文件URL
     */
    public void deleteInOss(String url) {
        OSS ossClient = getOssClient();
        String[] split = url.split(aliProperties.getBucketName() + "." + aliProperties.getEndpoint().split(Constants.HTTPS)[1] + "/");
        if (split.length != 2) {
            throw new CustomizeReturnException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL);
        }
        String key = split[1];
        try {
            ossClient.deleteObject(aliProperties.getBucketName(), key);
        } catch (OSSException | ClientException e) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ ali oss config DI.");
    }

}