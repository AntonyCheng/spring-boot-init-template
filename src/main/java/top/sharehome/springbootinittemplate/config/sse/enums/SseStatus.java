package top.sharehome.springbootinittemplate.config.sse.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SSE状态
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum SseStatus {

    /**
     * 已经连接
     */
    CONNECTED("connected"),

    /**
     * 断开连接
     */
    DISCONNECTED("disconnected"),

    /**
     * 开始传输数据
     */
    START("start"),

    /**
     * 数据传输中
     */
    PROCESS("process"),

    /**
     * 结束传输数据
     */
    FINISH("finish");

    private final String name;

}
