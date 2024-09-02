package top.sharehome.springbootinittemplate.config.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 日志请求方法枚举类
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum RequestMethod {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    PATCH("PATCH");

    private final String methodName;

}
