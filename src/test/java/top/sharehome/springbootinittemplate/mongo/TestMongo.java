package top.sharehome.springbootinittemplate.mongo;

import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import top.sharehome.springbootinittemplate.mongo.entity.UserMg;
import top.sharehome.springbootinittemplate.mongo.repository.UserMgRepository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试MongoDB类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class TestMongo {

    @Resource
    private UserMgRepository userMgRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 使用 Repository 测试 MongoDB CRUD
     * 这种方法适用于90%的基础使用场景，但是挺麻烦，遇到复杂操作编写接口方法比较困难，而且和实体类强相关
     */
    @Test
    void testMongoRepositoryCrud() {
        // 插入数据
        UserMg user1 = new UserMg()
                .setUserId(1L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        userMgRepository.save(user1);
        List<UserMg> users = new ArrayList<UserMg>();
        UserMg user2 = new UserMg()
                .setUserId(2L)
                .setName("cheng")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user2);
        UserMg user3 = new UserMg()
                .setUserId(3L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user3);
        UserMg user4 = new UserMg()
                .setUserId(4L)
                .setName("cheng")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user4);
        userMgRepository.saveAll(users);

        // 查询所有数据
        System.out.println("\n查询所有数据：");
        List<UserMg> all = userMgRepository.findAll();
        all.forEach(System.out::println);

        // 根据name条件查询数据
        System.out.println("\n根据name条件查询数据：");
        List<UserMg> nameAll = userMgRepository.findAll(
                Example.of(new UserMg()
                        .setName("antony"))
        );
        nameAll.forEach(System.out::println);

        // 根据name和userId条件查询数据
        System.out.println("\n根据name和userId条件查询数据：");
        List<UserMg> nameAndUserIdAll = userMgRepository.findAll(
                Example.of(new UserMg()
                        .setName("antony")
                        .setUserId(1L))
        );
        nameAndUserIdAll.forEach(System.out::println);

        // 根据name模糊查询数据
        System.out.println("\n根据name模糊查询数据：");
        List<UserMg> nameLikeAll = userMgRepository.findAllByNameLike("chen");
        nameLikeAll.forEach(System.out::println);

        // 根据name和userId模糊查询数据
        System.out.println("\n根据name和userId模糊查询数据：");
        List<UserMg> nameAndUserIdLikeAll = userMgRepository.findAllByNameLikeAndRoleLike("chen", "us");
        nameAndUserIdLikeAll.forEach(System.out::println);

        // 根据userId修改name，先插入，然后根据userId查询得到id，然后再封装修改后的对象进行修改（将姓名从update修改为afterUpdate）
        System.out.println("\n根据userId修改name：");
        UserMg user5 = new UserMg()
                .setUserId(5L)
                .setName("update")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        userMgRepository.save(user5);
        UserMg userMgInMongo = userMgRepository.findOne(Example.of(new UserMg().setUserId(5L))).orElseThrow(null);
        System.out.println("修改前：" + userMgInMongo);
        userMgRepository.save(userMgInMongo.setName("afterUpdate"));
        System.out.println("修改后：" + userMgRepository.findOne(Example.of(new UserMg().setUserId(5L))).orElseThrow(null));

        // 分页查询全部数据（在MongoDB中页码是从0开始的）
        System.out.println("\n分页查询全部数据：");
        Page<UserMg> pageAll = userMgRepository.findAll(PageRequest.of(0, 3));
        pageAll.forEach(System.out::println);
        System.out.println("第" + (pageAll.getNumber() + 1) + "页，包含" + pageAll.getNumberOfElements() + "条");
        System.out.println("共" + pageAll.getTotalPages() + "页，共" + pageAll.getTotalElements() + "条");

        // 根据单个条件模糊分页查询数据
        System.out.println("\n根据单个条件模糊分页查询数据：");
        Page<UserMg> pageSingleLike = userMgRepository.findAllByNameLike("ant", PageRequest.of(0, 1));
        pageSingleLike.forEach(System.out::println);
        System.out.println("第" + (pageSingleLike.getNumber() + 1) + "页，包含" + pageSingleLike.getNumberOfElements() + "条");
        System.out.println("共" + pageSingleLike.getTotalPages() + "页，共" + pageSingleLike.getTotalElements() + "条");

        // 根据多个条件模糊分页查询数据
        System.out.println("\n根据多个条件模糊分页查询数据：");
        Page<UserMg> pageMultipleLike = userMgRepository.findAllByNameLikeAndRoleLike("ant", "use", PageRequest.of(0, 1));
        pageMultipleLike.forEach(System.out::println);
        System.out.println("第" + (pageMultipleLike.getNumber() + 1) + "页，包含" + pageMultipleLike.getNumberOfElements() + "条");
        System.out.println("共" + pageMultipleLike.getTotalPages() + "页，共" + pageMultipleLike.getTotalElements() + "条");

        // 删除特定数据
        userMgRepository.delete(userMgRepository.findOne(Example.of(new UserMg().setUserId(5L))).orElseThrow(null));
        System.out.println("\n删除userId为5的数据之后剩下的数据：");
        userMgRepository.findAll().forEach(System.out::println);

        // 删除全部数据
        userMgRepository.deleteAll();
        System.out.println("\n所有数据删除完毕！");
    }

    /**
     * 使用 Template 测试 MongoDB CRUD
     * 这种方法简单，不和实体类强绑定，在涉及多个库时操作方便，但是较为复杂的操作需要编写MongoDB JSON进行操作，从某种意义上可以说他能解决100%的使用场景。
     */
    @Test
    void testMongoTemplateCrud() {
        // 插入数据
        UserMg user1 = new UserMg()
                .setUserId(1L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        mongoTemplate.insert(user1, UserMg.DOCUMENT);
        List<UserMg> users = new ArrayList<UserMg>();
        UserMg user2 = new UserMg()
                .setUserId(2L)
                .setName("cheng")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("admin")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user2);
        UserMg user3 = new UserMg()
                .setUserId(3L)
                .setName("antony")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user3);
        UserMg user4 = new UserMg()
                .setUserId(4L)
                .setName("cheng")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        users.add(user4);
        mongoTemplate.insertAll(users);

        // 查询所有数据
        System.out.println("\n查询所有数据：");
        List<UserMg> all = mongoTemplate.findAll(UserMg.class);
        all.forEach(System.out::println);

        // 根据name条件查询数据
        System.out.println("\n根据name条件查询数据：");
        List<UserMg> nameAll = mongoTemplate.find(
                Query.query(Criteria.where("name").is("antony")),
                UserMg.class,
                UserMg.DOCUMENT
        );
        nameAll.forEach(System.out::println);

        // 根据name和userId条件查询数据
        System.out.println("\n根据name和userId条件查询数据：");
        List<UserMg> nameAndUserIdAll = mongoTemplate.find(
                Query.query(
                        Criteria.where("name")
                                .is("antony")
                                .and("userId")
                                .is(1L)
                ),
                UserMg.class,
                UserMg.DOCUMENT
        );
        nameAndUserIdAll.forEach(System.out::println);

        // 根据name模糊查询数据
        System.out.println("\n根据name模糊查询数据：");
        List<UserMg> nameLikeAll = mongoTemplate.find(
                Query.query(
                        Criteria.where("name")
                                .regex(".*chen.*")
                ),
                UserMg.class,
                UserMg.DOCUMENT
        );
        nameLikeAll.forEach(System.out::println);

        // 根据name和userId模糊查询数据
        System.out.println("\n根据name和userId模糊查询数据：");
        List<UserMg> nameAndUserIdLikeAll = mongoTemplate.find(
                Query.query(
                        Criteria.where("name")
                                .regex(".*chen.*")
                                .and("role")
                                .regex(".*us.*")
                ),
                UserMg.class,
                UserMg.DOCUMENT
        );
        nameAndUserIdLikeAll.forEach(System.out::println);

        // 根据userId修改name，先插入，然后根据userId查询得到id，然后再封装修改后的对象进行修改（将姓名从update修改为afterUpdate）
        System.out.println("\n根据userId修改name：");
        UserMg user5 = new UserMg()
                .setUserId(5L)
                .setName("update")
                .setAccount("admin")
                .setPassword("123456")
                .setRole("user")
                .setIsDeleted(0)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        mongoTemplate.insert(user5, UserMg.DOCUMENT);
        UpdateResult updateResult = mongoTemplate.updateFirst(
                Query.query(
                        Criteria.where("userId")
                                .is(5L)
                ),
                Update.update("name", "afterUpdate"),
                UserMg.class, UserMg.DOCUMENT
        );
        System.out.println("被修改的数据数量：" + updateResult.getModifiedCount());
        System.out.println("修改后：" + mongoTemplate.findOne(
                Query.query(
                        Criteria.where("userId")
                                .is(5L)
                ),
                UserMg.class,
                UserMg.DOCUMENT));

        // 分页查询全部数据（在MongoDB中页码是从0开始的）
        System.out.println("\n分页查询全部数据：");
        // 构造分页对象
        Pageable pageableAll = PageRequest.of(0, 3);
        // 构造查询对象
        Query queryAll = new Query();
        // 带着查询对象查询总个数
        long countAll = mongoTemplate.count(queryAll, UserMg.class);
        // 带着分页对象和查询对象查询数据
        List<UserMg> pageDataAll = mongoTemplate.find(queryAll.with(pageableAll), UserMg.class, UserMg.DOCUMENT);
        // 构造分页结果
        Page<UserMg> pageAll = new PageImpl<>(pageDataAll, pageableAll, countAll);
        pageAll.forEach(System.out::println);
        System.out.println("第" + (pageAll.getNumber() + 1) + "页，包含" + pageAll.getNumberOfElements() + "条");
        System.out.println("共" + pageAll.getTotalPages() + "页，共" + pageAll.getTotalElements() + "条");

        // 根据单个条件模糊分页查询数据
        System.out.println("\n根据单个条件模糊分页查询数据：");
        // 构造分页对象
        Pageable pageableSingleLike = PageRequest.of(0, 1);
        // 构造查询对象
        Query querySingleLike = new Query(Criteria.where("name").regex(".*ant.*"));
        // 带着查询对象查询总个数
        long countSingleLike = mongoTemplate.count(querySingleLike, UserMg.class);
        // 带着分页对象和查询对象查询数据
        List<UserMg> pageDataSingleLike = mongoTemplate.find(querySingleLike.with(pageableSingleLike), UserMg.class, UserMg.DOCUMENT);
        // 构造分页结果
        Page<UserMg> pageSingleLike = new PageImpl<>(pageDataSingleLike, pageableSingleLike, countSingleLike);
        pageSingleLike.forEach(System.out::println);
        System.out.println("第" + (pageSingleLike.getNumber() + 1) + "页，包含" + pageSingleLike.getNumberOfElements() + "条");
        System.out.println("共" + pageSingleLike.getTotalPages() + "页，共" + pageSingleLike.getTotalElements() + "条");

        // 根据多个条件模糊分页查询数据
        System.out.println("\n根据多个条件模糊分页查询数据：");
        // 构造分页对象
        Pageable pageableMultipleLike = PageRequest.of(0, 1);
        // 构造查询对象
        Query queryMultipleLike = new Query(Criteria.where("name").regex(".*ant.*").and("role").regex(".*us.*"));
        // 带着查询对象查询总个数
        long countMultipleLike = mongoTemplate.count(queryMultipleLike, UserMg.class);
        // 带着分页对象和查询对象查询数据
        List<UserMg> pageDataMultipleLike = mongoTemplate.find(querySingleLike.with(pageableMultipleLike), UserMg.class, UserMg.DOCUMENT);
        // 构造分页结果
        Page<UserMg> pageMultipleLike = new PageImpl<>(pageDataMultipleLike, pageableMultipleLike, countMultipleLike);
        pageMultipleLike.forEach(System.out::println);
        System.out.println("第" + (pageMultipleLike.getNumber() + 1) + "页，包含" + pageMultipleLike.getNumberOfElements() + "条");
        System.out.println("共" + pageMultipleLike.getTotalPages() + "页，共" + pageMultipleLike.getTotalElements() + "条");

        // 删除特定数据
        mongoTemplate.remove(Query.query(Criteria.where("userId").is(5L)), UserMg.class, UserMg.DOCUMENT);
        System.out.println("\n删除userId为5的数据之后剩下的数据：");
        mongoTemplate.findAll(UserMg.class, UserMg.DOCUMENT).forEach(System.out::println);

        // 删除全部数据
        mongoTemplate.remove(new Query(), UserMg.class, UserMg.DOCUMENT);
        System.out.println("\n所有数据删除完毕！");
    }

}
