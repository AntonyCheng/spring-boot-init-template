package top.sharehome.springbootinittemplate.config.oss.service.local.handler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.oss.service.local.condition.OssLocalCondition;
import top.sharehome.springbootinittemplate.config.oss.service.local.properties.OssLocalProperties;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地OSS静态资源处理器
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(OssLocalProperties.class)
@Slf4j
@Conditional(OssLocalCondition.class)
public class OssLocalResourceHandler implements WebMvcConfigurer {

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
                // 缓存一个小时
                .setCachePeriod(3600)
                .resourceChain(true);
        log.info("############ Local OSS static resource mapping is successful: /{}/** <==> {}**", prefix, resourceLocation);
    }

    @PostConstruct
    public void init() {
        String storagePath = ossLocalProperties.getPath();
        File storageDir = new File(storagePath);

        if (!storageDir.exists()) {
            boolean created = storageDir.mkdirs();
            if (created) {
                log.info("############ Local OSS storage directory is successfully created: {}", storagePath);
            } else {
                log.error("############ Local OSS storage directory creation failed: {}", storagePath);
            }
        } else {
            log.info("############ Local OSS storage directory already exists: {}", storagePath);
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
        String accessUrl = String.format("%s://%s:%d/%s/%s/", protocol, ossLocalProperties.getAddress(), port, basePath, ossLocalProperties.getPrefix());
        log.info("############ Local OSS access address: {}", accessUrl);
    }

    /**
     * 构建跨平台的资源位置URL
     *
     * @param storagePath 存储路径
     * @return 资源位置URL
     */
    private String buildResourceLocation(String storagePath) {
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();
        String fileUri = path.toUri().toString();
        if (!fileUri.endsWith("/")) {
            fileUri += "/";
        }
        return fileUri;
    }

}