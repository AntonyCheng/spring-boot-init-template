package top.sharehome.springbootinittemplate.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import top.sharehome.springbootinittemplate.mongodb.entity.UserMg;

/**
 * 用户Mongo Repository接口
 * todo 想办法在非Mongo环境下不注入，参考Redis
 *
 * @author AntonyCheng
 */
@Repository
public interface UserMgRepository extends MongoRepository<UserMg, String> {
}
