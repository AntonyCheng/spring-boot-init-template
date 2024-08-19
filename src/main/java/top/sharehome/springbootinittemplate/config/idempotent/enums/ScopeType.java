package top.sharehome.springbootinittemplate.config.idempotent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 作用范围类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum ScopeType {

    /**
     * 面向个人，即接口操作和个人会话ID有关，比如A用户连续调用被限流不影响B用户进行正常调用
     */
    PERSONAL(),

    /**
     * 面向所有人，即接口操作和个人会话ID无关，比如A用户连续调用被限流会导致B用户调用限流
     */
    ALL()

}
