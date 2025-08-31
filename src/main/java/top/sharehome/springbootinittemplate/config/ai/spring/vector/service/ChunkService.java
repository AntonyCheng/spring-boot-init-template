package top.sharehome.springbootinittemplate.config.ai.spring.vector.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.springbootinittemplate.config.ai.spring.service.embedding.model.EmbeddingModelBase;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.model.entity.Chunk;
import top.sharehome.springbootinittemplate.model.common.Tuple2;

import java.util.List;

/**
 * 数据块服务接口
 *
 * @author AntonyCheng
 */
public interface ChunkService extends IService<Chunk> {

    /**
     * 添加数据块
     */
    void addChunk(Long userId, Long knowledgeId, Long documentId, List<String> text);

}
