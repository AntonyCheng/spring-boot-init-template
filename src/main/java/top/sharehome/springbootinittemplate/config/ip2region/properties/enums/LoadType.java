package top.sharehome.springbootinittemplate.config.ip2region.properties.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 加载IP离线文件方式枚举类
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum LoadType {
    /**
     * 文件加载：
     * 基于文件的查询，每次查询进行磁盘IO操作
     * 系统内存不足，推荐使用FILE
     */
    FILE,
    /**
     * 索引加载：
     * 缓存VectorIndex索引查询，缓存文件中一部分数据到内存，减少磁盘IO操作
     * 系统内存大小适中，查询操作发生并不频繁，推荐使用INDEX
     */
    INDEX,
    /**
     * 内存加载：
     * 缓存整个文件查询，缓存文件所有数据，避免磁盘IO操作
     * 系统内存大小完全足够，具有较为频繁的查询操作，推荐使用MEMORY，这个也是模板默认的加载类型
     */
    MEMORY
}
