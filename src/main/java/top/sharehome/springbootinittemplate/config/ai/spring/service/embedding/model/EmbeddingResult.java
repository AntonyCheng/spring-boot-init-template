package top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import top.sharehome.springbootinittemplate.model.common.Tuple2;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Embedding结果集
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class EmbeddingResult implements Serializable {

    /**
     * 结果集
     */
    private List<Tuple2<String, float[]>> result;

    /**
     * Token数
     */
    private Integer tokenNum;

    public EmbeddingResult(List<Tuple2<String, float[]>> result, Integer tokenNum) {
        this.result = result;
        this.tokenNum = tokenNum;
    }

    public static EmbeddingResult buildResult(EmbeddingResponse embeddingResponse, List<String> text) {
        List<Tuple2<String, float[]>> res = new ArrayList<>();
        List<Embedding> embeddings = embeddingResponse.getResults();
        for (int i = 0; i < embeddings.size(); i++) {
            Tuple2<String, float[]> tuple = new Tuple2<>(text.get(i), embeddings.get(i).getOutput());
            res.add(tuple);
        }
        return new EmbeddingResult(res, embeddingResponse.getMetadata().getUsage().getTotalTokens());
    }

    @Serial
    private static final long serialVersionUID = 538150090994005050L;

}
