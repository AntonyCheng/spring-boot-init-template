package top.sharehome.springbootinittemplate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.zhipuai.ZhiPuAiChatModel;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring AI测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class AiTest {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Resource
    private ZhiPuAiChatModel zhiPuAiChatModel;

    @Resource
    private OllamaChatModel ollamaChatModel;

    @Test
    public void testOpenAi() {
        System.out.println(openAiChatModel.call("你好"));
    }

    @Test
    public void testZhiPuAi() {
        System.out.println(zhiPuAiChatModel.call("你好"));
    }

    @Test
    public void testOllamaAi() {
//        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？")));
//        System.out.println(ollamaChatModel.call(new UserMessage("你能说出我的名字么？")));
        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？")));
        System.out.println(ollamaChatModel.call(new UserMessage("我的名字叫AntonyCheng，你能说出我的名字么？"), new UserMessage("我的名字叫什么？")));
    }

}
