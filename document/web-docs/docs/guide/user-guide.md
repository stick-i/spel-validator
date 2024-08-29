# 使用指南

::: warning

本组件的目的不是代替 `javax.validation` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。

原则上来说，能够使用 `javax.validation` 处理的场景就不应该使用 `spel-validator` 。

:::

## 添加依赖

Latest Version:
[![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator.svg)](https://central.sonatype.com/search?q=g:cn.sticki%20a:spel-validator)

大多少情况下，你只需要添加以下两个依赖：

```xml
<dependencys>
  <dependency>
    <groupId>cn.sticki</groupId>
    <artifactId>spel-validator</artifactId>
    <version>Latest Version</version>
  </dependency>

  <!-- 基于 javax.validation 标准的校验器实现类 -->
  <dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.2.5.Final</version>
  </dependency>
</dependencys>
```

如果你的项目中没有任何对于Spring的依赖，那么你需要额外添加一个对SpEL的依赖：

```xml
<dependencys>
  <!-- SpEL 依赖项 -->
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-expression</artifactId>
    <version>${spring.version}</version>
  </dependency>
</dependencys>
```

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

### 设置开启条件

`@SpelValid` 注解包含一个属性 `condition`，支持 SpEL 表达式。

当 **表达式的算结果为true** 时，表示开启校验，默认情况下开启校验。

```java
@Data
@SpelValid(condition = "1 > 0") /*设置开启校验的条件*/
public class SimpleExampleParamVo {
  // ...
}
```

这里的 `condition` 字段同样支持上下文引用，具体使用方式参考 [引用上下文字段](#引用上下文字段)。


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

## 处理异常

待补充

当校验失败时，本组件会将异常信息上报到 `javax.validation` 的异常体系中。

所以正常情况下，你无需添加额外的异常处理逻辑。

```java
```

## 开启对 Spring Bean 的支持

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

关于使用方法，请参考 [调用 Spring Bean](spel.md#调用-spring-bean)。