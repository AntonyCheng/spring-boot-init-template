package top.sharehome.springbootinittemplate.config.ai.spring.service.chat;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;

import java.util.stream.Stream;

/**
 * AI Chat服务接口
 *
 * @author AntonyCheng
 */
public interface AiChatService {

    /**
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatString(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Stream<String> chatStream(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Flux<String> chatFlux(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatString(ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Stream<String> chatStream(ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Flux<String> chatFlux(ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, Message... prompt);

    /**
     * 发起AI Chat功能，Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatString(ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Stream<String> chatStream(ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Flux<String> chatFlux(ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, Prompt prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, Prompt prompt);

}
