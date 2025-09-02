package top.sharehome.springbootinittemplate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sharehome.springbootinittemplate.model.entity.Model;

/**
 * 模型Mapper类
 *
 * @author AntonyCheng
 */
@Mapper
public interface ModelMapper extends BaseMapper<Model> {

}