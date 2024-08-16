package top.sharehome.springbootinittemplate.utils.redisson.lock.model;

import lombok.Data;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具时间模型实体类
 *
 * @author AntonyCheng
 */
@Data
@Accessors(chain = true)
public class TimeModel implements Serializable {

    /**
     * 时间
     */
    private Long time;

    /**
     * 单位
     */
    private TimeUnit unit;

    public TimeModel(Long time, TimeUnit unit) {
        if (Objects.isNull(time) || Objects.isNull(unit)) {
            throw new CustomizeRedissonException(ReturnCode.LOCK_SERVICE_ERROR, "时间和单位均不能为空");
        }
        if (time <= 0) {
            throw new CustomizeRedissonException(ReturnCode.LOCK_SERVICE_ERROR, "时间需要大于0");
        }
        this.time = time;
        this.unit = unit;
    }

    /**
     * 转换成毫秒值
     */
    public long toMillis() {
        return unit.toMillis(time);
    }

    @Serial
    private static final long serialVersionUID = 5474154156951281980L;

}
