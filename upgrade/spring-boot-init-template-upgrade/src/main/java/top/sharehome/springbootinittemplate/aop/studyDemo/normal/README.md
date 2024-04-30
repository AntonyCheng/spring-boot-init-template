### 这里是关于普通接口或者类的AOP示例代码。

#### 建议代码阅读顺序如列表所示：

- normal.executionAop ==> Pointcut("execution(...)")
- normal.withinAop ==>    Pointcut("within(...)")
- normal.argsAop ==>      Pointcut("args(...)")
- normal.targetAop ==>    Pointcut("target(...)")
- normal.thisAop ==>      Pointcut("this(...)")
- normal.beanAop ==>      Pointcut("bean(...)")

##### 其中normal.executionAop是最常用，最灵活，同时也是最基础的AOP切面表达式，其他的就只是匹配连接点方式的不同而已。

##### 以上示例表达式之间可以使用 `||` 、`&&` 以及 `!:` 进行逻辑上的与、或、非。