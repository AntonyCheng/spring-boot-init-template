package top.sharehome.springbootinittemplate.config.sse.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl.AiChatServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.OllamaChatEntity;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.config.sse.utils.SseUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

/**
 * SSE控制器
 * 该控制器中仅包含建立和断开连接的功能，若要添加其他功能，可以结合SseUtils工具进行数据传输
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping(value = "/${sse.path}")
@SaCheckLogin
public class SseController {

    @Resource
    private AiChatServiceImpl aiChatService;

    /**
     * AI Chat
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public R<Void> chat(@RequestParam("prompt") String prompt) {
        OllamaChatEntity ollamaChatEntity1 = new OllamaChatEntity("deepseek-coder:1.3b", "http://localhost:11434");
        OllamaChatEntity ollamaChatEntity2 = new OllamaChatEntity("deepseek-r1:1.5b", "http://localhost:11434");
        SseEmitter sseEmitter = SseUtils.getSseEmitter(LoginUtils.getLoginUserId(), LoginUtils.getLoginUserToken());
        ChatResult chatResult = aiChatService.chatFlux(sseEmitter, ollamaChatEntity1, prompt);
        return R.empty();
    }

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
