# 使用指南

::: tip

本组件的目的不是代替 `javax.validation` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。

原则上来说，能够使用 `javax.validation` 处理的场景就不应该使用 `spel-validator` 。

:::

## 开启约束校验

需要满足以下两个条件，才会对带注解的元素进行校验：

1. 在接口参数上使用 `@Valid` 或 `@Validated` 注解
2. 在实体类上使用 `@SpelValid` 注解

```java
@RestController
@RequestMapping("/example")
public class ExampleController {

  @PostMapping("/simple")
  public Resp<Void> simple(@RequestBody @Valid /*添加启动注解*/ SimpleExampleParamVo simpleExampleParamVo) {
    return Resp.ok(null);
  }

}

@Data
@SpelValid /*添加启动注解*/
public class SimpleExampleParamVo {
  // ...
}
```

如果只满足第一个条件，那么只会对带 `@NotNull`、`@NotEmpty`、`@NotBlank` 等注解的元素进行校验。

如果只满足第二个条件，那么不会对任何元素进行校验。

这是因为 `@SpelValid` 注解是基于 `javax.validation.Constraint` 实现的。
这就意味着，`@SpelValid` 和 `@NotNull`、`@NotEmpty`、`@NotBlank` 等注解一样，
需要在 `@Valid` 或 `@Validated` 注解的支持下才会生效。

而 `SpEL Validator` 提供的约束注解又是在 `@SpelValid` 的内部进行校验的，只有在 `@SpelValid` 注解生效的情况下才会执行约束校验。

所以，如果需要使用 `SpEL Validator` 进行校验，需要同时满足上述两个条件。

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

在需要校验的字段上使用 `@SpelNotNull` 等约束注解。

```java
@Data
@SpelValid
public class SimpleExampleParamVo {

  /**
   * 此处使用了 @SpelNotNull 注解
   * 当参数 condition 的计算结果 true 时，会启用对当前字段的约束，要求为当前字段不能为null
   * 约束校验失败时，提示信息为：语音内容不能为空
   */
  @SpelNotNull(condition = "true", message = "语音内容不能为空")
  private Object audioContent;

}
```

## 引用上下文字段

设计这套组件的初衷，就是为了满足一些需要判断另一个字段的值来决定当前字段是否校验的场景。

在组件内部，将当前校验的整个类对象作为了 SpEL 表达式解析过程中的根对象，所以在表达式中可以直接引用类中的任意字段。

通过 `#this.fieldName` 的方式来引用当前类对象的字段。

```java
@Data
@SpelValid
public class SimpleExampleParamVo {

  private boolean switchAudio;

  /**
   * 此处引用了上面的 switchAudio 字段
   * 当 switchAudio 字段的值为 true 时，才会校验 audioContent 是否为null
   */
  @SpelNotNull(condition = "#this.switchAudio == true")
  private Object audioContent;

}
```

## 分组校验

待补充

## 嵌套校验

本组件支持嵌套校验，只需要在需要校验的字段上添加 `@Valid`，以及在另一个类上添加 `@SpelValid` 注解。

```java
@Data
@SpelValid
public class TestParamVo {

  private Boolean switchVoice;

  @SpelNotNull(condition = "#this.switchVoice == true")
  private Object voiceContent;

  @Valid
  private TestParamVo2 testParamVo2;

}

@Data
@SpelValid /*在此处添加注解*/
public class TestParamVo2 {

  @SpelNotNull
  private Object object;

}
```

或者将 `@SpelValid` 注解转移到对应的字段上。

```java
@Data
@SpelValid
public class TestParamVo {

  private Boolean switchVoice;

  @SpelNotNull(condition = "#this.switchVoice == true")
  private Object voiceContent;

  @Valid
  @SpelValid /*在此处添加注解*/
  private TestParamVo2 testParamVo2;

}

@Data
public class TestParamVo2 {

  @SpelNotNull
  private Object object;

}
```

## 调用静态方法

利用 SpEL 的特性，可以调用静态方法进行校验。

调用静态方法的格式为：`T(全类名).方法名(参数)`。

如果你的 IDEA 版本比较新，那么不出意外的话，IDEA 能够识别到表达式，并且会给出提示，也具备引用的功能。

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

默认情况下，解析器无法识别 SpEL 表达式中的 Spring Bean。

如果需要在 SpEL 表达式中调用 Spring Bean，需要在任意一个被 Spring 托管的类上添加 `@EnableSpelValidatorBeanRegistrar` 注解，
开启 Spring Bean 支持。

```java
@EnableSpelValidatorBeanRegistrar /*添加注解*/
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
```

开启 Spring Bean 支持后，即可在 SpEL 表达式中调用 Spring Bean。

> 表达式中的 Bean 名称需要以 `@` 开头

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
