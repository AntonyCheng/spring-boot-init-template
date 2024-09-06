package top.sharehome.springbootinittemplate.config.oss.service.tencent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.*;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.oss.common.enums.OssType;
import top.sharehome.springbootinittemplate.config.oss.service.tencent.condition.OssTencentCondition;
import top.sharehome.springbootinittemplate.config.oss.service.tencent.properties.TencentProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeFileException;
import top.sharehome.springbootinittemplate.model.entity.File;
import top.sharehome.springbootinittemplate.service.FileService;
import top.sharehome.springbootinittemplate.utils.encrypt.SHA3Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 腾讯云COS配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(TencentProperties.class)
@AllArgsConstructor
@Slf4j
@Conditional(OssTencentCondition.class)
public class TencentConfiguration {

    private final TencentProperties tencentProperties;

    private final FileService fileService;

    /**
     * 上传文件到COS
     *
     * @param file     待上传的文件
     * @param rootPath 上传的路径
     */
    public File uploadToCos(MultipartFile file, String rootPath) {
        if (Objects.isNull(file)) {
            throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
        }
        String originalName = StringUtils.isNotBlank(file.getOriginalFilename()) ? file.getOriginalFilename() : file.getName();
        String suffix = FilenameUtils.getExtension(originalName);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
        // 使用Aop代理类进行上传操作，保证事物生效
        return ((TencentConfiguration) AopContext.currentProxy()).uploadToCos(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 上传文件到COS
     *
     * @param bytes        待上传的文件字节数组
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     */
    public File uploadToCos(byte[] bytes, String originalName, String suffix, String rootPath) {
        if (ObjectUtils.isEmpty(bytes)) {
            throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
        }
        InputStream inputStream = new ByteArrayInputStream(bytes);
        // 使用Aop代理类进行上传操作，保证事物生效
        return ((TencentConfiguration) AopContext.currentProxy()).uploadToCos(inputStream, originalName, suffix, rootPath);
    }

    /**
     * 上传文件到COS
     *
     * @param inputStream  待上传的文件流
     * @param originalName 文件原名称
     * @param suffix       文件后缀
     * @param rootPath     上传的路径
     */
    @Transactional(rollbackFor = Exception.class)
    public File uploadToCos(InputStream inputStream, String originalName, String suffix, String rootPath) {
        try (inputStream) {
            if (Objects.isNull(inputStream)) {
                throw new CustomizeFileException(ReturnCode.USER_DO_NOT_UPLOAD_FILE);
            }
            byte[] dataBytes = inputStream.readAllBytes();
            String plainKey = new String(dataBytes) + originalName + suffix;
            // 检查是否能进行秒传，如果能就直接返回秒传结果，否则进行普通上传
            String uniqueKey = SHA3Utils.encrypt(plainKey);
            File fastUploadResult = existAndFastUpload(uniqueKey);
            if (Objects.nonNull(fastUploadResult)) {
                return fastUploadResult;
            }
            // 接下来进行普通上传
            if (StringUtils.isEmpty(suffix)) {
                suffix = "." + Constants.UNKNOWN_FILE_TYPE_SUFFIX;
            } else {
                suffix = "." + suffix;
            }
            if (StringUtils.isBlank(originalName)) {
                originalName = "none" + suffix;
            }
            // 创建一个随机文件名称
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + System.currentTimeMillis() + suffix;
            // 对象键(Key)是对象在存储桶中的唯一标识。
            String key = StringUtils.isBlank(StringUtils.trim(rootPath)) ? fileName : rootPath + "/" + fileName;
            // 封装输出流为ByteArrayInputStream
            ByteArrayInputStream tempInputStream = new ByteArrayInputStream(dataBytes);
            // 使用高级接口必须先保证本进程存在一个 TransferManager 实例，如果没有则创建
            // 详细代码参见本页：高级接口 -> 创建 TransferManager
            TransferManager transferManager = createTransferManager();
            // 这里创建一个 ByteArrayInputStream 来作为示例，实际中这里应该是您要上传的 InputStream 类型的流
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(tempInputStream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(tencentProperties.getBucketName(), key, tempInputStream, objectMetadata);
            // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
            // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            try {
                // 高级接口会返回一个异步结果Upload
                // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回 UploadResult, 失败抛出异常
                Upload upload = transferManager.upload(putObjectRequest);
                if (!showTransferProgress(upload)) {
                    throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
                }
                // UploadResult uploadResult = upload.waitForUploadResult();
            } catch (CosClientException e) {
                throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
            }
            // 确定本进程不再使用 transferManager 实例之后，关闭即可
            // 详细代码参见本页：高级接口 -> 关闭 TransferManager
            shutdownTransferManager(transferManager);
            // 添加新文件
            String url = Constants.HTTPS + tencentProperties.getBucketName() + ".cos." + tencentProperties.getRegion() + ".myqcloud.com/" + key;
            File newFile = new File()
                    .setUniqueKey(uniqueKey)
                    .setName(key)
                    .setOriginalName(originalName)
                    .setSuffix(suffix)
                    .setUrl(url)
                    .setOssType(OssType.TENCENT.getTypeName());
            if (fileService.save(newFile)) {
                return newFile;
            } else {
                throw new CustomizeFileException(ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomizeFileException(ReturnCode.FILE_UPLOAD_EXCEPTION);
        }
    }

    /**
     * 从COS中删除文件
     *
     * @param id 文件ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteInCos(Long id) {
        File fileInDatabase = fileService.getOne(
                new LambdaQueryWrapper<File>()
                        .eq(File::getId,id)
                        .eq(File::getOssType, OssType.TENCENT.getTypeName())
        );
        if (Objects.isNull(fileInDatabase)) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL, "文件不存在");
        }
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper
                .eq(File::getUniqueKey, fileInDatabase.getUniqueKey())
                .eq(File::getOssType, OssType.TENCENT.getTypeName())
                // 搭配事务给数据库被读行数据加行锁
                .last("FOR UPDATE");
        List<File> filesInDatabase = fileService.list(fileLambdaQueryWrapper);
        if (!fileService.removeById(fileInDatabase.getId())) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
        if (Objects.equals(filesInDatabase.size(), 1)) {
            deleteInCosDirect(fileInDatabase.getUrl());
        }
    }

    /**
     * 从COS中删除文件
     *
     * @param url 文件URL
     */
    private void deleteInCosDirect(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL, "被删除地址为空");
        }
        COSClient cosClient = getCosClient();
        String[] split = url.split(tencentProperties.getBucketName() + ".cos." + tencentProperties.getRegion() + ".myqcloud.com/");
        if (split.length != 2) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_ADDRESS_IS_ABNORMAL);
        }
        String key = split[1];
        try {
            cosClient.deleteObject(tencentProperties.getBucketName(), key);
        } catch (CosClientException e) {
            throw new CustomizeFileException(ReturnCode.USER_FILE_DELETION_IS_ABNORMAL);
        }
    }

    /**
     * 获取COSClient客户端
     *
     * @return 返回COSClient客户端
     */
    private COSClient getCosClient() {
        COSCredentials cred = new BasicCOSCredentials(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        // 设置 bucket 的地域
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region(tencentProperties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        // 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 禁止打印关闭时的堆栈信息，绝大多数场景下是无效信息
        clientConfig.setPrintShutdownStackTrace(false);
        // 生成 cos 客户端并且上传文件
        return new COSClient(cred, clientConfig);
    }

    /**
     * 创建 TransferManager 实例，这个实例用来后续调用高级接口
     */
    private TransferManager createTransferManager() {
        // 创建一个 COSClient 实例，这是访问 COS 服务的基础实例。
        // 详细代码参见本页: 简单操作 -> 创建 COSClient
        COSClient cosClient = getCosClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        // ExecutorService threadPool = Executors.newFixedThreadPool(16);
        int nThreads = Runtime.getRuntime().availableProcessors();
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
        ExecutorService threadPool = new ThreadPoolExecutor(nThreads, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 3MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(3 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    /**
     * 确定不再通过TransferManager实例进行调用高级接口后，一定要关闭掉这个实例
     *
     * @param transferManager 需要关闭的实例
     */
    private void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例。
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例。
        transferManager.shutdownNow(true);
    }

    /**
     * 显示上传进度
     *
     * @param transfer 传输对象
     */
    private Boolean showTransferProgress(Transfer transfer) {
        // 这里的 Transfer 是异步上传结果 Upload 的父类
        log.info(transfer.getDescription());
        // transfer.isDone() 查询上传是否已经完成
        while (!transfer.isDone()) {
            try {
                // 每 5 秒获取一次进度
                ThreadUtils.sleep(Duration.ofSeconds(1));
            } catch (InterruptedException e) {
                return false;
            }
            TransferProgress progress = transfer.getProgress();
            long sofar = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            log.info(String.format("Upload progress: [%d / %d] = %.02f%%\n", sofar, total, pct));
        }
        // 完成了 Completed，或者失败了 Failed
        return Objects.equals(transfer.getState().toString(), "Completed");
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
                .eq(File::getOssType, OssType.TENCENT.getTypeName())
                .last("limit 1");
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