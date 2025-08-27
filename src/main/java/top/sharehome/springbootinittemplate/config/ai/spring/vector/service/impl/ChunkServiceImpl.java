package top.sharehome.springbootinittemplate.config.ai.spring.vector.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.mapper.ChunkMapper;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.model.bo.ChunkRetrievalBo;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.model.entity.Chunk;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.service.ChunkService;
import top.sharehome.springbootinittemplate.model.common.Tuple2;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据块服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ChunkServiceImpl extends ServiceImpl<ChunkMapper, Chunk> implements ChunkService {

    @Resource
    private ChunkMapper chunkMapper;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    @DS("postgresql-embed")
    public void addChunk(List<Tuple2<String, float[]>> data) {
        List<Chunk> list = data.stream().map(d -> new Chunk()
                .setDocumentId(Snowflake.DEFAULT_TIME_OFFSET)
                .setKnowledgeId(Snowflake.DEFAULT_TIME_OFFSET)
                .setUserId(Snowflake.DEFAULT_TIME_OFFSET)
                .setContent(d.getT1())
                .setEmbedding(d.getT2())
                .setState(2)
                .setDimension(d.getT2().length)
                .setFailReason("")
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setDeleted(0)).toList();
        chunkMapper.insert(list);
    }

    /**
     * 向量相似性搜索 - 使用 L2 距离
     */
    @DS("postgresql-embed")
    public List<ChunkRetrievalBo> findSimilarChunksByL2(float[] queryVector, int limit) {
        String sql = """
                SELECT *, chunk_embedding <-> ?::vector AS score 
                FROM t_chunk 
                WHERE is_deleted = 0 
                ORDER BY chunk_embedding <-> ?::vector 
                LIMIT ?
                """;

        String vectorStr = floatArrayToVectorString(queryVector);
        return jdbcTemplate.queryForList(sql, ChunkRetrievalBo.class, vectorStr, vectorStr, limit);
    }

    /**
     * 向量相似性搜索 - 使用余弦距离
     */
    @DS("postgresql-embed")
    public List<ChunkRetrievalBo> findSimilarChunksByCosine(float[] queryVector, int limit) {
        String sql = """
                SELECT *, chunk_embedding <=> ?::vector AS score 
                FROM t_chunk 
                WHERE is_deleted = 0 
                ORDER BY chunk_embedding <=> ?::vector 
                LIMIT ?
                """;

        String vectorStr = floatArrayToVectorString(queryVector);
        return jdbcTemplate.queryForList(sql, ChunkRetrievalBo.class, vectorStr, vectorStr, limit);
    }

    /**
     * 向量相似性搜索 - 使用内积
     */
    @DS("postgresql-embed")
    public List<ChunkRetrievalBo> findSimilarChunksByInnerProduct(float[] queryVector, int limit) {
        String sql = """
                SELECT *, chunk_embedding <#> ?::vector AS score 
                FROM t_chunk 
                WHERE is_deleted = 0 
                ORDER BY chunk_embedding <#> ?::vector 
                LIMIT ?
                """;

        String vectorStr = floatArrayToVectorString(queryVector);
        return jdbcTemplate.queryForList(sql, ChunkRetrievalBo.class, vectorStr, vectorStr, limit);
    }

    /**
     * 根据知识库ID进行向量搜索
     */
    @DS("postgresql-embed")
    public List<ChunkRetrievalBo> findSimilarChunksByKnowledge(Long knowledgeId, float[] queryVector, int limit) {
        String sql = """
                SELECT *, chunk_embedding <-> ?::vector AS distance 
                FROM t_chunk 
                WHERE chunk_knowledge_id = ? AND is_deleted = 0 
                ORDER BY chunk_embedding <-> ?::vector 
                LIMIT ?
                """;

        String vectorStr = floatArrayToVectorString(queryVector);
        return jdbcTemplate.queryForList(sql, ChunkRetrievalBo.class, vectorStr, knowledgeId, vectorStr, limit);
    }

    /**
     * 将 float[] 转换为 PostgreSQL vector 格式的字符串
     */
    private String floatArrayToVectorString(float[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(array[i]);
        }
        sb.append("]");
        return sb.toString();
    }

}
