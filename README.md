# SpEL Validator

[![Coverage Status](https://coveralls.io/repos/github/stick-i/spel-validator/badge.svg?branch=main)](https://coveralls.io/github/stick-i/spel-validator?branch=main)
[![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator.svg)](https://central.sonatype.com/search?q=g:cn.sticki%20a:spel-validator)
[![license](https://img.shields.io/github/license/stick-i/spel-validator)](https://github.com/stick-i/spel-validator/blob/main/LICENSE)

一个强大的 Java 参数校验包，基于 SpEL 实现，扩展自 javax.validation 包，用于简化参数校验，几乎支持所有场景下的参数校验。

## 项目地址

- GitHub：https://github.com/stick-i/spel-validator
- Gitee：https://gitee.com/sticki/spel-validator
- 在线文档：https://spel-validator.sticki.cn/

## 特点

- 简单易用，使用方式几乎与 javax.validation 一致，学习成本低，上手快。
- 强大的参数校验功能，几乎支持所有场景下的参数校验。
- 扩展自 javax.validation 包，只新增不修改，无缝集成到项目中。
- 基于 SpEL（Spring Expression Language） 表达式，支持复杂的校验逻辑。
- 支持调用 Spring Bean，可在表达式中使用注入过的 Spring Bean。
- 校验时基于整个对象，支持对象内字段间的校验逻辑。
- 支持自定义校验注解，可根据业务需求自定义校验逻辑。
- 无需额外的异常处理，校验失败时会上报到 javax.validation 的异常体系中。

## 支持的环境

JDK8+

## 交流群

请添加微信号 `sticki6`，备注 `SpEL`，我拉你入群。

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

## 📦 快速开始

- 添加依赖

  Latest Version:
  [![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator.svg)](https://central.sonatype.com/search?q=g:cn.sticki%20a:spel-validator)
  ```xml
  <dependency>
      <groupId>cn.sticki</groupId>
      <artifactId>spel-validator</artifactId>
      <version>Latest Version</version>
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
  @SpelValid // 添加启动注解
  public class SimpleExampleParamVo {
  
    @NotNull
    private Boolean switchAudio;
  
    /**
     * 此处开启了注解校验
     * 当 switchAudio 字段为 true 时，校验 audioContent，audioContent 不能为null
     */
    @SpelNotNull(condition = "#this.switchAudio == true", message = "语音内容不能为空")
    private Object audioContent;
  
  }
  ```

- 添加全局异常处理器，处理校验异常

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

## 📦 示例项目

- [spel-validator-example](https://github.com/stick-i/spel-validator-example)

## 🤝 贡献指南

非常欢迎您来参与项目贡献，但希望您可以先在 issue 中提出您的想法，我们可以一起讨论，然后再进行代码编写。

### 分支说明

- `main`：主分支，已发布的最新版本代码将合入此分支。
- `vX.Y.Z`：版本分支，用于汇总及验证新版本的功能，已经发布的版本会将分支删除。
- `dev-num-desc`：开发分支，用于开发新功能，每个dev分支都应当对应一个issue，功能开发完成后通过PR合入 `vX.Y.Z` 分支，并删除当前分支。
- `docs`：文档分支，修改文档的内容将提交到这里。

## License

[Apache-2.0](https://github.com/stick-i/spel-validator/blob/main/LICENSE)

## 📧 联系作者

- Email: sticki@126.com
- 微信: sticki6
- 公众号: 程序员阿杆

## 💰 捐赠支持

| 微信赞赏                                                 | 支付宝赞赏                                            |
|------------------------------------------------------|--------------------------------------------------|
| ![微信](./document/image/wechat-appreciation-code.jpg) | ![支付宝](./document/image/alipay-receipt-code.jpg) |
