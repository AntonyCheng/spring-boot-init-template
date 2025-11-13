package top.sharehome.springbootinittemplate.config.oss.service.local;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.oss.common.enums.OssType;
import top.sharehome.springbootinittemplate.config.oss.service.local.condition.OssLocalCondition;
import top.sharehome.springbootinittemplate.config.oss.service.local.properties.OssLocalProperties;
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
 * 本地OSS配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(OssLocalProperties.class)
@Slf4j
@Conditional(OssLocalCondition.class)
public class OssLocalConfiguration implements WebMvcConfigurer {

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private OssLocalProperties ossLocalProperties;

    @Resource
    private FileService fileService;

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
        return ((OssLocalConfiguration) AopContext.currentProxy()).uploadToOss(inputStream, originalName, suffix, rootPath);
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
        return ((OssLocalConfiguration) AopContext.currentProxy()).uploadToOss(inputStream, originalName, suffix, rootPath);
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
            String path = ossLocalProperties.getPath();
            if (StringUtils.endsWith(path, "/")) {
                path = path + key;
            } else {
                path = path + "/" + key;
            }
            java.io.File existFile = new java.io.File(path);
            if (!existFile.exists()) {
                FileUtils.copyInputStreamToFile(tempInputStream, existFile);
            }
            String url = getAccessUrl() + key;
            File newFile = new File()
                    .setUniqueKey(uniqueKey)
                    .setName(key)
                    .setOriginalName(originalName)
                    .setSuffix(suffix)
                    .setSize(dataBytes.length)
                    .setUrl(url)
                    .setOssType(OssType.LOCAL.getTypeName());
            if (fileService.save(newFile)) {
                return newFile;
            } else {
                throw new CustomizeFileException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        } catch (OSSException | ClientException | IOException e) {
            log.error(e.getMessage());
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
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
                        .eq(File::getOssType, OssType.LOCAL.getTypeName())
        );
        if (Objects.isNull(fileInDatabase)) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL, "文件不存在");
        }
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper
                .eq(File::getUniqueKey, fileInDatabase.getUniqueKey())
                .eq(File::getOssType, OssType.LOCAL.getTypeName())
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
        String uri = "/" + url.split(getAccessUrl())[1];
        String basePath = ossLocalProperties.getPath().replace("\\", "/");
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length() - 1);
        }
        java.io.File existFile = new java.io.File(basePath + uri);
        if (existFile.exists()) {
            try {
                FileUtils.delete(existFile);
            } catch (IOException e) {
                throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
            }
        }
    }

    /**
     * 判断文件是否存在，存在则进行秒传操作
     *
     * @param uniqueKey 唯一摘要值
     * @return 如果能进行秒传，就直接返回秒传结果，否则返回null
     */
    private File existAndFastUpload(String uniqueKey) {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper
                .eq(File::getUniqueKey, uniqueKey)
                .eq(File::getOssType, OssType.LOCAL.getTypeName())
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
     * 获取外部访问基础地址
     */
    private String getAccessUrl() {
        String basePath = "";
        if (this.contextPath.startsWith("/") && this.contextPath.length() > 1) {
            basePath = this.contextPath.substring(1);
        }
        if (this.contextPath.endsWith("/") && this.contextPath.length() > 1) {
            if (basePath.isEmpty()) {
                basePath = this.contextPath.substring(0, this.contextPath.length() - 1);
            } else {
                basePath = basePath.substring(0, basePath.length() - 1);
            }
        }
        String protocol = ossLocalProperties.getIsHttps() ? "https" : "http";
        return String.format("%s://%s:%d/%s/%s/", protocol, ossLocalProperties.getAddress(), port, basePath, ossLocalProperties.getPrefix());
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}