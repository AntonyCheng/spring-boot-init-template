package top.sharehome.springbootinittemplate.config.ai.common.tokenizers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeAiException;
import top.sharehome.springbootinittemplate.model.common.Tuple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * BPE算法提示词分词工具
 * 这个分词工具比较通用，如果不区分模型大小，推荐使用这个工具进行分词计算
 * 💡源自GPT2 BPE（字节对编码）算法（https://github.com/openai/gpt-2/blob/master/src/encoder.py）
 *
 * @author AntonyCheng
 */
@Slf4j
public class BpeTokenUtils {

    private static final Pattern PATTERN = Pattern.compile("'s|'t|'re|'ve|'m|'ll|'d| ?\\p{L}+| ?\\p{N}+| ?[^\\s\\p{L}\\p{N}]+|\\s+(?!\\S)|\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    private static final HashMap<String, Integer> ENCODER;

    private static final HashMap<Integer, String> DECODER;

    private static final HashMap<Integer, Integer> BYTE_ENCODER;

    private static final HashMap<Integer, Integer> BYTE_DECODER;

    private static final HashMap<Tuple<String>, Integer> BPE_RANKS;

    private static final HashMap<String, String> CACHE = new HashMap<>();

    static {
        String encoderJsonFileName = "tokenizers" + File.separator + "encoder.json";
        String vocabBpeFileName = "tokenizers" + File.separator + "vocab.bpe";
        String vocabBpeFileContent = "";
        try (
                InputStream encoderJsonFileStream = new ClassPathResource(encoderJsonFileName).getInputStream();
                InputStream vocabBpeFileStream = new ClassPathResource(vocabBpeFileName).getInputStream()
        ) {
            String encoderJsonFileContent = new String(encoderJsonFileStream.readAllBytes(), StandardCharsets.UTF_8);
            TypeReference<HashMap<String, Integer>> type = new TypeReference<>() {
            };
            ENCODER = JSON.parseObject(encoderJsonFileContent, type);
            DECODER = ENCODER.entrySet().stream()
                    .parallel()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue, HashMap::new));
            vocabBpeFileContent = new String(vocabBpeFileStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "初始化配置文件失败");
        }

        BYTE_ENCODER = bytesToUnicode();
        BYTE_DECODER = BYTE_ENCODER.entrySet().stream()
                .parallel()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue, HashMap::new));

        String[] vocabBpeFileLines = vocabBpeFileContent.split("\n");
        List<Tuple<String>> bpeMerges = new ArrayList<>();
        for (int i = 1; i < vocabBpeFileLines.length - 1; i++) {
            String mergeStr = vocabBpeFileLines[i];
            List<String> pair = Arrays.stream(mergeStr.split("(\\s+)"))
                    .filter(e -> !e.trim().isEmpty())
                    .toList();
            bpeMerges.add(new Tuple<>(pair.get(0), pair.get(1)));
        }
        List<Integer> rankList = new ArrayList<>();
        for (int i = 0; i < bpeMerges.size(); i++) {
            rankList.add(i);
        }
        BPE_RANKS = dictZip(bpeMerges, rankList);
    }

    public static Integer getPromptTokenNumber(Prompt prompt) {
        return getMessageTokenNumber(prompt.getInstructions());
    }

    public static Integer getMessageTokenNumber(List<Message> messages) {
        StringBuilder res = new StringBuilder();
        for (final Message message : messages) {
            res.append(message.getMessageType().getValue());
            res.append(message.getContent());
        }
        return getTokenNumber(res.toString());
    }

    public static Integer getMessageTokenNumber(Message... messages) {
        StringBuilder res = new StringBuilder();
        for (final Message message : messages) {
            res.append(message.getMessageType().getValue());
            res.append(message.getContent());
        }
        return getTokenNumber(res.toString());
    }

    public static Integer getStringTokenNumber(List<String> messages) {
        StringBuilder res = new StringBuilder();
        for (final String message : messages) {
            res.append(message);
            res.append(message);
        }
        return getTokenNumber(res.toString());
    }

    public static Integer getStringTokenNumber(String... messages) {
        StringBuilder res = new StringBuilder();
        for (final String message : messages) {
            res.append(message);
            res.append(message);
        }
        return getTokenNumber(res.toString());
    }

    public static Integer getTokenNumber(String text) {
        return encode(text).size();
    }

    public static List<Integer> encode(String text) {
        List<Integer> bpeTokens = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(text);
        List<String> regexTokens = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(0);
            regexTokens.add(group);
        }
        regexTokens.forEach(regexToken -> {
            byte[] tokenBytes = regexToken.getBytes(StandardCharsets.UTF_8);
            String token = IntStream.range(0, tokenBytes.length)
                    .parallel()
                    .map(i -> Byte.toUnsignedInt(tokenBytes[i]))
                    .mapToObj(tokenByte -> String.valueOf((char) ((int) BYTE_ENCODER.get(tokenByte))))
                    .collect(Collectors.joining(""));
            List<Integer> newTokens = Arrays.stream(bpe(token).split(" "))
                    .parallel()
                    .map(ENCODER::get)
                    .toList();
            bpeTokens.addAll(newTokens);
        });
        return bpeTokens;
    }

    public static String decode(List<Integer> tokens) {
        String text = tokens.stream()
                .map(DECODER::get)
                .collect(Collectors.joining(""));
        int[] array = text.chars()
                .map(BYTE_DECODER::get)
                .toArray();
        byte[] bytes = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bytes[i] = (byte) array[i];
        }
        return new String(bytes);
    }

    private static String bpe(String token) {
        String cacheToken;
        if (Objects.nonNull(cacheToken = CACHE.get(token))) {
            return cacheToken;
        }
        List<String> word = new Tuple<>(token.split(""));
        Set<Tuple<String>> pairs = getPairs(word);
        if (pairs.isEmpty()) {
            return token;
        }
        while (true) {
            Tuple<String> bigram = pairs.stream()
                    .parallel()
                    .min(Comparator.comparing(pair -> BPE_RANKS.getOrDefault(pair, Integer.MAX_VALUE)))
                    .orElse(null);
            if (!BPE_RANKS.containsKey(bigram)) {
                break;
            }
            if (Objects.isNull(bigram)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "Token分词算法异常");
            }
            String first = bigram.getOrDefault(0, null);
            String second = bigram.getOrDefault(1, null);
            if (ObjectUtils.anyNull(first, second)) {
                throw new CustomizeAiException(ReturnCode.EXCEPTION_OCCURRED_IN_AI_MODULE, "Token分词算法异常");
            }
            ArrayList<String> newWord = new ArrayList<>();
            int i = 0;
            while (i < word.size()) {
                int j = -1;
                for (int index = i; index < word.size(); index++) {
                    if (word.get(index).equals(first)) {
                        j = index;
                        break;
                    }
                }
                if (Objects.equals(j, -1)) {
                    newWord.addAll(word.subList(i, word.size()));
                    break;
                }
                newWord.addAll(word.subList(i, j));
                i = j;
                if (Objects.equals(word.get(i), first) && i < word.size() - 1 && Objects.equals(word.get(i + 1), second)) {
                    newWord.add(first + second);
                    i = i + 2;
                } else {
                    newWord.add(word.get(i));
                    i = i + 1;
                }
            }
            word = newWord;
            if (Objects.equals(word.size(), 1)) {
                break;
            } else {
                pairs = getPairs(word);
            }
        }
        word = Arrays.stream(String.join(" ", word).split("")).toList();
        CACHE.put(token, String.join("", word));
        return String.join("", word);
    }

    private static HashMap<Integer, Integer> bytesToUnicode() {
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
        return dictZip(bs, cs);
    }

    private static Set<Tuple<String>> getPairs(List<String> word) {
        Set<Tuple<String>> pairs = new HashSet<>();
        String prevChar = word.get(0);
        for (int i = 1; i < word.size(); i++) {
            pairs.add(new Tuple<>(prevChar, word.get(i)));
            prevChar = word.get(i);
        }
        return pairs;
    }

    private static <K, V> HashMap<K, V> dictZip(List<K> x, List<V> y) {
        HashMap<K, V> result = new HashMap<>();
        for (int i = 0; i < x.size(); i++) {
            result.put(x.get(i), y.get(i));
        }
        return result;
    }

}
