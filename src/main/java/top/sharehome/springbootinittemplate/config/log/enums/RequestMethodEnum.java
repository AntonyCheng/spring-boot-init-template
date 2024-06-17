package top.sharehome.springbootinittemplate.config.log.enums;

import lombok.Getter;

/**
 * 日志请求方法枚举类
 *
 * @author AntonyCheng
 */
@Getter
public enum RequestMethodEnum {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    PATCH("PATCH");

    private final String methodName;

    RequestMethodEnum(String methodName) {
        this.methodName = methodName;
    }

}
