# 介绍

SpEL Validator 是基于 Spring Expression Language 的参数校验包，也是 jakarta.validation-api 的扩展增强包，用于简化参数校验，它几乎支持所有场景下的参数校验。

设计的初衷是为了解决一些需要判断另一个字段的值来决定当前字段是否校验的场景。

::: tip

本组件的目的不是代替 `jakarta.validation-api` 的校验注解，而是作为一个扩展，方便某些场景下的参数校验。

:::

## 它是如何工作的？

简单来说，它按照以下步骤工作：

```md
@Valid/@Validated
   ↓
@SpelValid                <- 激活 SpelValidator
   ↓
SpelValidator#isValid()   <- 调用 SpelValidExecutor
   ↓
SpelValidExecutor         <- 扫描字段、解析表达式、调用具体约束的校验器
   ↓
SpelConstraintValidator   <- 执行约束校验
   ↓
FieldError                <- 错误信息收集
   ↓
ConstraintViolation       <- 转换为标准校验框架支持的结构
```

详细的说明，可以参考章节 [工作原理](principle.md)。

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
