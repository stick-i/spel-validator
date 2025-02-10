# 快速开始

:::tip
本章仅介绍如何快速上手 SpEL Validator 的基本使用，更详细的使用说明请参考 [使用指南](user-guide.md)。
:::

## 添加依赖

Latest Version:
[![Maven Central](https://img.shields.io/maven-central/v/cn.sticki/spel-validator-root.svg)](https://central.sonatype.com/search?q=g:cn.sticki%20a:spel-validator-root)

### SpringBoot 2.x

```xml
  <dependency>
    <groupId>cn.sticki</groupId>
    <artifactId>spel-validator-javax</artifactId>
    <version>0.4.1-beta</version>
  </dependency>
```

### SpringBoot 3.x

```xml
  <dependency>
    <groupId>cn.sticki</groupId>
    <artifactId>spel-validator-jakarta</artifactId>
    <version>0.4.1-beta</version>
  </dependency>
```

## 添加启动注解

在接口参数上对需要进行校验的类使用 `@Valid` 或 `@Validated` 注解

```java
@RestController
@RequestMapping("/example")
public class ExampleController {

  /**
   * 简单校验示例，这里使用了 @Valid 注解
   */
  @PostMapping("/simple")
  public Resp<Void> simple(@RequestBody @Valid SimpleExampleParamVo simpleExampleParamVo) {
    return Resp.ok(null);
  }

}
```

## 添加SpEL约束注解

在实体类上使用 `@SpelValid` 注解，表示开启校验，同时在需要校验的字段上使用 `@SpelNotNull` 等约束注解

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

## 处理异常

添加全局异常处理器，处理校验不通过的异常信息

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

## 请求示例

发起请求，即可看到校验结果

示例一：@SpelNotNull 校验不通过

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

示例二：校验通过

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

示例三：@NotNull 校验不通过

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

