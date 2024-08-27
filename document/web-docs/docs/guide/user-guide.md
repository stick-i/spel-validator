# 使用指南

::: tip

本组件的目的不是代替 `javax.validation` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。

原则上来说，能够使用 `javax.validation` 处理的场景就不应该使用 `spel-validator` 。

:::

## 开启约束校验

需要满足以下两个条件，才会对带注解的元素进行校验：

1. 在接口参数上使用 `@Valid` 或 `@Validated` 注解
2. 在实体类上使用 `@SpelValid` 注解

如果只满足第一个条件，那么只会对带 `@NotNull`、`@NotEmpty`、`@NotBlank` 等注解的元素进行校验。

如果只满足第二个条件，那么不会对任何元素进行校验。

这是因为 `@SpelValid` 注解是基于 `javax.validation.Constraint` 实现的。
这就意味着，`@SpelValid` 和 `@NotNull`、`@NotEmpty`、`@NotBlank` 等注解一样，
需要在 `@Valid` 或 `@Validated` 注解的支持下才会生效。

而 `spel-validator` 提供的约束注解又是在 `@SpelValid` 的内部进行校验的，只有在 `@SpelValid` 注解生效的情况下才会执行约束校验。

所以，如果需要使用 `spel-validator` 进行校验，需要同时满足上述两个条件。

## 使用约束注解

目前支持的约束注解有：

|       注解        |       说明        | 对标 javax.validation |
|:---------------:|:---------------:|:-------------------:|
|  `@SpelAssert`  |     逻辑断言校验      |    `@AssertTrue`    |
| `@SpelNotNull`  |    非 null 校验    |     `@NotNull`      |
| `@SpelNotEmpty` | 集合、字符串、数组大小非空校验 |     `@NotEmpty`     |
| `@SpelNotBlank` |    字符串非空串校验     |     `@NotBlank`     |
|   `@SpelNull`   |   必须为 null 校验   |       `@Null`       |
|   `@SpelSize`   |  集合、字符串、数组长度校验  |       `@Size`       |
|   `@SpelMin`    |      即将支持       |       `@Min`        |
|   `@SpelMax`    |      即将支持       |       `@Max`        |

所有约束注解都包含三个默认的属性：

- `message`：校验失败时的提示信息。
- `group`：分组条件，支持 SpEL 表达式，当分组条件满足时，才会对带注解的元素进行校验。
- `condition`：约束开启条件，支持 SpEL 表达式，当 **表达式为空** 或 **计算结果为true** 时，才会对带注解的元素进行校验。

## 调用 Spring Bean

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
