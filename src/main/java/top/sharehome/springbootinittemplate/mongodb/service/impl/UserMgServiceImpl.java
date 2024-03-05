package top.sharehome.springbootinittemplate.mongodb.service.impl;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.mongodb.service.UserMgService;

import javax.annotation.Resource;

/**
 * 用户MongoDB服务类
 * todo 想办法在非Mongo环境下不注入，参考Redis
 *
 * @author AntonyCheng
 */
@Service
public class UserMgServiceImpl implements UserMgService {

    @Resource
    private MongoTemplate mongoTemplate;

}
