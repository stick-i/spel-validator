<h1 align="center">SpEL Validator</h1>

<div align="center">

「SpEL Validator」是基于 SpEL 的参数校验包，也是 javax.validation 的扩展增强包，用于简化参数校验。

</div>

## 📚 目录

[它解决了什么问题](#-它解决了什么问题) | [简介](#-简介) |
[快速开始](#-快速开始) | [使用指南](#-使用指南) | [示例项目](#-示例项目) | [常见问题](#-常见问题) |
[更新日志](#-更新日志) | [贡献指南](#-贡献指南) | [捐赠支持](#-捐赠支持) | [联系作者](#-联系作者)

## 💡 它解决了什么问题？

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

## 📝 简介

### 特点

- 强大的参数校验功能，几乎支持所有场景下的参数校验。
- 扩展自 javax.validation 包，只新增不修改，无缝集成到项目中。
- 基于 SpEL（Spring Expression Language） 表达式，支持复杂的校验逻辑。
- 支持调用 Spring Bean，可在表达式中使用注入过的 Spring Bean。
- 校验时基于整个对象，支持对象内字段间的校验逻辑。
- 支持自定义校验注解，可根据业务需求自定义校验逻辑。
- 无需额外的异常处理，校验失败时会上报到 javax.validation 的异常体系中。
- 简单易用，使用方式几乎与 javax.validation 一致，学习成本低，上手快。

### 环境

目前仅测试了 JDK8 环境，理论上来说 JDK8+ 应该都是支持的。

### 交流群

<img src="./document/image/wechat-qrcode.jpg" alt="交流群二维码.jpg" style="width: 25%; height: auto;" />

## 📦 快速开始

- 添加依赖

  Latest Version:
  [![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator.svg)](https://search.maven.org/search?q=g:cn.sticki%20a:spel-validator)
  ```xml
  <dependency>
      <groupId>cn.sticki</groupId>
      <artifactId>spel-validator</artifactId>
      <version>Latest Version</version>
  </dependency>
  
  <dependency>
      <groupId>org.hibernate.validator</groupId>
      <artifactId>hibernate-validator</artifactId>
      <version>${hibernate-validator.version}</version>
  </dependency>
  
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <version>${spring-boot-starter-web.version}</version>
  </dependency>
  ```

- 在接口参数上使用 `@Valid` 或 `@Validated` 注解

  ```java
  @RestController
  @RequestMapping("/example")
  public class ExampleController {
  
    /**
     * 简单校验示例
     */
    @PostMapping("/simple")
    public Resp<Void> simple(@RequestBody @Valid SimpleExampleParamVo simpleExampleParamVo) {
      return Resp.ok(null);
    }

  }
  ```

- 在实体类上使用 `@SpelValid` 注解，同时在需要校验的字段上使用 `@SpelNotNull` 等约束注解

  ```java
  @Data
  @SpelValid
  public class SimpleExampleParamVo {
  
    @NotNull
    private Boolean switchAudio;
  
    /**
     * 当 switchAudio 为 true 时，校验 audioContent，audioContent 不能为null
     */
    @SpelNotNull(condition = "#this.switchAudio == true", message = "语音内容不能为空")
    private Object audioContent;

  }
  ```

- 添加异常处理器，处理校验异常

  ```java
  @RestControllerAdvice
  public class ControllerExceptionAdvice {

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public Resp<Void> handleBindException(BindException ex) {
      String msg = ex.getFieldErrors().stream()
          .map(error -> error.getField() + " " + error.getDefaultMessage())
          .reduce((s1, s2) -> s1 + "," + s2)
          .orElse("");
      return new Resp<>(400, msg);
    }
  
  }
  ```

- 发起请求，即可看到校验结果
  <details>
  <summary>示例一：@SpelNotNull 校验不通过</summary>

  - 请求体：

    ```json
    {
      "switchAudio": true,
      "audioContent": null
    }
    ```

  - 响应体
    ```json
    {
      "code": 400,
      "message": "audioContent 语音内容不能为空",
      "data": null
    }
    ```

  </details>

  <details>
  <summary>示例二：校验通过</summary>

  - 请求体
    ```json
    {
      "switchAudio": false,
      "audioContent": null
    }
    ```

  - 响应体
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": null
    }
    ```

  </details>

  <details>
  <summary>示例三：@NotNull 校验不通过</summary>

  - 请求体
    ```json
    {
      "switchAudio": null,
      "audioContent": null
    }
    ```

  - 响应体
    ```json
    {
      "code": 400,
      "message": "switchAudio 不能为null",
      "data": null
    }
    ```
    </details>

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

## 📦 示例项目

- [spel-validator-example](https://github.com/stick-i/spel-validator-example)

## ❓ 常见问题

### 关于性能

性能上我目前还没有进行测试，但代码里使用了很多的反射，会有一定的损耗，后面我准备多加一些缓存，尽量降低性能上的影响。

## 📅 更新日志

https://github.com/stick-i/spel-validator/releases

## 🤝 贡献指南

暂时没写，如果想贡献代码，可以在 issue 中提出，我会尽快回复。

## 💰 捐赠支持

| 微信赞赏                                                 | 支付宝赞赏                                            |
|------------------------------------------------------|--------------------------------------------------|
| ![微信](./document/image/wechat-appreciation-code.jpg) | ![支付宝](./document/image/alipay-receipt-code.jpg) |

## 📧 联系作者

- Email: sticki@126.com
- 微信: sticki6
- 公众号: 程序员阿杆
