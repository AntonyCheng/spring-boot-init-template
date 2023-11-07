# SpringBoot初始化模板

> 作者：[AntonyCheng](https://github.com/AntonyCheng})
> 基于：[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html) 开源协议

基于 Java Web 项目 SpringBoot 框架初始化模板，整合了常用的框架，保证大家在此基础上能够快速开发自己的项目，该模板针对于后端启动开发小而精，本项目会由作者持续更新。

* [SpringBoot初始化模板](#springboot初始化模板)
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
      * [整合ElasticSearch](#整合elasticsearch)
      * [整合对象存储服务](#整合对象存储服务)
        * [整合腾讯云COS](#整合腾讯云cos)
        * [整合MinIO](#整合minio)
        * [整合阿里云OSS](#整合阿里云oss)
      * [配置SaToken](#配置satoken)
      * [配置XXL-JOB](#配置xxl-job)
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
   ......
   ```

   > 在这个文件中还能看到很多其他的配置，如有需要，请开发者自行学习 ShardingSphere 框架，理解相关配置；

3. 直到这一步之后，模板就已经可以直接启动了，访问 `http://localhost:38080/api/doc.html#/home` 即可打开接口文档。

### 可选执行

> 为了方便开发者快速找到配置文件需要修改的位置，一律使用 todo 代办进行标识。

#### 整合缓存服务

**说明**：该项目中存在两种 Redis 服务，第一种是系统缓存服务（**对应整合Redis**），第二种是业务缓存服务（**对应整合Redisson**）。前者承担系统框架本身的缓存服务，例如用户分布式登陆信息的缓存；后者承担开发者业务逻辑所需的缓存操作，例如分布式锁、限流工具等。

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

===> 未完待续

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

===> 未完待续

#### 配置XXL-JOB

===> 未完待续

#### 配置SpringBootAdmin

===> 未完待续

## 申明&联系我

作者能力有限，暂时还不能精通使用本模板中所整合的所有框架，若在使用当中遇到问题或者确定BUG，请发布ISSUES或者直接提交PR，作者定会逐一查看，采纳意见并且做出修改。

如果你也想成为该项目的共建者，请直接提交PR，并**对其进行详细说明**，作者审核之后会并入该模板Master分支中（暂时性操作，如果有一天项目真能做大，从该项目中规划分支也不是没有可能）。

如果你在使用模板的过程中有建议或者看法，请尽管发布ISSUES。



