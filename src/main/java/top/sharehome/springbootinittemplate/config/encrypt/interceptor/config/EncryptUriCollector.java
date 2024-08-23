package top.sharehome.springbootinittemplate.config.encrypt.interceptor.config;

import jakarta.annotation.Resource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 请求参数加密拦截URI收集器
 *
 * @author AntonyCheng
 */
//@Component
public class EncryptUriCollector {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final Class<? extends Annotation> targetAnnotation = RSADecrypt.class;

    public List<String> collectUris() {
        List<String> uris = new ArrayList<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            // 获取处理方法所属的控制器类
            Class<?> controllerClass = handlerMethod.getBeanType();

            // 检查类上是否有目标注解
            Annotation classAnnotation = AnnotationUtils.findAnnotation(controllerClass, targetAnnotation);

            // 检查方法上是否有目标注解
            Annotation methodAnnotation = handlerMethod.getMethodAnnotation(targetAnnotation);

            if (classAnnotation != null || methodAnnotation != null) {
                uris.addAll(requestMappingInfo.getPatternsCondition().getPatterns());
            }
        }
        return uris;
    }
}
