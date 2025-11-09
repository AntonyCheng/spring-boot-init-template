package top.sharehome.springbootinittemplate.config.oss.service.ali;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.oss.common.enums.OssType;
import top.sharehome.springbootinittemplate.config.oss.service.ali.condition.OssAliCondition;
import top.sharehome.springbootinittemplate.config.oss.service.ali.properties.OssAliProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeFileException;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.utils.encrypt.SHA3Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 阿里云OSS配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(OssAliProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(OssAliCondition.class)
public class OssAliConfiguration {

    private final OssAliProperties ossAliProperties;

    private final FileService fileService;

    /**
     * 上传文件到OSS
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     */
    public File uploadToOss(MultipartFile file, String rootPath) {
        if (Objects.isNull(file)) {
            throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
        }
        String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
        String suffix = FilenameUtils.getExtension(originalName);
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        // 使用Aop代理类进行上传操作，保证事物生效
        return ((OssAliConfiguration) AopContext.currentProxy()).uploadToOss(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 上传文件到OSS
     *
     * @param bytes        待上传的文件字节数组
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     */
    public File uploadToOss(byte[] bytes, String originalName, String suffix, String rootPath) {
        if (ObjectUtils.isEmpty(bytes)) {
            throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        // 使用Aop代理类进行上传操作，保证事物生效
        return ((OssAliConfiguration) AopContext.currentProxy()).uploadToOss(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 上传文件到OSS
     *
     * @param inputStream  待上传的文件流
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadToOss(InputStream inputStream, String originalName, String suffix, String rootPath) {
        OSS ossClient = getOssClient();
        try (inputStream) {
            if (Objects.isNull(inputStream)) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            if (StringUtils.isEmpty(suffix)) {
                suffix = Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            } else {
                suffix = suffix.toLowerCase();
            }
            if (StringUtils.isBlank(originalName)) {
                originalName = "none" + "." + suffix;
            }
            byte[] dataBytes = inputStream.readAllBytes();
            String plainKey = new String(dataBytes) + originalName + suffix;
            // 检查是否能进行秒传，如果能就直接返回秒传结果，否则进行普通上传
            String uniqueKey = SHA3Utils.encrypt(plainKey);
            File fastUploadResult = existAndFastUpload(uniqueKey);
            if (Objects.nonNull(fastUploadResult)) {
                return fastUploadResult;
            }
            // 接下来进行普通上传，创建一个随机文件名称
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + "." + suffix;
            // 对象键(Key)是对象在存储桶中的唯一标识。
            String key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? fileName : rootPath + "/" + fileName;
            // 封装输出流为ByteArrayInputStream
            ByteArrayInputStream tempInputStream = new ByteArrayInputStream(dataBytes);
            // 上传文件的同时指定进度条参数。此处PutObjectProgressListenerDemo为调用类的类名，请在实际使用时替换为相应的类名。
            ossClient.putObject(new PutObjectRequest(ossAliProperties.getBucketName(), key, tempInputStream).withProgressListener(new PutObjectProgressListener()));
            // 添加新文件
            String url = Constants.HTTPS + ossAliProperties.getBucketName() + "." + ossAliProperties.getEndpoint().split(Constants.HTTPS)[1] + "/" + key;
            File newFile = new File()
                    .setUniqueKey(uniqueKey)
                    .setName(key)
                    .setOriginalName(originalName)
                    .setSuffix(suffix)
                    .setSize(dataBytes.length)
                    .setUrl(url)
                    .setOssType(OssType.ALI.getTypeName());
            if (fileService.save(newFile)) {
                return newFile;
            } else {
                throw new CustomizeFileException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        } catch (OSSException | ClientException | IOException e) {
            log.error(e.getMessage());
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从OSS中删除文件
     *
     * @param id 文件ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInOss(Long id) {
        File fileInDatabase = fileService.getOne(
                new LambdaQueryWrapper<File>()
                        .eq(File::getId, id)
                        .eq(File::getOssType, OssType.ALI.getTypeName())
        );
        if (Objects.isNull(fileInDatabase)) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL, "文件不存在");
        }
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper
                .eq(File::getUniqueKey, fileInDatabase.getUniqueKey())
                .eq(File::getOssType, OssType.ALI.getTypeName())
                // 搭配事务给数据库被读行数据加行锁
                .last("FOR UPDATE");
        List<File> filesInDatabase = fileService.list(fileLambdaQueryWrapper);
        if (!fileService.removeById(fileInDatabase.getId())) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
        if (Objects.equals(filesInDatabase.size(), 1)) {
            deleteInOssDirect(fileInDatabase.getUrl());
        }
    }

    /**
     * 从OSS中彻底删除文件
     *
     * @param url 文件URL
     */
    private void deleteInOssDirect(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL, "被删除地址为空");
        }
        OSS ossClient = getOssClient();
        String[] split = url.split(ossAliProperties.getBucketName() + "." + ossAliProperties.getEndpoint().split(Constants.HTTPS)[1] + "/");
        if (split.length != 2) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL);
        }
        String key = split[1];
        try {
            ossClient.deleteObject(ossAliProperties.getBucketName(), key);
        } catch (OSSException | ClientException e) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
    }

    /**
     * 获取OSSClient客户端
     *
     * @return 返回OSSClient客户端
     */
    private OSS getOssClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossAliProperties.getSecretId(), ossAliProperties.getSecretKey());
        // 获取配置类中的域名
        String endpoint = ossAliProperties.getEndpoint();
        // 创建ClientBuilderConfiguration。
        // ClientBuilderConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setProtocol(Protocol.HTTPS);
        return new OSSClientBuilder().build(endpoint, credentialsProvider, conf);
    }

    /**
     * 判断文件是否存在，存在则进行秒传操作
     *
     * @param  uniqueKey  唯一摘要值
     * @return 如果能进行秒传，就直接返回秒传结果，否则返回null
     */
    private File existAndFastUpload(String uniqueKey) {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper
                .eq(File::getUniqueKey, uniqueKey)
                .eq(File::getOssType, OssType.ALI.getTypeName())
                .last("LIMIT 1");
        File fileInDatabase = fileService.getOne(fileLambdaQueryWrapper);
        if (Objects.nonNull(fileInDatabase)) {
            if (fileService.save(fileInDatabase.setId(null))) {
                return fileInDatabase;
            } else {
                throw new CustomizeFileException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        } else {
            return null;
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}