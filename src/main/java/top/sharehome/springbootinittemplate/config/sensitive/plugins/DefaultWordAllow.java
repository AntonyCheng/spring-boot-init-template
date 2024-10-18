package top.sharehome.springbootinittemplate.config.sensitive.plugins;

import com.github.houbb.sensitive.word.api.IWordAllow;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 敏感词白名单插件
 *
 * @author AntonyCheng
 */
@Component
public class DefaultWordAllow implements IWordAllow {

    @Override
    public List<String> allow() {
        // todo 这里可以选择从数据库中进行查询
        return List.of();
    }

    public void addWordAllow(Collection<String> words) {
        // todo 这里可以同步数据库数据
    }

    public void addWordAllow(String word, String... other) {
        List<String> list = Arrays.stream(ArrayUtils.add(other, new String[]{word})).map(s -> (String) s).toList();
        // todo 这里可以同步数据库数据
    }

    public void removeWordAllow(Collection<String> words) {
        // todo 这里可以同步数据库数据
    }

    public void removeWordAllow(String word, String[] other) {
        List<String> list = Arrays.stream(ArrayUtils.add(other, new String[]{word})).map(s -> (String) s).toList();
        // todo 这里可以同步数据库数据
    }
}
