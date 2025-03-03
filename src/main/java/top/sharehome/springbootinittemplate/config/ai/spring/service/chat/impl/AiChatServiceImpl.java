package top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.knuddels.jtokkit.api.EncodingType;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.minimax.MiniMaxChatModel;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.minimax.api.MiniMaxApi;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.ai.moonshot.MoonshotChatOptions;
import org.springframework.ai.moonshot.api.MoonshotApi;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.qianfan.QianFanChatModel;
import org.springframework.ai.qianfan.QianFanChatOptions;
import org.springframework.ai.qianfan.api.QianFanApi;
import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.resolution.DelegatingToolCallbackResolver;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import org.springframework.ai.zhipuai.api.ZhiPuAiApi;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.AiChatService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.*;
import top.sharehome.springbootinittemplate.config.sse.entity.SseMessage;
import top.sharehome.springbootinittemplate.config.sse.enums.SseStatus;
import top.sharehome.springbootinittemplate.config.sse.utils.SseUtils;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;
import top.sharehome.springbootinittemplate.utils.tokenizers.TikTokenUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        StopWatch sw = new StopWatch();
        sw.start();
        if (model instanceof DeepSeekChatEntity entity) {
            String result = this.getDeepSeekChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OpenAiChatEntity entity) {
            String result = this.getOpenAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            String result = this.getOllamaChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            String result = this.getZhiPuAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            String result = this.getMoonshotChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            String result = this.getMistralAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            String result = this.getQianFanChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            String result = this.getMiniMaxChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            String result = this.getAzureOpenAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Flux<String> chatFlux(ChatModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt);
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public ChatResult chatFlux(SseEmitter sseEmitter, ChatModelBase model, String prompt) {
        if (StringUtils.isBlank(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (Objects.isNull(sseEmitter)) {
            throw new CustomizeAiException(ReturnCode.FAIL, "参数[sseEmitter]不能为空");
        }
        // 结果总汇
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, ChatModelBase model, String prompt) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
    }

    @Override
    public ChatResult chatFlux(Long userId, String token, ChatModelBase model, String prompt) {
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
        AtomicReference<String> result = new AtomicReference<>("");
        // 消耗时间
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(new UserMessage(prompt), new AssistantMessage(result.get())));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getDeepSeekChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OpenAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getOpenAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getOllamaChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getZhiPuAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getMoonshotChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getMistralAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getQianFanChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getMiniMaxChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            String result = this.getAzureOpenAiChatModel(entity).call(prompt);
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result)));
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).toStream();
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Flux<String> chatFlux(ChatModelBase model, Message... prompt) {
        if (ArrayUtils.isEmpty(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt);
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(ArrayUtils.add(prompt, new AssistantMessage(result.get()))));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
    }

    @Override
    public ChatResult chatString(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getDeepSeekChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OpenAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getOpenAiChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof OllamaChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getOllamaChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getZhiPuAiChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MoonshotChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getMoonshotChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MistralAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getMistralAiChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof QianFanChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getQianFanChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof MiniMaxChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getMiniMaxChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            StopWatch sw = new StopWatch();
            sw.start();
            Generation generation = this.getAzureOpenAiChatModel(entity).call(prompt).getResult();
            String result = Objects.nonNull(generation) ? generation.getOutput().getText() : "";
            sw.stop();
            Integer tokenNum = new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions());
            return new ChatResult(result, sw.getDuration().toMillis(), tokenNum, prompt);
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Stream<String> chatStream(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt).map(ChatResponse::getResult).mapNotNull(generation -> generation.getOutput().getText()).toStream();
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
    }

    @Override
    public Flux<String> chatFlux(ChatModelBase model, Prompt prompt) {
        if (Objects.isNull(prompt)) {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[prompt]不能为空");
        }
        if (model instanceof DeepSeekChatEntity entity) {
            return this.getDeepSeekChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof OpenAiChatEntity entity) {
            return this.getOpenAiChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof OllamaChatEntity entity) {
            return this.getOllamaChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            return this.getZhiPuAiChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof MoonshotChatEntity entity) {
            return this.getMoonshotChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof MistralAiChatEntity entity) {
            return this.getMistralAiChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof QianFanChatEntity entity) {
            return this.getQianFanChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof MiniMaxChatEntity entity) {
            return this.getMiniMaxChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            return this.getAzureOpenAiChatModel(entity).stream(prompt).map(res -> res.getResult().getOutput().getText());
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        for (Map.Entry<String, SseEmitter> sseEmitterEntry : sseEmitters.entrySet()) {
                            sseEmitterEntry.getValue().send(message);
                        }
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
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
        AtomicLong takeTime = new AtomicLong();
        // 消耗Token
        AtomicInteger tokenNum = new AtomicInteger();
        // 计时器
        StopWatch sw = new StopWatch();
        sw.start();
        Flux<String> flux;
        if (model instanceof DeepSeekChatEntity entity) {
            flux = this.getDeepSeekChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.O200K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OpenAiChatEntity entity) {
            flux = this.getOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof OllamaChatEntity entity) {
            flux = this.getOllamaChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof ZhiPuAiChatEntity entity) {
            flux = this.getZhiPuAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MoonshotChatEntity entity) {
            flux = this.getMoonshotChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MistralAiChatEntity entity) {
            flux = this.getMistralAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof QianFanChatEntity entity) {
            flux = this.getQianFanChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof MiniMaxChatEntity entity) {
            flux = this.getMiniMaxChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.P50K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else if (model instanceof AzureOpenAiChatEntity entity) {
            flux = this.getAzureOpenAiChatModel(entity).stream(prompt)
                    .map(chatResponse -> chatResponse.getResult().getOutput().getText())
                    .doOnError(e -> {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    })
                    .doOnTerminate(() -> {
                        sw.stop();
                        takeTime.set(sw.getDuration().toMillis());
                        tokenNum.set(new TikTokenUtils(EncodingType.CL100K_BASE).getMessageTokenNumber(prompt.getInstructions()));
                    });
        } else {
            throw new CustomizeAiException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数[model]存在异常");
        }
        flux.index((i, message) -> new SseMessage().setStatus(i == 0 ? SseStatus.START.getName() : SseStatus.PROCESS.getName()).setData(message))
                .concatWith(Flux.just(new SseMessage().setStatus(SseStatus.FINISH.getName())))
                .doOnNext(message -> {
                    try {
                        sseEmitter.send(message);
                        Object messageData = message.getData();
                        if (Objects.nonNull(messageData)) {
                            result.set(result.get() + messageData);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new CustomizeAiException(ReturnCode.FAIL, "数据传输异常");
                    }
                })
                .blockLast();
        return new ChatResult(result.get(), takeTime.get(), tokenNum.get(), prompt);
    }

    /**
     * 获取DeepSeekChatModel
     */
    private OpenAiChatModel getDeepSeekChatModel(DeepSeekChatEntity entity) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(new SimpleApiKey(entity.getApiKey()))
                .restClientBuilder(RestClient.builder())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTemperature())
                                .build()
                )
                .build();
    }

    /**
     * 获取OpenAiChatModel
     */
    private OpenAiChatModel getOpenAiChatModel(OpenAiChatEntity entity) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(entity.getBaseUrl())
                .apiKey(new SimpleApiKey(entity.getApiKey()))
                .restClientBuilder(RestClient.builder())
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTemperature())
                                .build()
                )
                .build();
    }

    /**
     * 获取OllamaChatModel
     */
    private OllamaChatModel getOllamaChatModel(OllamaChatEntity entity) {
        OllamaApi ollamaApi = new OllamaApi(entity.getBaseUrl());
        return new OllamaChatModel(ollamaApi, OllamaOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build(), new DefaultToolCallingManager(ObservationRegistry.NOOP, new DelegatingToolCallbackResolver(List.of()), new DefaultToolExecutionExceptionProcessor(true)), ObservationRegistry.NOOP, ModelManagementOptions.builder()
                .build());
    }

    /**
     * 获取ZhiPuAiChatModel
     */
    private ZhiPuAiChatModel getZhiPuAiChatModel(ZhiPuAiChatEntity entity) {
        ZhiPuAiApi zhiPuAiApi = new ZhiPuAiApi(entity.getApiKey());
        return new ZhiPuAiChatModel(zhiPuAiApi, ZhiPuAiChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MoonshotChatModel
     */
    private MoonshotChatModel getMoonshotChatModel(MoonshotChatEntity entity) {
        MoonshotApi moonshotApi = new MoonshotApi(entity.getApiKey());
        return new MoonshotChatModel(moonshotApi, MoonshotChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MistralAiChatModel
     */
    private MistralAiChatModel getMistralAiChatModel(MistralAiChatEntity entity) {
        MistralAiApi mistralAiApi = new MistralAiApi(entity.getApiKey());
        return MistralAiChatModel.builder()
                .mistralAiApi(mistralAiApi)
                .defaultOptions(
                        MistralAiChatOptions.builder()
                                .model(entity.getModel())
                                .temperature(entity.getTemperature())
                                .topP(entity.getTopP()).build()
                )
                .build();
    }

    /**
     * 获取QianFanChatModel
     */
    private QianFanChatModel getQianFanChatModel(QianFanChatEntity entity) {
        QianFanApi qianFanApi = new QianFanApi(entity.getApiKey(), entity.getSecretKey());
        return new QianFanChatModel(qianFanApi, QianFanChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取MiniMaxChatModel
     */
    private MiniMaxChatModel getMiniMaxChatModel(MiniMaxChatEntity entity) {
        MiniMaxApi miniMaxApi = new MiniMaxApi(entity.getApiKey());
        return new MiniMaxChatModel(miniMaxApi, MiniMaxChatOptions.builder()
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build());
    }

    /**
     * 获取AzureOpenAiChatModel
     */
    private AzureOpenAiChatModel getAzureOpenAiChatModel(AzureOpenAiChatEntity entity) {
        OpenAIClientBuilder clientBuilder = new OpenAIClientBuilder().credential(new AzureKeyCredential(entity.getApiKey())).endpoint(entity.getEndpoint()).serviceVersion(entity.getModelVersion());
        return new AzureOpenAiChatModel(clientBuilder, AzureOpenAiChatOptions.builder()
                .deploymentName(entity.getModel())
                .temperature(entity.getTemperature())
                .topP(entity.getTopP())
                .build(), DefaultToolCallingManager.builder().build(), ObservationRegistry.NOOP);
    }

}
