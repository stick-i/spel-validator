# 工作原理

## 一、调用入口来自 `@Valid` / `@Validated`

这是 Java 的标准约束校验机制：只要方法参数上使用了 `@Valid` 或 `@Validated`，Spring 或 Jakarta Validation 会自动识别参数上有哪些字段或类需要校验。

```java

@PostMapping("/submit")
public Resp<Void> doSomething(@RequestBody @Valid MyParam param) {
    ...
}
```

## 二、识别到类上标注了 `@SpelValid`

这是 **SpEL Validator 的启动注解**，本质上是个 `@Constraint` 注解，会被绑定到一个实现了 `ConstraintValidator<SpelValid, Object>` 接口的类：

```java
@Constraint(validatedBy = SpelValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface SpelValid {
  ...
}
```

所以当 Validator 框架（如 `hibernate-validator`）发现你的类上有 `@SpelValid` 时，就会调用绑定的校验器：`SpelValidator`。

## 三、SpelValidator 实际工作流程

在 `SpelValidator` 的逻辑中：

### 1. 校验条件成立才执行（支持 condition）

```java
if(!spelValid.condition().isEmpty() &&SpelParser.parse(spelValid.condition()))return true;
```

- 支持通过 `@SpelValid(condition = "...")` 控制是否触发校验逻辑
- 允许动态场景下跳过校验，例如某些参数为空时跳过

### 2. 构建上下文并调用执行器 `SpelValidExecutor`

```java
ObjectValidResult result = SpelValidExecutor.validateObject(obj, spelGroups, context);
```

- `spelGroups` 指定了这次校验使用哪些分组
- `context` 中包含语言环境等信息，便于做国际化

## 四、SpelValidExecutor 启动字段校验流程

执行器 `SpelValidExecutor` 是 SpEL Validator 的核心引擎，它会：

1. 找出被校验类的所有字段
2. 找出字段上有哪些 `@SpelConstraint` 标注的注解（如 `@SpelSize`, `@SpelNotNull`, `@SpelFuture` 等）
3. 根据分组和 `condition` 决定是否执行注解
4. 执行注解对应的 `SpelConstraintValidator`
5. 收集错误结果，支持国际化 + 参数插值

每一个字段的错误，都会包装成 `FieldError`，包含了一些必要的信息。

## 五、回传错误信息给上层框架

```java
context.buildConstraintViolationWithTemplate(error.getErrorMessage())
    .addPropertyNode(error.getFieldName())
    .addConstraintViolation();
```

这一步是把内部的校验错误结果，转换为标准的 Jakarta Validation 机制可感知的 ConstraintViolation：

- 自动在响应中显示字段名、错误信息
- 可以统一封装为 `400 Bad Request`、`ErrorResult` 等结构返回前端

这样可以与 BindingResult 等常见的 Spring 错误处理机制无缝集成。

## 六、整体执行流程如下

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

# 小结

SpEL Validator 是一个基于 Jakarta Validation 扩展的表达式校验框架，其核心特性包括：

- 支持基于 SpEL 表达式的灵活字段校验
- 支持注解级 condition 控制
- 支持分组校验与国际化提示
- 与 Spring MVC 原生校验机制无缝集成

使用时必须注意：

| 场景                         | 是否会触发 SpEL 校验   |
|----------------------------|-----------------|
| 只有 `@Valid` / `@Validated` | ❌ 不会触发 SpEL 逻辑  |
| 只有 `@SpelValid`            | ❌ 不会被 Spring 调用 |
| 同时加了 `@Valid + @SpelValid` | ✅ 生效            |

