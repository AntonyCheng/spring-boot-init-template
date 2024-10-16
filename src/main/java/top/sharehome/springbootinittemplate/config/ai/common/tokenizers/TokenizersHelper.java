package top.sharehome.springbootinittemplate.config.ai.common.tokenizers;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;
import top.sharehome.springbootinittemplate.model.common.Tuple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 题词分词器
 * 💡源自GPT2 BPE（字节对编码）算法（https://github.com/openai/gpt-2/blob/master/src/encoder.py）
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
public class TokenizersHelper {

    private final static Pattern pattern = Pattern.compile("'s|'t|'re|'ve|'m|'ll|'d| ?\\p{L}+| ?\\p{N}+| ?[^\\s\\p{L}\\p{N}]+|\\s+(?!\\S)|\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    public static LinkedHashMap<String, Integer> encoder;

    public static LinkedHashMap<Integer, String> decoder;

    public static LinkedHashMap<Integer, Integer> byteEncoder;

    public static LinkedHashMap<Integer, Integer> byteDecoder;

    public static LinkedHashMap<Tuple<String>, Integer> bpeRanks;

    public static final LinkedHashMap<String, String> CACHE = new LinkedHashMap<>();

    static {
        String encoderJsonFileName = "tokenizers" + File.separator + "encoder.json";
        String vocabBpeFileName = "tokenizers" + File.separator + "vocab.bpe";
        String vocabBpeFileContent = "";
        try (
                InputStream encoderJsonFileStream = new ClassPathResource(encoderJsonFileName).getInputStream();
                InputStream vocabBpeFileStream = new ClassPathResource(vocabBpeFileName).getInputStream()
        ) {
            String encoderJsonFileContent = new String(encoderJsonFileStream.readAllBytes(), StandardCharsets.UTF_8);
            TypeReference<LinkedHashMap<String, Integer>> type = new TypeReference<>() {};
            encoder = JSONObject.parseObject(encoderJsonFileContent, type);
            decoder = encoder.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            vocabBpeFileContent = new String(vocabBpeFileStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "初始化配置文件失败");
        }

        byteEncoder = bytesToUnicode();
        byteDecoder = byteEncoder.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        String[] vocabBpeFileLines = vocabBpeFileContent.split("\n");
        List<Tuple<String>> bpeMerges = new ArrayList<>();
        for (int i = 1; i < vocabBpeFileLines.length - 1; i++) {
            String mergeStr = vocabBpeFileLines[i];
            List<String> pair = Arrays.stream(mergeStr.split("(\\s+)")).filter(e -> !e.trim().isEmpty()).toList();
            bpeMerges.add(new Tuple<>(pair.get(0), pair.get(1)));
        }
        List<Integer> rankList = new ArrayList<>();
        for (int i = 0; i < bpeMerges.size(); i++) {
            rankList.add(i);
        }
        bpeRanks = dictZip(bpeMerges,rankList);
    }

    public static LinkedHashMap<Integer, Integer> bytesToUnicode() {
        List<Integer> bs = new ArrayList<>();
        for (char i = '!'; i < '~' + 1; i++) {
            bs.add((int) i);
        }
        for (char i = '¡'; i < '¬' + 1; i++) {
            bs.add((int) i);
        }
        for (char i = '®'; i < 'ÿ' + 1; i++) {
            bs.add((int) i);
        }
        List<Integer> cs = new ArrayList<>(bs);
        int n = 0;
        for (int b = 0; b < 256; b++) {
            if (!bs.contains(b)) {
                bs.add(b);
                cs.add(256 + n);
                n += 1;
            }
        }
        return dictZip(bs,cs);
    }

    public static Set<Tuple<String>> getPairs(List<String> word) {
        Set<Tuple<String>> pairs = new HashSet<>();
        String prevChar = word.get(0);
        for (int i = 1; i < word.size(); i++) {
            pairs.add(new Tuple<>(prevChar, word.get(i)));
            prevChar = word.get(i);
        }
        return pairs;
    }

    public static <K,V> LinkedHashMap<K, V> dictZip(List<K> x, List<V> y) {
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < x.size(); i++) {
            result.put(x.get(i), y.get(i));
        }
        return result;
    }

}
