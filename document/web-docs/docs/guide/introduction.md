# 介绍

SpEL Validator 是基于 Spring Expression Language 的参数校验包，也是 javax.validation 的扩展增强包，用于简化参数校验，几乎支持所有场景下的参数校验。

::: tip

本组件的目的不是代替 `javax.validation` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。

原则上来说，能够使用 `javax.validation` 处理的场景就不要使用 `spel-validator` 。

:::

## 它是如何工作的？

Test

## 它解决了什么问题？

- 枚举值字段校验：
  ```java
  @SpelAssert(assertTrue = " T(cn.sticki.enums.UserStatusEnum).getByCode(#this.userStatus) != null ", message = "用户状态不合法")
  private Integer userStatus;
  ```

- 多字段联合校验：
  ```java
  @NotNull
  private Integer contentType;
  
  @SpelNotNull(condition = "#this.contentType == 1", message = "语音内容不能为空")
  private Object audioContent;
  
  @SpelNotNull(condition = "#this.contentType == 2", message = "视频内容不能为空")
  private Object videoContent;
  ```

- 复杂逻辑校验，调用静态方法：
  ```java
  // 中文算两个字符，英文算一个字符，要求总长度不超过 10
  // 调用外部静态方法进行校验
  @SpelAssert(assertTrue = "T(cn.sticki.util.StringUtil).getLength(#this.userName) <= 10", message = "用户名长度不能超过10")
  private String userName;
  ```

- 调用 Spring Bean（需要使用 @EnableSpelValidatorBeanRegistrar 开启Spring Bean支持）：
  ```java
  // 这里只是简单举例，实际开发中不建议这样判断用户是否存在
  @SpelAssert(assertTrue = "@userService.getById(#this.userId) != null", message = "用户不存在")
  private Long userId;
  ```

- 更多使用场景，欢迎探索和补充！

## 📖 使用指南

> 注意：本组件的目的不是代替 `javax.validation` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。
> 能够使用 `javax.validation` 的场景就不要使用 `spel-validator` ，因为 `spel-validator` 会有一定的性能损耗。

### 开启约束校验

需要满足以下两个条件，才会对带注解的元素进行校验：

1. 在接口参数上使用 `@Valid` 或 `@Validated` 注解
2. 在实体类上使用 `@SpelValid` 注解

如果只满足第一个条件，那么只会对带 `@NotNull`、`@NotEmpty`、`@NotBlank` 等注解的元素进行校验。

如果只满足第二个条件，那么不会对任何元素进行校验。

这是因为 `@SpelValid` 注解是基于 `javax.validation.Constraint` 实现的，只有在 `@Valid` 或 `@Validated` 注解的支持下才会生效。
而 `spel-validator` 提供的约束注解是基于 `@SpelValid` 进行扫描校验的，只有在 `@SpelValid` 注解生效的情况下才会执行约束校验。

### 使用约束注解

目前支持的约束注解有：

|       注解        |       说明        | 对标 javax.validation |
|:---------------:|:---------------:|:-------------------:|
|  `@SpelAssert`  |     逻辑断言校验      |          无          |
| `@SpelNotNull`  |    非 null 校验    |     `@NotNull`      |
| `@SpelNotEmpty` | 集合、字符串、数组大小非空校验 |     `@NotEmpty`     |
| `@SpelNotBlank` |    字符串非空串校验     |     `@NotBlank`     |
|   `@SpelNull`   |   必须为 null 校验   |       `@Null`       |
|   `@SpelSize`   |  集合、字符串、数组长度校验  |       `@Size`       |

每个约束注解都包含三个默认的属性：

- `message`：校验失败时的提示信息。
- `group`：分组条件，支持 SpEL 表达式，当分组条件满足时，才会对带注解的元素进行校验。
- `condition`：约束开启条件，支持 SpEL 表达式，当 表达式为空 或 计算结果为true 时，才会对带注解的元素进行校验。

### 调用 Spring Bean

默认情况下，解析器无法识别 SpEL 表达式中的 Spring Bean。

如果需要在 SpEL 表达式中调用 Spring Bean，需要在启动类上添加 `@EnableSpelValidatorBeanRegistrar` 注解，
开启 Spring Bean 支持。

```java

@EnableSpelValidatorBeanRegistrar
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
```

### 自定义约束注解

参考 `cn.sticki.validator.spel.SpelConstraint` 类，实现自定义约束注解。

如果你使用过 `javax.validation` 的自定义约束注解，那么你会发现 `SpEL Validator` 的自定义约束注解几乎与 `javax.validation`
一致。
