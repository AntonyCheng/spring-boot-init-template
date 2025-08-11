package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.AiChatService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.manager.ChatManager;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResultChunk;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.DeepSeekChatEntity;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.utils.ReasonStreamParser;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;
import top.sharehome.springbootinittemplate.config.sse.enums.SseStatus;
import top.sharehome.springbootinittemplate.config.sse.utils.SseUtils;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;
import top.sharehome.springbootinittemplate.model.common.Tuple2;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;
import top.sharehome.springbootinittemplate.utils.tokenizers.TikTokenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * AI Chat服务接口实现类
 *
 * @author AntonyCheng
 */
@Service
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt) {
        return chatString(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, String systemPrompt) {
        return chatString(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatString(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatString(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatString(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatString(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatString(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatString(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt) {
        return chatStream(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String systemPrompt, String prompt) {
        return chatStream(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatStream(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatStream(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatStream(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatStream(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatStream(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatStream(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt) {
        return chatFlux(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String systemPrompt, String prompt) {
        return chatFlux(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatFlux(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatFlux(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatFlux(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatFlux(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatFlux(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatFlux(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String systemPrompt, String prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatFlux(sseEmitter, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatFlux(sseEmitter, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatFlux(sseEmitter, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatFlux(sseEmitter, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatFlux(sseEmitter, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatFlux(sseEmitter, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt) {
        return chatFlux(userId, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String systemPrompt, String prompt) {
        return chatFlux(userId, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatFlux(userId, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatFlux(userId, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatFlux(userId, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatFlux(userId, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatFlux(userId, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatFlux(userId, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String systemPrompt, String prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, String prompt) {
        return chatFlux(userId, token, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, String prompt) {
        return chatFlux(userId, token, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, String prompt) {
        return chatFlux(userId, token, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, String prompt) {
        return chatFlux(userId, token, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, String prompt) {
        return chatFlux(userId, token, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, String prompt) {
        return chatFlux(userId, token, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    // ======================================================================

    @Override
    public ChatResult chatString(ChatModelBase model, Message... prompt) {
        return chatString(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatString(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatString(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatString(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatString(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatString(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatString(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatString(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, Message... prompt) {
        return chatStream(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatStream(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatStream(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatStream(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatStream(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatStream(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatStream(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatStream(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, Message... prompt) {
        return chatFlux(model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatFlux(model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatFlux(model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatFlux(model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatFlux(model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatFlux(model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatFlux(model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatFlux(model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Message... prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatFlux(sseEmitter, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatFlux(sseEmitter, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatFlux(sseEmitter, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatFlux(sseEmitter, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatFlux(sseEmitter, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatFlux(sseEmitter, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, Message... prompt) {
        return chatFlux(userId, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatFlux(userId, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatFlux(userId, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatFlux(userId, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatFlux(userId, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatFlux(userId, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatFlux(userId, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatFlux(userId, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, Message... prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String systemPrompt, Message... prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, Message... prompt) {
        return chatFlux(userId, token, model, multipartFile, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Message... prompt) {
        return chatFlux(userId, token, model, multipartFile, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, Message... prompt) {
        return chatFlux(userId, token, model, mimeType, inputStream, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Message... prompt) {
        return chatFlux(userId, token, model, mimeType, inputStream, systemPrompt, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, Message... prompt) {
        return chatFlux(userId, token, model, mimeType, byteArray, null, toPrompt(prompt));
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Message... prompt) {
        return chatFlux(userId, token, model, mimeType, byteArray, systemPrompt, toPrompt(prompt));
    }

    // ======================================================================

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt) {
        return chatString(model, null, (byte[]) null, null, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatString(model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatString(model, multipartFile, null, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatString(model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatString(model, mimeType, inputStream, null, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatString(model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatString(model, mimeType, byteArray, null, prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        ChatResponse chatResponse = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .call()
                .chatResponse();
        String content = StringUtils.EMPTY;
        String reasoningContent = StringUtils.EMPTY;
        AssistantMessage assistantMessage = chatResponse != null && chatResponse.getResult() != null
                ? chatResponse.getResult().getOutput()
                : null;
        if (Objects.nonNull(assistantMessage)) {
            if (model instanceof DeepSeekChatEntity) {
                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                content = output.getText() != null ? output.getText() : StringUtils.EMPTY;
                reasoningContent = output.getReasoningContent() != null ? output.getReasoningContent() : StringUtils.EMPTY;
            } else {
                String text = assistantMessage.getText() != null ? assistantMessage.getText() : StringUtils.EMPTY;
                ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
                reasonStreamParser.processChunk(text);
                content = reasonStreamParser.getReplyContent();
                reasoningContent = reasonStreamParser.getThinkContent();
            }
        }
        List<Message> messages = new ArrayList<>(prompt.getInstructions());
        messages.add(new AssistantMessage(content));
        messages.add(new AssistantMessage(reasoningContent));
        Integer usage = new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(messages);
        sw.stop();
        return new ChatResult(content.trim(), reasoningContent.trim(), sw.getDuration().toMillis(), usage, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, Prompt prompt) {
        return chatStream(model, null, (byte[]) null, null, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatStream(model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatStream(model, multipartFile, null, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatStream(model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatStream(model, mimeType, inputStream, null, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatStream(model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatStream(model, mimeType, byteArray, null, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .stream()
                .chatResponse();
        ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
        return model instanceof DeepSeekChatEntity ?
                chatResponseFlux
                        .mapNotNull(chatResponse -> {
                            AssistantMessage assistantMessage = chatResponse != null && chatResponse.getResult() != null
                                    ? chatResponse.getResult().getOutput()
                                    : null;
                            if (Objects.nonNull(assistantMessage)) {
                                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                                return new ChatResultChunk(output.getText(), output.getReasoningContent());
                            } else {
                                return null;
                            }
                        })
                        .toStream() :
                chatResponseFlux
                        .mapNotNull(chatResponse -> {
                            String assistantMessage = chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null
                                    ? chatResponse.getResult().getOutput().getText()
                                    : null;
                            if (Objects.nonNull(assistantMessage)) {
                                reasonStreamParser.processChunk(assistantMessage);
                                String replyContentIncrement = reasonStreamParser.getReplyContentIncrement();
                                String thinkContentIncrement = reasonStreamParser.getThinkContentIncrement();
                                if (!replyContentIncrement.isEmpty() || !thinkContentIncrement.isEmpty()) {
                                    return new ChatResultChunk(replyContentIncrement, thinkContentIncrement);
                                }
                            }
                            return null;
                        })
                        .toStream();
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, Prompt prompt) {
        return chatFlux(model, null, (byte[]) null, null, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatFlux(model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatFlux(model, multipartFile, null, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatFlux(model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatFlux(model, mimeType, inputStream, null, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatFlux(model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatFlux(model, mimeType, byteArray, null, prompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .stream()
                .chatResponse();
        ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
        return model instanceof DeepSeekChatEntity ?
                chatResponseFlux
                        .mapNotNull(chatResponse -> {
                            AssistantMessage assistantMessage = chatResponse != null && chatResponse.getResult() != null
                                    ? chatResponse.getResult().getOutput()
                                    : null;
                            if (Objects.nonNull(assistantMessage)) {
                                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                                return new ChatResultChunk(output.getText(), output.getReasoningContent());
                            } else {
                                return null;
                            }
                        }) :
                chatResponseFlux
                        .mapNotNull(chatResponse -> {
                            String assistantMessage = chatResponse != null && chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null
                                    ? chatResponse.getResult().getOutput().getText()
                                    : null;
                            if (Objects.nonNull(assistantMessage)) {
                                reasonStreamParser.processChunk(assistantMessage);
                                String replyContentIncrement = reasonStreamParser.getReplyContentIncrement();
                                String thinkContentIncrement = reasonStreamParser.getThinkContentIncrement();
                                if (!replyContentIncrement.isEmpty() || !thinkContentIncrement.isEmpty()) {
                                    return new ChatResultChunk(replyContentIncrement, thinkContentIncrement);
                                }
                            }
                            return null;
                        });
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Prompt prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, null, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatFlux(sseEmitter, model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatFlux(sseEmitter, model, multipartFile, null, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatFlux(sseEmitter, model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatFlux(sseEmitter, model, mimeType, inputStream, null, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatFlux(sseEmitter, model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatFlux(sseEmitter, model, mimeType, byteArray, null, prompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        // 参数验证
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "参数[sseEmitter]不能为空");
        }
        // 结果总汇
        AtomicReference<String> content = new AtomicReference<>("");
        AtomicReference<String> reasoningContent = new AtomicReference<>("");
        AtomicLong time = new AtomicLong();
        AtomicInteger usage = new AtomicInteger();
        // 非DeepSeek协议深度思考流解析器
        ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .stream()
                .chatResponse();
        // 公共的流处理逻辑
        Flux<SseMessage> processedFlux = model instanceof DeepSeekChatEntity ?
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            AssistantMessage assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                                String tempContent = output.getText();
                                String tempReasoningContent = output.getReasoningContent();
                                return new SseMessage()
                                        .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                        .setContent(StringUtils.isBlank(tempContent) ? StringUtils.EMPTY : tempContent)
                                        .setReasoningContent(StringUtils.isBlank(tempReasoningContent) ? StringUtils.EMPTY : tempReasoningContent);
                            } else {
                                return new SseMessage();
                            }
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus())) :
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            String assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .map(AssistantMessage::getText)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                reasonStreamParser.processChunk(assistantMessage);
                                String replyContentIncrement = reasonStreamParser.getReplyContentIncrement();
                                String thinkContentIncrement = reasonStreamParser.getThinkContentIncrement();
                                if (replyContentIncrement.isEmpty() || thinkContentIncrement.isEmpty()) {
                                    return new SseMessage()
                                            .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                            .setContent(StringUtils.isEmpty(replyContentIncrement) ? StringUtils.EMPTY : replyContentIncrement)
                                            .setReasoningContent(StringUtils.isEmpty(thinkContentIncrement) ? StringUtils.EMPTY : thinkContentIncrement);
                                }
                            }
                            return new SseMessage();
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus()));
        processedFlux
                .concatWith(Mono.just(new SseMessage().setStatus(SseStatus.FINISH.getName()).setContent(StringUtils.EMPTY).setReasoningContent(StringUtils.EMPTY)))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        String tempContent = (String) message.getContent();
                        String tempReasoningContent = message.getReasoningContent();
                        if (StringUtils.isNotEmpty(tempContent)) {
                            content.set(content.get() + tempContent);
                        } else if (StringUtils.isNotEmpty(tempReasoningContent)) {
                            reasoningContent.set(reasoningContent.get() + tempReasoningContent);
                        }
                    } catch (IOException e) {
                        log.error("数据传输异常: {}", e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error("数据传输异常:{}", e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(
                            new ArrayList<>(prompt.getInstructions()) {
                                {
                                    add(new AssistantMessage(content.get()));
                                }

                                {
                                    add(new AssistantMessage(reasoningContent.get()));
                                }
                            }));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, Prompt prompt) {
        return chatFlux(userId, model, null, (byte[]) null, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatFlux(userId, model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatFlux(userId, model, multipartFile, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatFlux(userId, model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatFlux(userId, model, mimeType, inputStream, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatFlux(userId, model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatFlux(userId, model, mimeType, byteArray, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        // 参数验证
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (!LoginUtils.isLogin(userId)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未登录");
        }
        Map<String, SseEmitter> sseEmitters = SseUtils.getSseEmitter(userId);
        if (Objects.isNull(sseEmitters)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未连接");
        }
        // 结果总汇
        AtomicReference<String> content = new AtomicReference<>("");
        AtomicReference<String> reasoningContent = new AtomicReference<>("");
        AtomicLong time = new AtomicLong();
        AtomicInteger usage = new AtomicInteger();
        // 非DeepSeek协议深度思考流解析器
        ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .stream()
                .chatResponse();
        // 公共的流处理逻辑
        Flux<SseMessage> processedFlux = model instanceof DeepSeekChatEntity ?
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            AssistantMessage assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                                String tempContent = output.getText();
                                String tempReasoningContent = output.getReasoningContent();
                                return new SseMessage()
                                        .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                        .setContent(StringUtils.isBlank(tempContent) ? StringUtils.EMPTY : tempContent)
                                        .setReasoningContent(StringUtils.isBlank(tempReasoningContent) ? StringUtils.EMPTY : tempReasoningContent);
                            } else {
                                return new SseMessage();
                            }
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus())) :
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            String assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .map(AssistantMessage::getText)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                reasonStreamParser.processChunk(assistantMessage);
                                String replyContentIncrement = reasonStreamParser.getReplyContentIncrement();
                                String thinkContentIncrement = reasonStreamParser.getThinkContentIncrement();
                                if (replyContentIncrement.isEmpty() || thinkContentIncrement.isEmpty()) {
                                    return new SseMessage()
                                            .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                            .setContent(StringUtils.isEmpty(replyContentIncrement) ? StringUtils.EMPTY : replyContentIncrement)
                                            .setReasoningContent(StringUtils.isEmpty(thinkContentIncrement) ? StringUtils.EMPTY : thinkContentIncrement);
                                }
                            }
                            return new SseMessage();
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus()));
        processedFlux
                .concatWith(Mono.just(new SseMessage().setStatus(SseStatus.FINISH.getName()).setContent(StringUtils.EMPTY).setReasoningContent(StringUtils.EMPTY)))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        String tempContent = (String) message.getContent();
                        String tempReasoningContent = message.getReasoningContent();
                        if (StringUtils.isNotEmpty(tempContent)) {
                            content.set(content.get() + tempContent);
                        } else if (StringUtils.isNotEmpty(tempReasoningContent)) {
                            reasoningContent.set(reasoningContent.get() + tempReasoningContent);
                        }
                    } catch (IOException e) {
                        log.error("数据传输异常: {}", e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error("数据传输异常:{}", e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(
                            new ArrayList<>(prompt.getInstructions()) {
                                {
                                    add(new AssistantMessage(content.get()));
                                }

                                {
                                    add(new AssistantMessage(reasoningContent.get()));
                                }
                            }));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, Prompt prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String systemPrompt, Prompt prompt) {
        return chatFlux(userId, token, model, null, (byte[]) null, systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, Prompt prompt) {
        return chatFlux(userId, token, model, multipartFile, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MultipartFile multipartFile, String systemPrompt, Prompt prompt) {
        Tuple2<MimeType, byte[]> tuple2 = this.handleMultipartFile(multipartFile);
        return chatFlux(userId, token, model, tuple2.getT1(), tuple2.getT2(), systemPrompt, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, Prompt prompt) {
        return chatFlux(userId, token, model, mimeType, inputStream, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, InputStream inputStream, String systemPrompt, Prompt prompt) {
        try (inputStream) {
            return chatFlux(userId, token, model, mimeType, StreamUtils.copyToByteArray(inputStream), systemPrompt, prompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "处理参数[inputStream]时出错");
        }
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, Prompt prompt) {
        return chatFlux(userId, token, model, mimeType, byteArray, null, prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, MimeType mimeType, byte[] byteArray, String systemPrompt, Prompt prompt) {
        // 参数验证
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (!LoginUtils.isLogin(userId)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + "]未登录");
        }
        SseEmitter sseEmitter = SseUtils.getSseEmitter(userId, token);
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "目标用户[" + userId + ":" + token + "]未连接");
        }
        // 结果总汇
        AtomicReference<String> content = new AtomicReference<>("");
        AtomicReference<String> reasoningContent = new AtomicReference<>("");
        AtomicLong time = new AtomicLong();
        AtomicInteger usage = new AtomicInteger();
        // 非DeepSeek协议深度思考流解析器
        ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt(prompt)
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    if (ObjectUtils.allNotNull(mimeType, byteArray)) {
                        u.media(mimeType, new ByteArrayResource(byteArray));
                    }
                })
                .stream()
                .chatResponse();
        // 公共的流处理逻辑
        Flux<SseMessage> processedFlux = model instanceof DeepSeekChatEntity ?
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            AssistantMessage assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                                String tempContent = output.getText();
                                String tempReasoningContent = output.getReasoningContent();
                                return new SseMessage()
                                        .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                        .setContent(StringUtils.isBlank(tempContent) ? StringUtils.EMPTY : tempContent)
                                        .setReasoningContent(StringUtils.isBlank(tempReasoningContent) ? StringUtils.EMPTY : tempReasoningContent);
                            } else {
                                return new SseMessage();
                            }
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus())) :
                chatResponseFlux
                        .index((i, chatResponse) -> {
                            String assistantMessage = Optional.ofNullable(chatResponse)
                                    .map(ChatResponse::getResult)
                                    .map(Generation::getOutput)
                                    .map(AssistantMessage::getText)
                                    .orElse(null);
                            if (Objects.nonNull(assistantMessage)) {
                                reasonStreamParser.processChunk(assistantMessage);
                                String replyContentIncrement = reasonStreamParser.getReplyContentIncrement();
                                String thinkContentIncrement = reasonStreamParser.getThinkContentIncrement();
                                if (replyContentIncrement.isEmpty() || thinkContentIncrement.isEmpty()) {
                                    return new SseMessage()
                                            .setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName())
                                            .setContent(StringUtils.isEmpty(replyContentIncrement) ? StringUtils.EMPTY : replyContentIncrement)
                                            .setReasoningContent(StringUtils.isEmpty(thinkContentIncrement) ? StringUtils.EMPTY : thinkContentIncrement);
                                }
                            }
                            return new SseMessage();
                        })
                        .filter(sseMessage -> Objects.nonNull(sseMessage.getStatus()));
        processedFlux
                .concatWith(Mono.just(new SseMessage().setStatus(SseStatus.FINISH.getName()).setContent(StringUtils.EMPTY).setReasoningContent(StringUtils.EMPTY)))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        String tempContent = (String) message.getContent();
                        String tempReasoningContent = message.getReasoningContent();
                        if (StringUtils.isNotEmpty(tempContent)) {
                            content.set(content.get() + tempContent);
                        } else if (StringUtils.isNotEmpty(tempReasoningContent)) {
                            reasoningContent.set(reasoningContent.get() + tempReasoningContent);
                        }
                    } catch (IOException e) {
                        log.error("数据传输异常: {}", e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error("数据传输异常: {}", e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(
                            new ArrayList<>(prompt.getInstructions()) {
                                {
                                    add(new AssistantMessage(content.get()));
                                }

                                {
                                    add(new AssistantMessage(reasoningContent.get()));
                                }
                            }));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    /**
     * String转换为Prompt
     */
    private Prompt toPrompt(String str) {
        return Prompt
                .builder()
                .content(str)
                .build();
    }

    /**
     * Messages转换为Prompt
     */
    private Prompt toPrompt(Message... messages) {
        return Prompt
                .builder()
                .messages(messages)
                .build();
    }

    /**
     * 处理MultipartFile
     */
    private Tuple2<MimeType, byte[]> handleMultipartFile(MultipartFile multipartFile) {
        try {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return new Tuple2<>(mimeType, multipartFile.getBytes());
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

}
