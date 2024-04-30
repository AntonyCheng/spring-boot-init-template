### 这里是关于注解的AOP示例代码。

#### 建议代码阅读顺序如列表所示：

- annotation.atAnnotationAop ==> Pointcut("@annotation(...)")
- annotation.atArgsAop ==>       Pointcut("@args(...)")
- annotation.atTargetAop ==>     Pointcut("@target(...)")
- annotation.atWithinAop ==>     Pointcut("@within(...)")

##### 其中annotation.atAnnotationAop是最常用，最灵活，同时也是最基础的AOP切面表达式。

##### 以上示例表达式之间可以使用 `||` 、`&&` 以及 `!:` 进行逻辑上的与、或、非。