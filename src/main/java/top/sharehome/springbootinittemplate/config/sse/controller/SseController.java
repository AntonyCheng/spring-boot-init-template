package top.sharehome.springbootinittemplate.config.sse.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.config.sse.condition.SseCondition;
import top.sharehome.springbootinittemplate.config.sse.utils.SseUtils;

/**
 * SSE控制器
 * 该控制器中仅包含建立和断开连接的功能，若要添加其他功能，可以结合SseUtils工具进行数据传输
 *
 * @author AntonyCheng
 */
@RestController
@Conditional(SseCondition.class)
@RequestMapping(value = "/${sse.path}")
@SaCheckLogin
public class SseController {

    /**
     * SSE创建连接
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ControllerLog(description = "SSE创建连接", operator = Operator.OTHER)
    public SseEmitter connect() {
        return SseUtils.connect();
    }

    /**
     * SSE断开连接
     */
    @GetMapping(value = "/disconnect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ControllerLog(description = "SSE断开连接", operator = Operator.OTHER)
    public R<Void> disconnect() {
        SseUtils.disconnect();
        return R.empty();
    }

}
