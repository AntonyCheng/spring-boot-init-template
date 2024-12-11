package top.sharehome.springbootinittemplate.config.ai.common.tokenizers;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

/**
 * 基于tiktoken库的提示词分词工具
 * 这个分词工具比较细化，如果需要区分模型大小，推荐使用这个工具进行分词计算
 *
 * @author AntonyCheng
 */
@Slf4j
public class TikTokenUtils {

    private static final EncodingRegistry REGISTRY = Encodings.newLazyEncodingRegistry();

    private final Encoding encoding;

    public TikTokenUtils(EncodingType encodingType) {
        encoding = REGISTRY.getEncoding(encodingType);
    }

    public TikTokenUtils(ModelType modelType) {
        encoding = REGISTRY.getEncodingForModel(modelType);
    }

    public Integer getPromptTokenNumber(Prompt prompt) {
        return getMessageTokenNumber(prompt.getInstructions());
    }

    public Integer getMessageTokenNumber(List<Message> messages) {
        StringBuilder res = new StringBuilder();
        for (final Message message : messages) {
            res.append(message.getMessageType().getValue());
            res.append(message.getContent());
        }
        return getTokenNumber(res.toString());
    }

    public Integer getMessageTokenNumber(Message... messages) {
        StringBuilder res = new StringBuilder();
        for (final Message message : messages) {
            res.append(message.getMessageType().getValue());
            res.append(message.getContent());
        }
        return getTokenNumber(res.toString());
    }

    public Integer getStringTokenNumber(List<String> messages) {
        StringBuilder res = new StringBuilder();
        for (final String message : messages) {
            res.append(message);
            res.append(message);
        }
        return getTokenNumber(res.toString());
    }

    public Integer getStringTokenNumber(String... messages) {
        StringBuilder res = new StringBuilder();
        for (final String message : messages) {
            res.append(message);
            res.append(message);
        }
        return getTokenNumber(res.toString());
    }

    public Integer getTokenNumber(String text) {
        return encoding.countTokensOrdinary(text);
    }

    public List<Integer> encode(String text) {
        return encoding.encodeOrdinary(text).boxed();
    }

    public String decode(List<Integer> tokens) {
        IntArrayList intArrayList = new IntArrayList();
        tokens.forEach(intArrayList::add);
        return encoding.decode(intArrayList);
    }

}
