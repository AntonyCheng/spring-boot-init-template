# SpringBoot初始化模板

> 作者：[AntonyCheng](https://github.com/AntonyCheng})
> 基于：[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 开源协议

基于 Java Web 项目 SpringBoot 框架初始化模板，整合了常用的框架，保证大家在此基础上能够快速开发自己的项目，该模板针对于后端启动开发小而精，本项目会由作者持续更新。

## 模板特点

### 主流框架

- **Java 11**

- **SpringBoot 2.7.x**
  - freemarker == 模板引擎
  - web == SpringMVC
  - undertow == Java Web 服务器
  - aop == 面向切面编程
  - devtools == 热部署插件
- **MySQL 8.0.33**
  - druid 1.2.16 == 连接池
  - mybatis-plus == MySQL 持久层操作框架
  - JDBC 8.0.33 == Java 连接 MySQL 依赖
  - ShardingSphere 5.3.2 == 分布式数据库解决方案
- **工具类**
  - Lombok 1.18.30
  - hutool 5.8.22
  - commons-lang3
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
  - easy-es-boot-starter 2.0.0-bata4 == 简化ElasticSearch搜索引擎操作的开源框架
- **对象存储（OSS）**
  - 腾讯云 COS 5.6.155
  - MinIO 8.5.6
- **文件操作**
  - POI == 操作 Word
  - EasyExcel == 操作 Excel
  - itext 7.2.5
- **外接平台**
  - XXL-JOB 2.4.0 == 分布式定时任务管理平台
  - SpringBootAdmin 2.7.4 == 监控平台

### 业务特性

- 使用 Undertow 服务器替换掉 Tomcat 服务器
- SaToken 分布式可配置登陆&认证&鉴权
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

- 提供模板SQL示例文件（业务数据库&XXL-JOB数据库）
- 用户登陆、注册、注销、信息获取
- Spring Scheduler 单机版定时任务示例

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

## 快速上手

> 拉取项目模板之后需要确保所有依赖下载完成，以下的操作都是针对于application.yaml文件。

### 必须执行

1. 执行 `sql/init_db.sql` 和 `sql/init_xxl_job.sql` 文件；

2. 修改 `src/main/resources/sharding.yaml` 文件：

   ```yaml
   dataSources:
     master:
       dataSourceClassName: com.alibaba.druid.pool.DruidDataSource
       driverClassName: com.mysql.cj.jdbc.Driver
       # 修改url地址
       url: jdbc:mysql://xxx.xxx.xxx.xxx:23305/init_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true
       username: root
       password: 123456
   ```

   > 在这个文件中还能看到很多其他的配置，如有需要，请开发者自行学习 ShardingSphere 框架，理解相关配置；

3. 直到这一步之后，模板就已经可以直接启动了，访问 `http://localhost:38080/api/doc.html#/home` 即可打开接口文档。

### 可选执行

> 为了方便开发者快速找到配置文件需要修改的位置，一律使用 todo 代办进行标识。

#### 整合缓存服务

**说明**：该模板中存在两种 Redis 服务，第一种是系统缓存服务（**对应整合Redis**），第二种是业务缓存服务（**对应整合Redisson**）。前者承担系统框架本身的缓存服务，例如用户分布式登陆信息的缓存；后者承担开发者业务逻辑所需的缓存操作，例如分布式锁、限流工具等。

##### 整合Redis（系统缓存）

系统缓存服务主要为一些依赖spring-boot-starter-data-redis原生操作的框架而设计，例如模板中用于校验权限的 SaToken 框架就有借用 Redis 进行分布式登陆或校验的需求，系统缓存的过程对开发者能做到透明。

1. 取消排除 `RedisAutoConfiguration`  依赖：

   ```yaml
   spring:
     autoconfigure:
       exclude:
         # todo 是否开启Redis依赖类（如果要打开Redis配置，就将RedisAutoConfiguration注释掉，该配置类一旦被注释，就需要设置redis相关配置，redisson相关配置也需要依赖这个类，预先关闭）
         #- org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
   ```

2. 修改Redis相关配置，切记注意单机模式和集群模式无法共存，默认开启单机模式，注释掉集群模式相关代码，同时默认没有密码，所以密码也被注释掉：

   ```yaml
   spring: 
     # 修改系统缓存redis配置（这里的redis配置主要用于鉴权认证等模板自带服务的系统缓存服务）
     redis:
       # 单机地址（单价模式配置和集群模式配置只能存在一个）
       host: redis IP
       # 单机端口，默认为6379
       port: redis 端口
       # 集群地址（单价模式配置和集群模式配置只能存在一个）
       #cluster:
       #  nodes:
       #    - xxx.xxx.xxx.xxx:16379
       #    - xxx.xxx.xxx.xxx:16380
       #    - xxx.xxx.xxx.xxx:16381
       #    - xxx.xxx.xxx.xxx:16382
       #    - xxx.xxx.xxx.xxx:16383
       #    - xxx.xxx.xxx.xxx:16384
       # 数据库索引
       database: 0
       # 密码（考虑是否需要密码）
       #password: 123456
       # 连接超时时间
       timeout: 3s
       # redis连接池
       lettuce:
         pool:
           min-idle: 8
           max-idle: 16
           max-active: 32
   ```

3. 此时项目就能够直接启动，Redis相关配置就完成了，特别说明一下，为了适应模板的通用性，该模板中依旧保留了spring-boot-starter-data-redis中RedisTemplate的原生操作途径，在`config/redis` 包中设计了 RedisTemplate 的 Bean，同时更新了其序列化方式以防止存入Redis之后出现乱码，这意味着开发者依旧可以使用 RedisTemplate 的方式将系统缓存和业务缓存合二为一，这种保留仅仅是为了可拓展性，所以没有围绕 RedisTemplate 编写缓存工具类，如果需要使用缓存工具类，详情见**整合Redisson**；

##### 整合Redisson（业务缓存）

业务缓存服务主要是为了满足开发者在编码过程中的缓存需求，例如接口限流、分布式锁等。

1. 首先完成**整合Redis**步骤，两者整合的Redis数据源可以不相同，如果相同，建议使用不同的数据库进行存储两种不同缓存的存储；

2. 修改Redisson配置，此时单机版本和集群版本的启动状态可以自定义：

   - 都不开启（都为false）：系统不会将 Redisson 相关依赖纳入发转控制容器中；
   - 仅开启一个；
   - 都开启（都为true）：系统只会参考单机版本的Redisson配置；

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
       address: redis://xxx.xxx.xxx.xxx:xxxx
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
         - redis://xxx.xxx.xxx.xxx:xxxx
         - redis://xxx.xxx.xxx.xxx:xxxx
         - redis://xxx.xxx.xxx.xxx:xxxx
         - redis://xxx.xxx.xxx.xxx:xxxx
         - redis://xxx.xxx.xxx.xxx:xxxx
         - redis://xxx.xxx.xxx.xxx:xxxx
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

3. 此时项目就能够直接启动，Redisson相关配置就完成了，模板为了降低开发者的模板使用门槛，特意针对Redisson进行进一步封装，在 `utils/redisson` 包中设计了缓存工具类 CacheUtils 和限流工具类 RateLimitUtils 供开发者使用，使用参考示例单元测试类。

#### 整合消息队列

**说明**：该模板中存在一套较为全面的 RabbitMQ 消息队列解决方案，具有消费者手动 ACK 示例、死信队列、延迟队列等特性，开发者不仅可以直接使用模板中存在的默认队列，还可以根据自己的需求对原有的解决方案进行拓展以设计出满足自身项目特色的消息队列，同时该模板对拓展的消息队列具有一定规则性兼容。

##### 激活消息队列

1. 启动消息队列相关配置，同时选择配置单机 RabbitMQ 或者集群 RabbitMQ ，切记这两者无法共存，使用其中一个配置的同时需要把另一个配置给注释或者删除掉（不建议删除），然后根据自己搭建的 RabbitMQ 进行相关配置：

   ```yaml
   spring: 
     # 修改rabbitmq配置
     rabbitmq:
       # todo 是否开启RabbitMQ（预先关闭）
       enable: true
       # 单机RabbitMQ IP（单价模式配置和集群模式配置只能存在一个）
       host: xxx.xxx.xxx.xxx
       # 单机RabbitMQ端口
       port: 25671
       # 集群RabbitMQ（单价模式配置和集群模式配置只能存在一个）
       #addresses: xxx.xxx.xxx.xxx:25672,xxx.xxx.xxx.xxx:25673,xxx.xxx.xxx.xxx:25674
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

2. 配置好之后即可启动项目，在模板中存在默认的三个消息队列配置，它们在 `config/rabbitmq/default_mq` 包下，分别是 `DefaultRabbitMq` （默认的普通队列）、`DefaultRabbitMqWithDlx` （默认的带有死信队列的消息队列）、`DefaultRabbitMqWithDelay` （默认的延迟队列），这三个队列可以直接使用，在 `utils/rabbitmq/RabbitMqUtils` 工具类中设计由“default”开头的方法，当然开发者也可以通过一定规则自定义消息队列，这些消息队列会与 RabbitMqUtils工具类兼容。

##### 自定义消息队列

1. 关于自定义消息队列，这里给出最简单的方式，首先开发者需要考虑自定义哪种形式的消息队列：

   - 普通消息队列：即最基础的消息队列，能够支持最基本的消息投递和消息消费的能力，简单易懂；
   - 带有死信队列的消息队列：与普通消息队列相比，带有死信队列的消息队列能够在消息被拒绝消费之后选择将消息列入死信队列中，而并不是直接丢弃或者再次扔进队列中等待消费，这种的应用场景多用于不可丢失消息的处理或者对拒绝消费的消息再处理；
   - 延迟队列：投递消息之后并不能从队列中马上取出消息进行消费。

   这些具体的消息队列特性往往需要开发者掌握一定的前置知识基础；

2. 然后粘贴 `config/default_mq/DefaultRabbitMq` 类，复制到 `config/customize_mq` 包中，并且重命名（在 DefaultRabbitMq 的文档注释中有具体说明），注意不要和 DefaultRabbitMq 类重复，不然会造成反转控制冲突，这里假设命名成 TestRabbitMq；

3. 在 TestRabbitMq 中进行文本替换，将所有“default”替换成“test”即可，如果想要使用自定义的消息队列配置，直接使用 RabbitMqUtils 工具类中形参带有“Class”的方法即可（详情见 RabbitMqUtils 文档注释）。

注意：这里只给出了最简单的自定义方式，开发者在理解这种“替换”变换和阅读相关源码的基础上可以对模板进行更加自定义的改造。

#### 整合ElasticSearch

===> 未完待续

#### 整合对象存储服务

##### 整合腾讯云COS

===> 未完待续

##### 整合MinIO

===> 未完待续

##### 整合阿里云OSS

===> 未完待续

#### 配置SaToken

##### 开启鉴权

===> 未完待续

##### 开启认证

===> 未完待续

##### 开启JWT

===> 未完待续

#### 配置XXL-JOB

===> 未完待续

#### 配置SpringBootAdmin

===> 未完待续

## 申明&联系我

作者能力有限，暂时还不能精通使用本模板中所整合的所有框架，若在使用当中遇到问题或者确定BUG，请发布ISSUES或者直接提交PR，作者定会逐一查看，采纳意见并且做出修改。

如果你也想成为该项目的共建者，请直接提交PR，并**对其进行详细说明**，作者审核之后会并入该模板Master分支中（暂时性操作，如果有一天项目真能做大，从该项目中规划分支也不是没有可能）。

如果你在使用模板的过程中有建议或者看法，请尽管发布ISSUES。



