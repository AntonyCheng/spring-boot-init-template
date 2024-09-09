package top.sharehome.springbootinittemplate.config.meta;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.Constants;

import java.time.LocalDateTime;

/**
 * 自动字段填充器
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
public class FieldMetaObjectHandler implements MetaObjectHandler {

    /**
     * 需要处理的字段名——updateTime
     */
    public static final String UPDATE_TIME = "updateTime";

    /**
     * 需要处理的字段名——createTime
     */
    public static final String CREATE_TIME = "createTime";

    /**
     * 需要处理的字段名——delFlag
     */
    public static final String DEL_FLAG = "delFlag";

    /**
     * 需要处理的字段名——state
     */
    public static final String STATE = "state";

    /**
     * todo 如果有自己的一套鉴权系统，请去掉这个自动填充项
     * 需要处理的字段名——role
     */
    public static final String ROLE = "role";

    /**
     * 插入时自动填充的字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATE_TIME)) {
            metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
        }

        if (metaObject.hasSetter(CREATE_TIME)) {
            metaObject.setValue(CREATE_TIME, LocalDateTime.now());
        }

        if (metaObject.hasSetter(DEL_FLAG)) {
            metaObject.setValue(DEL_FLAG, 0);
        }

        if (metaObject.hasSetter(STATE)) {
            metaObject.setValue(STATE, 0);
        }

        if (metaObject.hasSetter(ROLE)) {
            metaObject.setValue(ROLE, Constants.ROLE_USER);
        }
    }

    /**
     * 更新时自动填充的字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATE_TIME)) {
            metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
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