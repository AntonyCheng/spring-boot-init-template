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

> 拉取项目模板之后需要确保所有依赖下载完成

### 必须执行

1. 执行 `sql/init_db.sql` 和 `sql/init_xxl_job.sql` 

2. 修改 `src/main/resources/sharding.yaml` 文件

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

##### 整合Redis

系统缓存服务主要为一些依赖spring-boot-starter-data-redis原生操作的框架

##### 整合Redisson

===> 未完待续

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
