package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl;

import com.knuddels.jtokkit.api.EncodingType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
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
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;
import top.sharehome.springbootinittemplate.utils.tokenizers.TikTokenUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
        return chatString(model, prompt, null, null, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, String systemPrompt) {
        return chatString(model, prompt, null, null, systemPrompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatString(model, prompt, multipartFile, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatString(model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatString(model, prompt, mimeType, inputStream, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        ChatResponse chatResponse = ChatManager.getChatClient(model)
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
                    }
                })
                .call()
                .chatResponse();
        String content = StringUtils.EMPTY;
        String reasonContent = StringUtils.EMPTY;
        AssistantMessage assistantMessage = chatResponse != null && chatResponse.getResult() != null
                ? chatResponse.getResult().getOutput()
                : null;
        if (Objects.nonNull(assistantMessage)) {
            if (model instanceof DeepSeekChatEntity) {
                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                content = output.getText() != null ? output.getText() : StringUtils.EMPTY;
                reasonContent = output.getReasoningContent() != null ? output.getReasoningContent() : StringUtils.EMPTY;
            } else {
                String text = assistantMessage.getText() != null ? assistantMessage.getText() : StringUtils.EMPTY;
                ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
                reasonStreamParser.processChunk(text);
                content = reasonStreamParser.getReplyContent();
                reasonContent = reasonStreamParser.getThinkContent();
            }
        }
        Integer usage = new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(content), new AssistantMessage(reasonContent));
        sw.stop();
        return new ChatResult(content, reasonContent, sw.getDuration().toMillis(), usage, prompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt) {
        return chatStream(model, prompt, null, null, null);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, String systemPrompt) {
        return chatStream(model, prompt, null, null, systemPrompt);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatStream(model, prompt, multipartFile, null);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatStream(model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatStream(model, prompt, mimeType, inputStream, null);
    }

    @Override
    public Stream<ChatResultChunk> chatStream(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
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
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt) {
        return chatFlux(model, prompt, null, null, null);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, String systemPrompt) {
        return chatFlux(model, prompt, null, null, systemPrompt);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatFlux(model, prompt, multipartFile, null);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatFlux(model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatFlux(model, prompt, mimeType, inputStream, null);
    }

    @Override
    public Flux<ChatResultChunk> chatFlux(ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        Flux<ChatResponse> chatResponseFlux = ChatManager.getChatClient(model)
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
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
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt) {
        return chatFlux(sseEmitter, model, prompt, null, null, null);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, String systemPrompt) {
        return chatFlux(sseEmitter, model, prompt, null, null, systemPrompt);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatFlux(sseEmitter, model, prompt, multipartFile, null);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatFlux(sseEmitter, model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatFlux(sseEmitter, model, prompt, mimeType, inputStream, null);
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        // 参数验证
        if (StringUtils.isBlank(prompt)) {
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
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
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
                    usage.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(content.get()), new AssistantMessage(reasoningContent.get())));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt) {
        return chatFlux(userId, model, prompt, null, null, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, String systemPrompt) {
        return chatFlux(userId, model, prompt, null, null, systemPrompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatFlux(userId, model, prompt, multipartFile, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatFlux(userId, model, prompt, mimeType, inputStream, null);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatFlux(userId, model, prompt, mimeType, inputStream, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        // 参数验证
        if (StringUtils.isBlank(prompt)) {
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
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
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
                    usage.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(content.get())));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt) {
        return chatFlux(userId, token, model, prompt, null, null, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, String systemPrompt) {
        return chatFlux(userId, token, model, prompt, null, null, systemPrompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MultipartFile multipartFile) {
        return chatFlux(userId, token, model, prompt, multipartFile, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatFlux(userId, token, model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream) {
        return chatFlux(userId, token, model, prompt, mimeType, inputStream, null);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
        // 参数验证
        if (StringUtils.isBlank(prompt)) {
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
                .prompt()
                .system(s -> {
                    if (StringUtils.isNotBlank(systemPrompt)) {
                        s.text(systemPrompt);
                    }
                })
                .user(u -> {
                    u.text(prompt);
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
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
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(content.get())));
                })
                .blockLast();
        return new ChatResult(content.get().trim(), reasoningContent.get().trim(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        String result = ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .call()
                .content();
        sw.stop();
        Integer usage = new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(prompt);
        return new ChatResult(result, sw.getDuration().toMillis(), usage, prompt);
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        return ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .stream()
                .content()
                .toStream();
    }

    @Override
    public Flux<String> chatFlux(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        return ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .stream()
                .content();
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "参数[sseEmitter]不能为空");
        }
        // 结果总汇
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt()
                .messages(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt) {
        return chatString(model, prompt, null, null, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt, String systemPrompt) {
        return chatString(model, prompt, null, null, systemPrompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt, MultipartFile multipartFile) {
        return chatString(model, prompt, multipartFile, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt, MultipartFile multipartFile, String systemPrompt) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String contentType = multipartFile.getContentType();
            if (StringUtils.isBlank(contentType)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "文件类型异常");
            }
            MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
            return chatString(model, prompt, mimeType, inputStream, systemPrompt);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "读取文件异常");
        }
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt, MimeType mimeType, InputStream inputStream) {
        return chatString(model, prompt, mimeType, inputStream, null);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt, MimeType mimeType, InputStream inputStream, String systemPrompt) {
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
                    if (ObjectUtils.allNotNull(mimeType, inputStream)) {
                        u.media(mimeType, new InputStreamResource(inputStream));
                    }
                })
                .call()
                .chatResponse();
        String content = StringUtils.EMPTY;
        String reasonContent = StringUtils.EMPTY;
        AssistantMessage assistantMessage = chatResponse != null && chatResponse.getResult() != null
                ? chatResponse.getResult().getOutput()
                : null;
        if (Objects.nonNull(assistantMessage)) {
            if (model instanceof DeepSeekChatEntity) {
                DeepSeekAssistantMessage output = (DeepSeekAssistantMessage) assistantMessage;
                content = output.getText() != null ? output.getText() : StringUtils.EMPTY;
                reasonContent = output.getReasoningContent() != null ? output.getReasoningContent() : StringUtils.EMPTY;
            } else {
                String text = assistantMessage.getText() != null ? assistantMessage.getText() : StringUtils.EMPTY;
                ReasonStreamParser reasonStreamParser = new ReasonStreamParser();
                reasonStreamParser.processChunk(text);
                content = reasonStreamParser.getReplyContent();
                reasonContent = reasonStreamParser.getThinkContent();
            }
        }
        Integer usage = new TikTokenUtils(ChatManager.getEncodingType(model)).getPromptTokenNumber(prompt);
        sw.stop();
        return new ChatResult(content, reasonContent, sw.getDuration().toMillis(), usage, prompt);
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        return ChatManager.getChatClient(model)
                .prompt(prompt)
                .stream()
                .content()
                .toStream();
    }

    @Override
    public Flux<String> chatFlux(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        return ChatManager.getChatClient(model)
                .prompt(prompt)
                .stream()
                .content();
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "参数[sseEmitter]不能为空");
        }
        // 结果总汇
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(prompt.getInstructions()));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, Prompt prompt) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(prompt.getInstructions()));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, Prompt prompt) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong time = new AtomicLong();
        // 消耗Token
        AtomicInteger usage = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        // 获取大模型回复
        ChatManager.getChatClient(model)
                .prompt(prompt)
                .stream()
                .content()
                .index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setContent(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getContent();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .doOnError(e -> {
                    log.error(e.getMessage());
                    throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                })
                .doOnTerminate(() -> {
                    sw.stop();
                    time.set(sw.getDuration().toMillis());
                    usage.set(new TikTokenUtils(ChatManager.getEncodingType(model)).getMessageTokenNumber(prompt.getInstructions()));
                })
                .blockLast();
        return new ChatResult(result.get(), time.get(), usage.get(), prompt);
    }

    private static Prompt toPrompt(String str) {
        return Prompt
                .builder()
                .content(str)
                .build();
    }

    private static Prompt toPrompt(Message... messages){
        return Prompt
                .builder()
                .messages(messages)
                .build();
    }

}
