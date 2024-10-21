package top.sharehome.springbootinittemplate.config.sensitive.word.plugins;

import com.github.houbb.sensitive.word.api.IWordDeny;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 敏感词黑名单插件
 *
 * @author AntonyCheng
 */
@Component
public class DefaultWordDeny implements IWordDeny {

    @Override
    public List<String> deny() {
        // todo 这里可以从数据库中进行查询
        return List.of();
    }

    public void addWordDeny(Collection<String> words) {
        // todo 这里可以同步数据库数据
    }

    public void addWordDeny(String word, String[] other) {
        // todo 这里可以同步数据库数据
    }

    public void removeWordDeny(Collection<String> words) {
        // todo 这里可以同步数据库数据
    }

    public void removeWordDeny(String word, String[] other) {
        // todo 这里可以同步数据库数据
    }

}
