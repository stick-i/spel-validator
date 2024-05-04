<h1 align="center">SpEL Validator</h1>

<div align="center">

「SpEL Validator」是基于 SpEL 的参数校验包，也是 javax.validation 的扩展增强包，用于简化参数校验。

</div>

## 📚 目录

- [快速开始](#-快速开始)
- [使用指南](
- [示例项目](
- [常见问题](
- [更新日志](
- [贡献指南](
- [许可证](
- [捐赠支持](
- [联系作者](
- [关于我们](

## 📦 快速开始

- 添加依赖
  - Latest
    Version: [![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator.svg)](https://search.maven.org/search?q=g:cn.sticki%20a:spel-*)
  - Maven:
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

  - 示例一：

    请求体
    ```json
    {
      "switchAudio": true,
      "audioContent": null
    }
    ```

    响应体
    ```json
    {
      "code": 400,
      "message": "audioContent 语音内容不能为空",
      "data": null
    }
    ```

  - 示例二：

    请求体
    ```json
    {
      "switchAudio": false,
      "audioContent": null
    }
    ```

    响应体
    ```json
    {
      "code": 200,
      "message": "成功",
      "data": null
    }
    ```
  - 示例三：

    请求体
    ```json
    {
      "switchAudio": null,
      "audioContent": null
    }
    ```

    响应体
    ```json
    {
      "code": 400,
      "message": "switchAudio 不能为null",
      "data": null
    }
    ```
    