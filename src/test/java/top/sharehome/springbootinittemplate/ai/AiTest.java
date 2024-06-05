package top.sharehome.springbootinittemplate.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
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
        System.out.printf(openAiChatModel.call("你好"));
    }

    @Test
    public void testZhiPuAi() {
        System.out.printf(zhiPuAiChatModel.call("你好"));
    }

    @Test
    public void testOllamaAi() {
        System.out.println(ollamaChatModel.call("你好"));
    }

}
