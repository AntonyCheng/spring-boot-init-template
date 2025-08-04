package top.sharehome.springbootinittemplate.config.ai.spring.service.chat;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResultChunk;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;

import java.io.InputStream;
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
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    ChatResult chatString(ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    ChatResult chatString(ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    ChatResult chatString(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);

    /**
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    ChatResult chatString(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，返回Chat结果集
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    ChatResult chatString(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，返回流式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，返回响应式数据
     *
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

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
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param sseEmitter    Sse连接器
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

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
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);


    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

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
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, String systemPrompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MultipartFile multipartFile);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param multipartFile 请求文件
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream);

    /**
     * 发起AI Chat功能，响应式输出，并返回Chat结果集
     *
     * @param userId        输出目标用户ID
     * @param token         输出目标用户Token
     * @param model         chat模型信息
     * @param prompt        提示词
     * @param mimeType      多模态文件MIME格式
     * @param inputStream   文件流
     * @param systemPrompt  系统提示词
     */
    ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt);

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
