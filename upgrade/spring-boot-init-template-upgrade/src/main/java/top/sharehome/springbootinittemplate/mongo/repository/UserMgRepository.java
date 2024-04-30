package top.sharehome.springbootinittemplate.mongo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import top.sharehome.springbootinittemplate.config.mongo.annotation.MgRepository;
import top.sharehome.springbootinittemplate.mongo.entity.UserMg;

import java.util.List;

/**
 * 用户Mongo Repository接口
 *
 * @author AntonyCheng
 */
@MgRepository
public interface UserMgRepository extends MongoRepository<UserMg, String> {

    /**
     * 根据某个字段模糊查询示例
     *
     * @param name 模糊查询的姓名字段
     * @return 返回结果
     */
    List<UserMg> findAllByNameLike(String name);

    /**
     * 根据某个字段模糊分页查询示例
     *
     * @param name 模糊查询的姓名字段
     * @return 返回结果
     */
    Page<UserMg> findAllByNameLike(String name, Pageable pageable);

    /**
     * 根据某些字段模糊查询示例
     *
     * @param name 模糊查询的姓名字段
     * @param role 模糊查询的角色字段
     * @return 返回结果
     */
    List<UserMg> findAllByNameLikeAndRoleLike(String name, String role);

    /**
     * 根据某些字段模糊分页查询示例
     *
     * @param name 模糊查询的姓名字段
     * @param role 模糊查询的角色字段
     * @return 返回结果
     */
    Page<UserMg> findAllByNameLikeAndRoleLike(String name, String role, Pageable pageable);

}
