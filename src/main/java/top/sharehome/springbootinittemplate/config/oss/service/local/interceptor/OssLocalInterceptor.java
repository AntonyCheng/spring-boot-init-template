package top.sharehome.springbootinittemplate.config.oss.service.local.interceptor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.oss.service.local.properties.OssLocalProperties;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 国际化拦截器Web配置类
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(OssLocalProperties.class)
@Slf4j
public class OssLocalInterceptor implements WebMvcConfigurer {

    @Value("${server.port}")
    private Integer port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private OssLocalProperties ossLocalProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String storagePath = ossLocalProperties.getPath();
        String prefix = ossLocalProperties.getPrefix();

        String resourceLocation = buildResourceLocation(storagePath);

        registry.addResourceHandler("/" + prefix + "/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600)  // 缓存1小时
                .resourceChain(true);

        log.info("静态资源映射配置成功:");
        log.info("  - 请求路径: /{}/**", prefix);
        log.info("  - 物理路径: {}", resourceLocation);
        log.info("  - 操作系统: {}", System.getProperty("os.name"));
    }

    @PostConstruct
    public void init() {
        String storagePath = ossLocalProperties.getPath();
        File storageDir = new File(storagePath);

        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (created) {
                log.info("本地OSS存储目录创建成功: {}", storagePath);
            } else {
                log.error("本地OSS存储目录创建失败: {}", storagePath);
            }
        } else {
            log.info("本地OSS存储目录已存在: {}", storagePath);
        }
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
        String accessUrl = String.format("%s://%s:%d/%s/%s/",
                protocol,
                ossLocalProperties.getAddress(),
                port,
                basePath,
                ossLocalProperties.getPrefix());

        log.info("本地OSS访问地址: {}", accessUrl);
        log.info("示例: {}2024/11/example.jpg", accessUrl);
    }

    /**
     * 构建跨平台的资源位置URL
     *
     * @param storagePath 存储路径
     * @return 资源位置URL
     */
    private String buildResourceLocation(String storagePath) {
        // 使用 Paths API 标准化路径（自动处理 \ 和 /）
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();

        // 转换为URI字符串（会自动处理平台差异）
        String fileUri = path.toUri().toString();

        // 确保以斜杠结尾
        if (!fileUri.endsWith("/")) {
            fileUri += "/";
        }

        return fileUri;
    }

}