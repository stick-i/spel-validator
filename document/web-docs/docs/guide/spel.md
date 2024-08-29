# SpEL 表达式

本章只介绍一些重要的 SpEL 表达式用法，更详细的使用说明请参考官方文档。

官方文档：[Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/reference/core/expressions.html)

::: tip

如果你的 IDEA 版本比较新，那么不出意外的话，IDEA 能够识别到表达式，并且会给出提示，也具备引用的功能。

:::

## 调用静态方法

调用静态方法的语法为：`T(全类名).方法名(参数)`。

```java
@Data
@SpelValid
public class SimpleExampleParamVo {

  /**
   * 枚举值字段校验
   */
  @SpelAssert(assertTrue = " T(cn.sticki.enums.UserStatusEnum).getByCode(#this.userStatus) != null ", message = "用户状态不合法")
  private Integer userStatus;

  // 中文算两个字符，英文算一个字符，要求总长度不超过 10
  // 调用外部静态方法进行校验
  @SpelAssert(assertTrue = "T(cn.sticki.util.StringUtil).getLength(#this.userName) <= 10", message = "用户名长度不能超过10")
  private String userName;

}
```

## 调用 Spring Bean

[开启 Spring Bean 支持](user-guide.md#开启对-spring-bean-的支持)后，即可在 SpEL 表达式中调用 Spring Bean。

调用 Bean 的语法为：`@beanName.methodName(参数)`。

```java
@Data
@SpelValid
public class SimpleExampleParamVo {

  /**
   * 调用 userService 的 getById 方法，判断用户是否存在
   * 校验失败时，提示信息为：用户不存在
   * 这里只是简单举例，实际开发中不建议这样判断用户是否存在
   */
  @SpelAssert(assertTrue = "@userService.getById(#this.userId) != null", message = "用户不存在")
  private Long userId;

}
```
