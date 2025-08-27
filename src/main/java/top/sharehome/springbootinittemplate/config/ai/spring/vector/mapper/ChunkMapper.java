package top.sharehome.springbootinittemplate.config.ai.spring.vector.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sharehome.springbootinittemplate.config.ai.spring.vector.model.entity.Chunk;

/**
 * 数据块Mapper类
 *
 * @author AntonyCheng
 */
@Mapper
public interface ChunkMapper extends BaseMapper<Chunk> {

}
