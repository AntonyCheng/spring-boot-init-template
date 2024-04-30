package top.sharehome.springbootinittemplate.model.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

/**
 * 分页查询模型类
 *
 * @author AntonyCheng
 */
@Data
public class PageModel implements Serializable {

    /**
     * 页数
     */
    private Integer page;

    /**
     * 项数
     */
    private Integer size;

    /**
     * 是否允许深分页
     */
    private Boolean allowDeep;

    /**
     * 默认页数 第1页
     */
    private static final int DEFAULT_PAGE = 1;

    /**
     * 不允许深分页情况下默认项数（最大项数） 查询500条
     */
    private static final int DEFAULT_SIZE_NO_DEEP = 500;

    /**
     * 允许深分页情况下默认项数（最大项数） 查询全部
     */
    private static final int DEFAULT_SIZE_IN_DEEP = Integer.MAX_VALUE;

    /**
     * 默认不允许深分页
     */
    private static final boolean DEFAULT_ALLOW_DEEP = false;

    /**
     * Page<T>构建器
     *
     * @param <T> 分页泛型
     * @return 构建结果
     */
    public <T> Page<T> build() {
        Boolean allowDeep = ObjectUtils.defaultIfNull(getAllowDeep(), DEFAULT_ALLOW_DEEP);
        Integer page = ObjectUtils.defaultIfNull(getPage(), DEFAULT_PAGE);
        Integer size = allowDeep ?
                ObjectUtils.defaultIfNull(getSize(), DEFAULT_SIZE_IN_DEEP) : ObjectUtils.defaultIfNull(getSize(), DEFAULT_SIZE_NO_DEEP);
        if (page <= 0) {
            page = DEFAULT_PAGE;
        }
        return new Page<T>(page, size);
    }

    private static final long serialVersionUID = -7445813604888145436L;

}
