package top.sharehome.springbootinittemplate.config.sensitive;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import com.github.houbb.sensitive.word.support.ignore.SensitiveWordCharIgnores;
import com.github.houbb.sensitive.word.support.resultcondition.WordResultConditions;
import com.github.houbb.sensitive.word.support.tag.WordTags;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.ip2region.properties.Ip2RegionProperties;
import top.sharehome.springbootinittemplate.config.sensitive.plugins.DefaultWordAllow;
import top.sharehome.springbootinittemplate.config.sensitive.plugins.DefaultWordDeny;
import top.sharehome.springbootinittemplate.config.sensitive.properties.SensitiveProperties;

import java.util.*;

/**
 * 离线IP库配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(Ip2RegionProperties.class)
@Slf4j
public class SensitiveConfiguration {

    private static SensitiveWordBs sensitiveWordBs = null;

    @Resource
    private SensitiveProperties sensitiveProperties;

    @Resource
    private DefaultWordAllow defaultWordAllow;

    @Resource
    private DefaultWordDeny defaultWordDeny;

    @Bean(name = "SensitiveConfiguration0", destroyMethod = "destroy")
    public SensitiveConfiguration create() {
        if (Objects.isNull(sensitiveWordBs)) {
            sensitiveWordBs = SensitiveWordBs.newInstance()
                    // 忽略大小写
                    .ignoreCase(sensitiveProperties.getIsIgnoreCase())
                    // 忽略半角圆角
                    .ignoreWidth(sensitiveProperties.getIsIgnoreWidth())
                    // 忽略数字的写法
                    .ignoreNumStyle(sensitiveProperties.getIsIgnoreNumStyle())
                    // 忽略中文的书写格式
                    .ignoreChineseStyle(sensitiveProperties.getIsIgnoreChineseStyle())
                    // 忽略英文的书写格式
                    .ignoreEnglishStyle(sensitiveProperties.getIsIgnoreEnglishStyle())
                    // 忽略重复词
                    .ignoreRepeat(sensitiveProperties.getIsIgnoreRepeat())
                    // 是否启用数字检测
                    .enableNumCheck(sensitiveProperties.getEnableNumCheck())
                    // 是有启用邮箱检测
                    .enableEmailCheck(sensitiveProperties.getEnableEmailCheck())
                    // 是否启用链接检测
                    .enableUrlCheck(sensitiveProperties.getEnableUrlCheck())
                    // 是否启用IPv4检测
                    .enableIpv4Check(sensitiveProperties.getEnableIpv4Check())
                    // 是否启用敏感单词检测
                    .enableWordCheck(sensitiveProperties.getEnableWordCheck())
                    // 数字检测，自定义指定长度
                    .numCheckLen(sensitiveProperties.getNumCheckLen())
                    // 词对应的标签
                    .wordTag(WordTags.none())
                    // 忽略的字符
                    .charIgnore(SensitiveWordCharIgnores.defaults())
                    // 针对匹配的敏感词额外加工，比如可以限制英文单词必须全匹配
                    .wordResultCondition(WordResultConditions.alwaysTrue())
                    // 单词白名单
                    .wordAllow(new DefaultWordAllow())
                    // 单词黑名单
                    .wordDeny(sensitiveProperties.getEnableDefaultDenys() ? WordDenys.chains(WordDenys.defaults(), new DefaultWordDeny()) : new DefaultWordDeny())
                    .init();
        }
        return this;
    }

    public Boolean hasSensitive(String target) {
        return !Objects.isNull(target) && sensitiveWordBs.contains(target);
    }

    public String getFirstSensitive(String target) {
        return StringUtils.isBlank(target) ? null : sensitiveWordBs.findFirst(target);
    }

    public List<String> getSensitiveList(String target) {
        return StringUtils.isBlank(target) ? new ArrayList<>() : sensitiveWordBs.findAll(target);
    }

    public void addWordAllow(Collection<String> words) {
        if (CollectionUtils.isNotEmpty(words)) {
            List<String> newWords = null;
            newWords = words.stream().filter(Objects::nonNull).toList();
            if (CollectionUtils.isNotEmpty(newWords)) {
                sensitiveWordBs.addWordAllow(newWords);
                defaultWordAllow.addWordAllow(newWords);
            }
        }
    }

    public void addWordAllow(String word, String... other) {
        if (StringUtils.isNotBlank(word)) {
            List<String> list = Arrays.stream(other).parallel().filter(Objects::nonNull).toList();
            String[] array = list.toArray(new String[0]);
            sensitiveWordBs.addWordAllow(word, array);
            defaultWordAllow.addWordAllow(word, array);
        }
    }

    public void removeWordAllow(Collection<String> words) {
        if (CollectionUtils.isNotEmpty(words)) {
            List<String> newWords = null;
            newWords = words.stream().filter(Objects::nonNull).toList();
            if (CollectionUtils.isNotEmpty(newWords)) {
                sensitiveWordBs.removeWordAllow(words);
                defaultWordAllow.removeWordAllow(words);
            }
        }
    }

    public void removeWordAllow(String word, String... other) {
        if (StringUtils.isNotBlank(word)) {
            List<String> list = Arrays.stream(other).parallel().filter(Objects::nonNull).toList();
            String[] array = list.toArray(new String[0]);
            sensitiveWordBs.removeWordAllow(word, array);
            defaultWordAllow.removeWordAllow(word, array);
        }
    }

    public void addWordDeny(Collection<String> words) {
        if (CollectionUtils.isNotEmpty(words)) {
            List<String> newWords = null;
            newWords = words.stream().filter(Objects::nonNull).toList();
            if (CollectionUtils.isNotEmpty(newWords)) {
                sensitiveWordBs.addWord(words);
                defaultWordDeny.addWordDeny(words);
            }
        }
    }

    public void addWordDeny(String word, String... other) {
        if (StringUtils.isNotBlank(word)) {
            List<String> list = Arrays.stream(other).parallel().filter(Objects::nonNull).toList();
            String[] array = list.toArray(new String[0]);
            sensitiveWordBs.addWord(word, array);
            defaultWordDeny.addWordDeny(word, array);
        }
    }

    public void removeWordDeny(Collection<String> words) {
        if (CollectionUtils.isNotEmpty(words)) {
            List<String> newWords = null;
            newWords = words.stream().filter(Objects::nonNull).toList();
            if (CollectionUtils.isNotEmpty(newWords)) {
                sensitiveWordBs.removeWord(words);
                defaultWordDeny.removeWordDeny(words);
            }
        }
    }

    public void removeWordDeny(String word, String... other) {
        if (StringUtils.isNotBlank(word)) {
            List<String> list = Arrays.stream(other).parallel().filter(Objects::nonNull).toList();
            String[] array = list.toArray(new String[0]);
            sensitiveWordBs.removeWord(word, array);
            defaultWordDeny.removeWordDeny(word, array);
        }
    }

    private void destroy() {
        if (Objects.nonNull(sensitiveWordBs)) {
            sensitiveWordBs.destroy();
        }
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
