# SpringBoot初始化模板

> 作者：[AntonyCheng](https://github.com/AntonyCheng})
> 基于：[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 开源协议

> **版本号：v1.x.x**

基于 Java Web 项目 SpringBoot 框架初始化模板，整合了常用的框架，保证大家在此基础上能够快速开发自己的项目，该模板针对于后端启动开发小而精，本项目会由作者持续更新。

* [模板特点](#模板特点)
  * [主流框架](#主流框架)
  * [业务特性](#业务特性)
* [业务功能](#业务功能)
  * [示例业务](#示例业务)
  * [单元测试](#单元测试)
* [快速上手](#快速上手)
  * [必须执行](#必须执行)
  * [可选执行](#可选执行)
    * [整合缓存服务](#整合缓存服务)
      * [整合Redis（系统缓存）](#整合redis系统缓存)
      * [整合Redisson（业务缓存）](#整合redisson业务缓存)
    * [整合消息队列](#整合消息队列)
      * [激活消息队列](#激活消息队列)
      * [自定义消息队列](#自定义消息队列)
    * [整合ElasticSearch](#整合elasticsearch)
    * [整合对象存储服务](#整合对象存储服务)
      * [整合腾讯云COS](#整合腾讯云cos)
      * [整合MinIO](#整合minio)
      * [整合阿里云OSS](#整合阿里云oss)
    * [配置SaToken](#配置satoken)
      * [开启鉴权](#开启鉴权)
      * [开启认证](#开启认证)
      * [开启JWT](#开启jwt)
        * [整合Redis](#整合redis)
        * [整合JWT](#整合jwt)
    * [配置XXL-JOB](#配置xxl-job)
      * [SpringBoot任务调度](#springboot任务调度)
      * [XXL-JOB任务调度](#xxl-job任务调度)
    * [配置SpringBootAdmin](#配置springbootadmin)
* [申明&联系我](#申明联系我)

## 模板特点

### 主流框架

- **Java 11**
- **SpringBoot 2.7.x**
    - freemarker == 模板引擎
    - web == SpringMVC
    - undertow == Java Web 服务器
    - aop == 面向切面编程
    - devtools == 热部署插件
- **MySQL**
    - mybatis-plus 3.5.4 == MySQL 持久层操作框架
    - JDBC 8.0.33 == Java 连接 MySQL 依赖
    - ShardingSphere 5.3.2 == 分布式数据库解决方案
- **工具类**
    - Lombok 1.18.30
    - hutool 5.8.22
    - commons-lang3 3.12.0
- **权限校验**
    - SaToken 1.37.0
        - SaToken Core 1.37.0
        - SaToken JWT 1.37.0
        - SaToken Redis 1.37.0
- **缓存服务**
    - spring-boot-starter-data-redis
    - spring-boot-starter-cache
    - redisson 3.23.5 == Redis的基础上实现的Java驻内存数据网格
- **消息队列**
    - rabbitMQ
        - spring-boot-starter-amqp
- **搜索引擎**
    - Elastic Stack
        - elasticsearch 7.14.0
        - elasticsearch-rest-high-level-client 7.14.0
        - logstash-logback-encoder 7.3
    - easy-es-boot-starter 2.0.0-bata4 == 简化ElasticSearch搜索引擎，可以像Mybatis-Plus操作MySQL一样操作的开源框架
- **对象存储（OSS）**
    - 腾讯云 COS 5.6.155
    - 阿里云 OSS 3.15.1
    - MinIO 8.5.6
- **文件操作**
    - POI 5.2.3 == 操作 Word
    - EasyExcel 3.3.1 == 操作 Excel
    - itext 7.2.5 == 操作 PDF
- **外接平台**
    - XXL-JOB 2.4.0 == 分布式定时任务管理平台
    - SpringBootAdmin 2.7.4 == SpringBoot服务监控平台

### 业务特性

- 使用 Undertow 服务器替换掉 Tomcat 服务器
- SaToken 分布式可配置登录&认证&鉴权
- 全局请求拦截器&过滤器
- 全局异常处理器
- 封装统一响应对象
- 自定义响应码
- Swagger + Knife4j 接口文档
- 全局跨域处理
- Spring 上下文处理工具
- JSON 长整型精度处理
- 自动字段填充器

## 业务功能

### 示例业务

- 提供模板 SQL 示例文件（业务数据库 & XXL-JOB数据库）；
- 用户登录、注册、注销、信息获取；
- Spring Scheduler 单机版定时任务示例。

### 单元测试

- JUnit5 单元测试；
- 示例单元测试类。

## 快速上手

> 拉取项目模板之后需要确保所有依赖下载完成，以下的操作都是针对于 application.yaml 文件。

### 必须执行

1. 执行 `sql/init_db.sql` 和 `sql/init_xxl_job.sql` 文件；

2. 修改 `src/main/resources/mysql.yaml` 文件：

   ```yaml
   dataSources:
     master:
       dataSourceClassName: com.zaxxer.hikari.HikariDataSource
       driverClassName: com.mysql.cj.jdbc.Driver
       # 修改url地址
       url: jdbc:mysql://xxx.xxx.xxx.xxx:3306/init_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
       username: root
       password: 123456
   ```

   > 在这个文件中还能看到很多其他的配置，如有需要，请开发者自行学习 ShardingSphere 框架，理解相关配置；

3. 直到这一步之后，模板就已经可以直接启动了，访问 `http://localhost:38080/api/doc.html#/home` 即可打开接口文档。

### 可选执行

> 为了方便开发者快速找到配置文件需要修改的位置，一律使用 todo 代办进行标识，请务必“**必须执行**”。

#### 整合缓存服务

**说明**：该模板中存在两种 Redis 服务，第一种是系统缓存服务（ **对应整合 Redis** ），第二种是业务缓存服务（ **对应整合 Redisson** ）。前者承担系统框架本身的缓存服务，例如用户分布式登录信息的缓存；后者承担开发者业务逻辑所需的缓存操作，例如分布式锁、限流工具等。

##### 整合Redis（系统缓存）

系统缓存服务主要为一些依赖 spring-boot-starter-data-redis 原生操作的框架而设计，例如模板中用于校验权限的 SaToken 框架就有借用 Redis 进行分布式登录或校验的需求，系统缓存的过程对开发者能做到透明。

1. 取消排除 `RedisAutoConfiguration`  依赖：

   ```yaml
   spring:
     autoconfigure:
       exclude:
         # todo 是否开启Redis依赖类（如果要打开Redis配置，就将RedisAutoConfiguration注释掉，该配置类一旦被注释，就需要设置redis相关配置，redisson相关配置也需要依赖这个类，预先关闭）
         #- org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
   ```

2. 修改 Redis 相关配置，切记注意单机模式和集群模式无法共存，默认开启单机模式，注释掉集群模式相关代码，同时默认没有密码，所以密码也被注释掉：

   ```yaml
   spring: 
     # 修改系统缓存redis配置（这里的redis配置主要用于鉴权认证等模板自带服务的系统缓存服务）
     redis:
       # 单机地址（单价模式配置和集群模式配置只能存在一个）
       host: xxx.xxx.xxx.xxx
       # 单机端口，默认为6379
       port: 6379
       # 集群地址（单价模式配置和集群模式配置只能存在一个）
       #cluster:
       #  nodes:
       #    - xxx.xxx.xxx.xxx:6379
       #    - xxx.xxx.xxx.xxx:6380
       #    - xxx.xxx.xxx.xxx:6381
       #    - xxx.xxx.xxx.xxx:6382
       #    - xxx.xxx.xxx.xxx:6383
       #    - xxx.xxx.xxx.xxx:6384
       # 数据库索引
       database: 0
       # 密码（考虑是否需要密码）
       #password: 123456
       # 连接超时时间
       timeout: 3s
       # redis连接池
       lettuce:
         pool:
           # 最小空闲连接数
           min-idle: 8
           # 最大空闲连接数
           max-idle: 25
           # 最大活动连接数
           max-active: 50
           # 最大等待时间/ms
           max-wait: 3000
   ```

3. 此时项目就能够直接启动， Redis 相关配置就完成了，特别说明一下，为了适应模板的通用性，该模板中依旧保留了 spring-boot-starter-data-redis 中 RedisTemplate 的原生操作途径，在`config/redis` 包中设计了 RedisTemplate 的 Bean，同时更新了其序列化方式以防止存入 Redis 之后出现乱码，这意味着开发者依旧可以使用 RedisTemplate 的方式将系统缓存和业务缓存合二为一，这种保留仅仅是为了可拓展性，所以没有围绕 RedisTemplate 编写缓存工具类，如果需要使用缓存工具类，详情见 **整合 Redisson** 。

##### 整合Redisson（业务缓存）

业务缓存服务主要是为了满足开发者在编码过程中的缓存需求，例如接口限流、分布式锁等。

1. 修改 Redisson 配置，此时单机版本和集群版本的启动状态可以自定义：

   - 都不开启（都为 false ）：模版不会将 Redisson 相关依赖纳入反转控制容器中；
   - 仅开启一个；
   - 都开启（都为 true ）：模版只会加载单机版本的 Redisson 配置；

   ```yaml
   # 修改redisson配置（这里的redisson配置主要用来系统业务逻辑的缓存服务）
   # 如果同时开启单机版本和集群版本，只有单机版本生效
   redisson:
     # 线程池数量
     threads: 4
     # Netty线程池数量
     nettyThreads: 8
     # redis单机版本
     singleServerConfig:
       # todo 是否启动单机Redis（Redisson）缓存（预先关闭）
       enableSingle: true
       # 单机地址（一定要在redis协议下）
       address: redis://xxx.xxx.xxx.xxx:6379
       # 数据库索引
       database: 1
       # 密码（考虑是否需要密码）
       #password: 123456
       # 命令等待超时，单位：毫秒
       timeout: 3000
       # 发布和订阅连接池大小
       subscriptionConnectionPoolSize: 50
       # 最小空闲连接数
       connectionMinimumIdleSize: 8
       # 连接池大小
       connectionPoolSize: 32
       # 连接空闲超时，单位：毫秒
       idleConnectionTimeout: 10000
     # redis集群版本
     clusterServersConfig:
       # todo 是否启动集群redisson（Redisson）缓存（预先关闭）
       enableCluster: false
       # redis集群节点（一定要在redis协议下）
       nodeAddresses:
         - redis://xxx.xxx.xxx.xxx:6379
         - redis://xxx.xxx.xxx.xxx:6380
         - redis://xxx.xxx.xxx.xxx:6381
         - redis://xxx.xxx.xxx.xxx:6382
         - redis://xxx.xxx.xxx.xxx:6383
         - redis://xxx.xxx.xxx.xxx:6384
       # 密码（考虑是否需要密码）
       #password: 123456
       # master最小空闲连接数
       masterConnectionMinimumIdleSize: 32
       # master连接池大小
       masterConnectionPoolSize: 64
       # slave最小空闲连接数
       slaveConnectionMinimumIdleSize: 32
       # slave连接池大小
       slaveConnectionPoolSize: 64
       # 连接空闲超时，单位：毫秒
       idleConnectionTimeout: 10000
       # 命令等待超时，单位：毫秒
       timeout: 3000
       # 发布和订阅连接池大小
       subscriptionConnectionPoolSize: 50
   ```

2. 此时项目就能够直接启动， Redisson 相关配置就完成了，模板为了降低开发者的模板使用门槛，特意针对 Redisson 进行进一步封装，在 `utils/redisson` 包中设计了缓存工具类 CacheUtils 、限流工具类 RateLimitUtils 以及 LockUtils 分布式锁工具类供开发者使用，使用参考示例单元测试类。

#### 整合消息队列

**说明**：该模板中存在一套较为全面的 RabbitMQ 消息队列解决方案，具有消费者手动 ACK 示例、死信队列、延迟队列等特性，开发者不仅可以直接使用模板中存在的默认队列，还可以根据自己的需求对原有的解决方案进行拓展以设计出满足自身项目特色的消息队列，同时该模板对拓展的消息队列具有一定规则性兼容。

##### 激活消息队列

1. 修改消息队列相关配置，同时选择配置单机 RabbitMQ 或者集群 RabbitMQ ，切记这两者无法共存，使用其中一个配置的同时需要把另一个配置给注释或者删除掉（不建议删除），然后根据自己搭建的 RabbitMQ 进行相关配置：

   ```yaml
   spring: 
     # 修改Rabbitmq配置
     rabbitmq:
       # todo 是否开启RabbitMQ （预先关闭）
       enable: true
       # 单机RabbitMQ IP（单价模式配置和集群模式配置只能存在一个）
       host: xxx.xxx.xxx.xxx
       # 单机RabbitMQ端口
       port: 5672
       # 集群RabbitMQ（单价模式配置和集群模式配置只能存在一个）
       #addresses: xxx.xxx.xxx.xxx:5672,xxx.xxx.xxx.xxx:5673,xxx.xxx.xxx.xxx:5674
       # 使用到的虚拟主机
       virtual-host: /
       # 用户名
       username: admin
       # 密码
       password: admin123456
       # 消息确认（ACK）
       publisher-confirm-type: correlated #确认消息已发送到交换机(Exchange)
       publisher-returns: true #确认消息已发送到队列(Queue)
       template:
         mandatory: true
       # 是否手动ACK
       listener:
         type: simple
         direct:
           acknowledge-mode: manual
         simple:
           acknowledge-mode: manual
   ```

2. 配置好之后即可启动项目，在模板中存在默认的三个消息队列配置，它们在 `config/rabbitmq/default_mq` 包下，分别是 `DefaultRabbitMq` （默认的普通队列）、`DefaultRabbitMqWithDlx` （默认的带有死信队列的消息队列）、`DefaultRabbitMqWithDelay` （默认的延迟队列），这三个队列可以直接使用，它们在 `utils/rabbitmq/RabbitMqUtils` 工具类中由“default”开头的方法，当然开发者也可以通过一定规则自定义消息队列，这些消息队列会与 RabbitMqUtils 工具类兼容。

##### 自定义消息队列

1. 关于自定义消息队列，这里给出最简单的方式，首先开发者需要考虑自定义哪种形式的消息队列：

    - 普通消息队列：即最基础的消息队列，能够支持最基本的消息投递和消息消费的能力，简单易懂；
    - 带有死信队列的消息队列：与普通消息队列相比，带有死信队列的消息队列能够在消息被拒绝消费之后选择将消息列入死信队列中，而并不是直接丢弃或者再次扔进队列中等待消费，这种的应用场景多用于不可丢失消息的处理或者对拒绝消费的消息再处理；
    - 延迟队列：投递消息之后并不能从队列中马上取出消息进行消费；

   这些具体的消息队列特性往往需要开发者掌握一定的前置知识基础；

2. 然后粘贴 `config/default_mq/DefaultRabbitMq` 类，复制到 `config/customize_mq` 包中，并且重命名（在 DefaultRabbitMq 的文档注释中有具体说明），注意不要和 DefaultRabbitMq 类重复，不然会造成反转控制冲突，这里假设命名成 TestRabbitMq ；

3. 在 TestRabbitMq 中进行文本替换，将所有 “default” 替换成 “test” 即可，如果想要使用自定义的消息队列配置，直接使用 RabbitMqUtils 工具类中形参带有 “Class” 类型的方法即可（详情见 RabbitMqUtils 文档注释）。

注意：这里只给出了最简单的自定义方式，开发者在理解这种“替换”变换和阅读相关源码的基础上可以对模板进行更加自定义的改造。

#### 整合ElasticSearch

与其说是整合 ElasticSearch 搜索引擎，不如说是整合 Easy-ES 框架，正因为 Easy-ES 框架过于强大， ElasticSearch 那种繁琐的操作才能得以简化，但是不要依赖这个工具而不去深入了解学习 ElasticSearch 这个伟大的搜索引擎，Easy-ES 的开发者就已经说得很明白了：这套框架的是站在 elasticsearch-rest-high-level-client 和 Mybatis-Plus 的肩膀上创作的，而前者的底层操作逻辑正是 ElasticSearch 的操作逻辑。

在使用该模板中的 ElasticSearch 相关功能之前一定要前往 Easy-ES 官网将快速入门部分通读一遍，要有一个大概的了解才能容易上手。

1. 修改 Easy-ES 相关配置，重点关注 ElasticSearch 的部署地址，由于框架自身原因， ElasticSearch 的相关依赖被固定在 **7.14.0** ，好在 ElasticSearch 在小版本之间兼容性还不错，所以理论上部署 7.x.x 的 EalsticSearch 即可满足要求，当然推荐部署 7.14.0 版本的 ElasticSearch ，然而 ElasticSearch 7.x.x 这个大版本依旧在更新维护，所以可以放心使用：

   ```yaml
   # Easy-ES配置，需要使用ElasticSearch时将enable改为true
   easy-es:
     # todo 是否启动（预先关闭）
     enable: true
     # es连接地址+端口 格式必须为ip:port,如果是集群则可用逗号隔开
     address: xxx.xxx.xxx.xxx:9200
     # 如果无账号密码则可不配置此行
     #username:
     # 如果无账号密码则可不配置此行
     #password:
     # 默认为http 可缺省
     schema: http
     # 默认为true 打印banner 若您不期望打印banner,可配置为false
     banner: false
     # 心跳策略时间 单位:ms
     keep-alive-millis: 30000
     # 连接超时时间 单位:ms
     connect-timeout: 5000
     # 通信超时时间 单位:ms
     socket-timeout: 600000
     # 连接请求超时时间 单位:ms
     connection-request-timeout: 5000
     # 最大连接数 单位:个
     max-conn-total: 100
     # 最大连接路由数 单位:个
     max-conn-per-route: 100
     global-config:
       # 索引处理模式,smoothly:平滑模式, not_smoothly:非平滑模式, manual:手动模式,,默认开启此模式
       process-index-mode: manual
       # 开启控制台打印通过本框架生成的DSL语句,默认为开启,测试稳定后的生产环境建议关闭,以提升少量性能
       print-dsl: true
       # 当前项目是否分布式项目,默认为true,在非手动托管索引模式下,若为分布式项目则会获取分布式锁,非分布式项目只需synchronized锁.
       distributed: false
       # 重建索引超时时间 单位小时,默认72H可根据ES中存储的数据量调整
       reindexTimeOutHours: 72
       # 异步处理索引是否阻塞主线程 默认阻塞 数据量过大时调整为非阻塞异步进行 项目启动更快
       async-process-index-blocking: true
       db-config:
         # 是否开启下划线转驼峰 默认为false
         map-underscore-to-camel-case: false
         # 索引前缀,可用于区分环境  默认为空 用法和MP的tablePrefix一样的作用和用法
         index-prefix: template_
         # id生成策略 customize为自定义,id值由用户生成,比如取MySQL中的数据id,如缺省此项配置,则id默认策略为es自动生成
         id-type: customize
         # 数据刷新策略,默认为不刷新,若对数据时效性要求比较高,可以调整为immediate,但性能损耗高,也可以调整为折中的wait_until
         refresh-policy: immediate
   ```

2. 配置完之后已经就能够使用一些方法了，但是并不是全部，想要让框架功能得以发挥就必须有操作 Mybatis-Plus 的思想，具体代码示例在最后一步，下面仅作文字说明；

3. 准备一个包来存放属于 Easy-ES 映射接口，然后在项目启动类上使用 @EsMapperScan 注解注明该包的全包名：

   ```java
   ......
   @EsMapperScan("xxx.xxx.xxx.mapper")
   ......
   public class MainApplication {
   
       public static void main(String[] args) {
           SpringApplication.run(MainApplication.class, args);
       }
   
   }
   ```

4. 创建一个包来存放属于 Easy-ES 的实体类，在实体类上使用 @IndexName 注解注明该类所对应的索引名（类似于 MySQL 中的表名），强制该模型存在一个 id 字段来作为实体类的 id ，这个 id 非常重要，在 Easy-ES 框架有许多方法依赖实体类 id；

   ```java
   ......
   @IndexName("t_user")
   ......
   public class UserEs implements Serializable {
       ......
       private Long id;
       ......
   }
   ```

5. 该模板中也提供了使用的一个范例，在 `elasticsearch` 包中，同时在测试用例中也存在相关框架操作，如果还想了解更多，请在 Easy-ES 官网自行学习。

#### 整合对象存储服务

**说明**：对象存储是一种计算机数据存储架构，旨在处理大量非结构化数据，说直白点主要就是存储文件这一类数据，其中腾讯云 COS 和 MinIO 是可以对文件进行网页预览的，而阿里云 OSS 则需要配置自定义域名，所以针对于个人的中小型项目，推荐优先使用腾讯云 COS 和 MinIO 对象存储服务，以免给自己挖坑。

##### 整合腾讯云COS

该模块中整合腾讯云 COS 非常容易，仅仅需要开发者开通腾讯云 COS 服务，从中获取到一些必要的参数：

- region ==> 地域
- secretId ==> 用户公钥
- secretKey ==> 用户私钥
- bucketName ==> 桶名称

然后将这些参数写入 `application.yaml` 文件中，同时开启 enable 配置项：

```yaml
# 对象存储配置
oss:
  # 腾讯云COS配置
  tencent:
    # todo 是否开启（预先关闭）
    enable: true
    # 地域
    region: ap-chengdu
    # 用户的 SecretId，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    secretId: xxx
    # 用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
    secretKey: xxx
    # 桶名称
    bucketName: test-xxx
```

修改完之后即可使用模板中对象存储工具类 `TencentUtils` ，这个类中提供文件上传和文件删除的操作，至于文件下载，通常是上传后拿到文件地址，当需要下载时直接访问文件地址即可。

##### 整合MinIO

该模块中整合 MinIO 非常容易，仅仅需要开发者部署 MinIO 服务，从中获取到一些必要的参数：

- endpoint ==> 域名
- enableTls ==> 是否开启TLS
- secretId ==> 用户公钥
- secretKey ==> 用户私钥
- bucketName ==> 桶名称

然后将这些参数写入 `application.yaml` 文件中，同时开启 enable 配置项：

```yaml
# 对象存储配置
oss:
  # MinIO OSS配置
  minio:
    # todo 是否开启（预先关闭）
    enable: false
    # 域名（格式：【ip:port】）
    endpoint: xxx.xxx.xxx.xxx:9000
    # 是否开启TLS
    enableTls: false
    # 用户的 SecretId
    secretId: xxx
    # 用户的 SecretKey
    secretKey: xxx
    # 桶名称
    bucketName: xxx
```

修改完之后即可使用模板中对象存储工具类 `MinioUtils` ，这个类中提供文件上传和文件删除的操作，至于文件下载，通常是上传后拿到文件地址，当需要下载时直接访问文件地址即可。

##### 整合阿里云OSS

该模块中整合阿里云 OSS 非常容易，仅仅需要开发者开通阿里云 OSS 服务，从中获取到一些必要的参数：

- endpoint==> 域名
- secretId ==> 用户公钥
- secretKey ==> 用户私钥
- bucketName ==> 桶名称

然后将这些参数写入 `application.yaml` 文件中，同时开启 enable 配置项：

```yaml
# 对象存储配置
oss:
  # 阿里云OSS配置
  ali:
    # todo 是否开启（预先关闭）
    enable: false
    # 域名 以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    endpoint: https://oss-xx-xxx.aliyuncs.com
    # 用户的 SecretId
    secretId: xxx
    # 用户的 SecretKey
    secretKey: xxx
    # 桶名称
    bucketName: xxx
```

修改完之后即可使用模板中对象存储工具类 `AliUtils` ，这个类中提供文件上传和文件删除的操作，至于文件下载，通常是上传后拿到文件地址，当需要下载时直接访问文件地址即可。

#### 配置SaToken

**说明**：与其说配置 SaToken ，不如说是介绍该模板中封装的三个 SaToken 特性：

```yaml
# Sa-Token配置
sa-token:
  # todo 是否开启鉴权（不开启鉴权就意味SaCheckRole和SaCheckPermission失效，预先开启）
  enableAuthorization: true
  # todo 是否开启认证（不开启认证就意味着所有接口无论是否使用Sa-Token注解，均开放，预先开启）
  enableIdentification: true
  # todo 是否使用jwt（建议如果没有开启redis就不要开启jwt，预先关闭）
  enableJwt: false
```

至于 SaToken 具体的使用说明，请前往其官方网站仔细阅读文档，接下来以文字的方式对三个封装后的特性挨个介绍。

##### 开启鉴权

鉴权其实就是查看某个用户对于某个接口、某个功能或者某个服务是否具有操作的权限，在该模板自带的数据库中有一个 `t_user` 表，表里有一个 `role` 角色字段，框架自带的业务逻辑就是用户登录后会保存一个登录信息，每次操作之前都去登录信息中获取角色信息， SaToken 框架会自动去对比角色信息，从而做到鉴权，当然开发者们可以按照自己的需求去设计鉴权内容，并不使用 `role` 角色字段，所以要求开发者自行编写符合自己需求的代码，而需要更改的文件就是 `config/security/AuthorizationConfiguration` 类：

```java
@Component
@Slf4j
public class AuthorizationConfiguration implements StpInterface {

    /**
     * 重写权限方法
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 返回结果
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 根据SaToken权限配置文档：https://sa-token.cc/doc.html#/use/jur-auth
        // 由于此处设计主要针对于接口权限，所以权限通常有多个，上帝权限和个别极端情况除外
        // 需要使用"."将每一级权限给分离开来，支持权限通配符操作，"*"表示上帝权限
        return Collections.singletonList("*");
    }

    /**
     * 重写角色方法
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return 返回结果
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 根据SaToken权限配置文档：https://sa-token.cc/doc.html#/use/jur-auth
        // 由于此处设计主要针对于用户角色，所以角色通常只有一个，个别情况除外
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        String userRole = session.get(USER_ROLE_KEY, "");
        return Collections.singletonList(userRole);
    }

}
```

##### 开启认证

认证主要负责校验用户的在线状态，大多数系统的认证逻辑就是用户没有登录就不能使用绝大部分系统功能，该模板默认实现认证功能，但是在长期的开发过程中就会发现各式各样的框架型模板调试过程会因为认证功能而变得麻烦。所以该模板允许开发者决定是否开启认证功能，也就是说开发者能够不需要登录去调试代码，也可以发布不需要登录的 Web 项目。

##### 开启JWT

###### 整合Redis

由于 JWT 无状态且可解析，避免存在篡改之后对系统进行操作，强烈建议不要单独使用，将其存入 Redis 缓存数据库中交给系统直接管理，此时就需要整合 Redis ，这一步相比于单独整合缓存服务中的整合 Redis 多一些步骤，可以说这一步是其超集，但是也很简单，见如下配置文件修改：

```yaml
spring:
  autoconfigure:
    exclude:
      # todo 是否开启Redis依赖类（如果要打开Redis配置，就将RedisAutoConfiguration注释掉，该配置类一旦被注释，就需要设置redis相关配置，redisson相关配置也需要依赖这个类，预先关闭）
      #- org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
      # todo 是否使用Redis搭配SaToken鉴权认证（如果需要，就将RedisAutoConfiguration和SaTokenDaoRedisJackson注释掉，预先关闭）
      #- cn.dev33.satoken.dao.SaTokenDaoRedisJackson
  # 修改系统缓存redis配置（这里的redis配置主要用于鉴权认证等模板自带服务的系统缓存服务）
  redis:
    # 单机地址（单价模式配置和集群模式配置只能存在一个）
    host: xxx.xxx.xxx.xxx
    # 单机端口，默认为6379
    port: 6379
    # 集群地址（单价模式配置和集群模式配置只能存在一个）
    #cluster:
    #  nodes:
    #    - xxx.xxx.xxx.xxx:6379
    #    - xxx.xxx.xxx.xxx:6380
    #    - xxx.xxx.xxx.xxx:6381
    #    - xxx.xxx.xxx.xxx:6382
    #    - xxx.xxx.xxx.xxx:6383
    #    - xxx.xxx.xxx.xxx:6384
    # 数据库索引
    database: 0
    # 密码（考虑是否需要密码）
    password: 123456
    # 连接超时时间
    timeout: 3s
    # redis连接池
    lettuce:
      pool:
        # 最小空闲连接数
        min-idle: 8
        # 最大空闲连接数
        max-idle: 25
        # 最大活动连接数
        max-active: 50
        # 最大等待时间/ms
        max-wait: 3000
```

修改完毕之后即自动使用 Redis 缓存登陆凭证信息，注意这里的登录凭证信息包含 JWT 或者 Session + Cookie，如果没有整合 JWT ，那么该系统就是分布式 Session 形式，反之则是分布式 Token 形式。

###### 整合JWT

JWT 全称是 JSON Web Tokens ，见名知意， JWT 就是一种内容为 JSON 的校验凭证，Web 应用凭证校验的方式一般分为两种：一种是 Session + Cookie，另一种就是 JWT，前者主要特点就是单机式、服务端管理凭证，后者主要特点就是分布式、客户端管理凭证，两种方式各有千秋，具体优劣请移步于百度，但要注意 JWT 是一种可解析的凭证，也就是说一旦客户端拿到这个凭证就能拿到其中的明文信息，所以通常让 JWT 和 Redis 搭配使用，不交给用户直接管理，所以该模板中默认不使用 JWT 的凭证模式，开发者需要自行开启。

#### 配置XXL-JOB

XXL-JOB 是一个开箱即用的轻量级分布式任务调度系统，其核心设计目标是开发迅速、学习简单、轻量级、易扩展，在开源社区广泛流行，已在多家公司投入使用。 XXL-JOB 开源协议采用的是 GPL ，因此云厂商无法直接商业化托管该产品，各大中小企业需要自建，增加了学习成本、机器成本、人工运维成本，在真正接入 XXL-JOB 分布式任务调度系统之前，开发者先了解一下在 SpringBoot 项目中最简单的任务调度方式。

##### SpringBoot任务调度

SpringBoot 中自带有一些建议的任务调度方案，我们通常将其称为“定时任务”，模板中这样的定时任务主要分为两类，第一类是全量任务，第二类是循环任务；

1. 在编码之前首先修改 `application.yaml` 配置文件：

   ```yaml
   # 配置SpringBoot任务调度
   schedule:
     # 全量任务配置
     once:
       # todo 是否开启全量任务（预先关闭）
       enable: true
     # 循环任务配置
     cycle:
       # todo 是否开启循环任务（预先关闭）
       enable: true
       # 线程池大小（开启则必填）
       threadPool: 10
   ```

2. 全量任务指的是在 SpringBoot 项目程序启动时所执行的任务，举个例子：有一些非常常用的数据存储于 MySQL 当中，为了提高系统性能，我们通常会把这些数据存入 Redis 缓存当中，然后每次 Redis 中访问数据，此时就应该考虑是否开启全量任务进行数据“内存化”的操作，简而言之，全量任务就类似于整个系统的初始化任务；

   全量任务需要实现 CommandLineRunner 接口，重写接口中的 run 方法即可，模板中示例代码被放在 `job/standalone/once` 包中：

   ```java
   @Component
   public class OnceJob implements CommandLineRunner {
   
       @Override
       public void run(String... args) throws Exception {
           ......
       }
   
   }
   ```

3. 循环任务指的是现实意义上的定时任务，举个例子：当我们每天凌晨 3 点需要更新系统的 Redis 缓存，不现实的做法就是写一个对外接口，维护人员在每天凌晨 3 点去手动调用，或者非常麻烦的做法就是额外写一个功能脚本，对它进行维护和运行，面对这样的问题需求， SpringBoot 框架已经给出了相对应的解决方案，即循环任务；

   循环任务需要在需要循环的任务上使用 @Scheduled 注解，该注解中有两个比较重要的字段：fixedDelay 和 cron ，前者就是以 SpringBoot 项目启动时间为基准往后间隔固定时长运行任务，后者就是以 Crontab 表达式为基准运行任务，模板中示例代码放在 `job/standalone/once` 包中：

   ```java
   @Component
   public class CycleJob {
   
       /**
        * 固定间隔时间任务
        * 注解表示一分钟执行一次
        */
       @Scheduled(fixedDelay = 60 * 1000)
       public void fixedTimeJob() {
           ......
       }
   
       /**
        * 定时任务
        * 注解使用Cron表达式(cron = "秒 分 时 天 月 周")
        */
       @Scheduled(cron = "0 * * * * *")
       public void scheduledTimeJob() {
           ......
       }
   
   }
   ```

##### XXL-JOB任务调度

1. 部署 XXL-JOB 分布式调度系统控制面板；

   想要使用 XXL-JOB 分布式任务调度系统的功能，就需要先部署一个 XXL-JOB 分布式调度系统控制面板，得益于 Java 生态的完备，开发者可以直接使用模板中已经继承好的 XXL-JOB 模块来部署一个 XXL-JOB 分布式调度系统控制面板，在 `module` 文件夹中有一个 xxl-job-admin 模块，首先需要修改 XXL-JOB 模块的 `application.yaml` 配置文件，此时在“必须执行”的操作中引入的 `sql/init-xxl-job.sql` 就起到了作用：

   ```yaml
   spring:
     # 配置XXL-JOB的MySQL数据库
     datasource:
       url: jdbc:mysql://xxx.xxx.xxx.xxx:3305/init_xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
       username: root
       password: 123456
       driver-class-name: com.mysql.cj.jdbc.Driver
       type: com.zaxxer.hikari.HikariDataSource
       hikari:
         minimum-idle: 10
         maximum-pool-size: 30
         auto-commit: true
         idle-timeout: 30000
         pool-name: HikariCP
         max-lifetime: 900000
         connection-timeout: 10000
         connection-test-query: SELECT 1
         validation-timeout: 1000
   ```

   部署完成之后即可启动 XXL-JOB 分布式调度系统控制面板，启动成功即表示部署成功；

2. 然后修改模板模块 `application.yaml` 配置文件的相关内容，在保证 XXL-JOB 控制面板地址正确的前提下打开 `enable` 配置项：

   ```yaml
   # Xxl-Job配置（如果是导入了模板sql，那么登录账号/密码为：admin/123456）
   xxl:
     job:
       # todo 是否开启（预先关闭）
       enable: true
       # Xxl-Job监控面板地址
       adminAddresses: http://localhost:38079/xxl-job-admin
       # Xxl-Job token
       accessToken: xxl-job
       # 执行器配置
       executor:
         # 执行器AppName：执行器心跳注册分组依据；为空则关闭自动注册
         appname: xxl-job-executor
         # 执行器端口号 执行器从38080开始往后写
         port: 38081
         # 执行器注册：默认IP:PORT（appname不为空，该处则可为空）
         address:
         # 执行器IP：默认自动获取IP（appname不为空，该处则可为空）
         ip:
         # 执行器运行日志文件存储磁盘路径
         logpath: ./logs/${spring.application.name}/xxl-job
         # 执行器日志文件保存天数：大于3生效
         logretentiondays: 30
   ```

3. 该模板提供了各种调度任务的示例代码，这些代码放在 `job/distributed/service/SampleService` 类中，至此模板中关于 XXL-JOB 的内容就结束了，如果想要使用 XXL-JOB 分布式调度系统，请前往其官方网站仔细阅读文档并且按要求编码。

#### 配置SpringBootAdmin

SpringBoot Admin 能够将 Actuator 中的信息进行界面化的展示，也可以监控所有 Spring Boot 应用的健康状况，提供实时警报功能，和 XXL-JOB 一样需要先部署，当然在该模板中的 `module` 文件夹中有一个 spring-boot-admin 模块，不用对其进行任何修改，但是需要前往其 `application.yaml` 文件中查看部署后的地址：

```yaml
server:
  port: 38078
spring:
  application:
    name: spring-boot-admin
  boot:
    admin:
      context-path: /spring-boot-admin
  thymeleaf:
    check-template-location: false
```

即 `http://localhost:38078/spring-boot-admin` ，接下来就去整合其他模块，修改模板模块中的 `application.yaml` 文件，更改 enable 配置项：

```yaml
# 公共配置文件
spring:
  # 配置SpringBootAdmin项目所在地址
  boot:
    admin:
      client:
        # todo 是否纳入SpringBootAdmin监控体系（预先关闭）
        enabled: true
        url: http://localhost:38078/spring-boot-admin
```

如果还想将 XXL-JOB 分布式任务调度系统整合进入 SpringBoot Admin 中，那就进行和上面相同的操作即可。

## 申明&联系我

作者能力有限，暂时还不能精通使用本模板中所整合的所有框架，若在使用当中遇到问题或者确定 BUG ，请发布 ISSUES 或者直接提交 PR ，作者定会逐一查看，采纳意见并且做出修改。

如果你也想成为该项目的共建者，请直接提交 PR ，并**对其进行详细说明**，作者审核之后会并入该模板 Master 分支中（暂时性操作，如果有一天项目真能做大，从该项目中规划分支也不是没有可能）。

如果你在使用模板的过程中有建议或者看法，请尽管发布 ISSUES 。
