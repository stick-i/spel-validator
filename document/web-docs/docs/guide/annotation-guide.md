# 注解完整参考指南

本文档提供了 SpEL Validator 中所有内置注解的完整参考，包括启动注解和约束注解的详细说明。

## 注解总览

**启动注解**

|      注解      |         功能说明         |       使用位置       |
|:------------:|:--------------------:|:----------------:|
| `@SpelValid` | 开启 SpEL 约束注解验证，激活校验器 | 类、字段、方法参数、构造函数参数 |

**约束注解**

|           注解           | 对标 jakarta.validation-api |     功能说明      |
|:----------------------:|:-------------------------:|:-------------:|
|     `@SpelAssert`      |       `@AssertTrue`       |    逻辑断言校验     |
|     `@SpelNotNull`     |        `@NotNull`         |   非 null 校验   |
|      `@SpelNull`       |          `@Null`          |  必须为 null 校验  |
|    `@SpelNotEmpty`     |        `@NotEmpty`        | 集合、字符串、数组非空校验 |
|    `@SpelNotBlank`     |        `@NotBlank`        |   字符串非空串校验    |
|      `@SpelSize`       |          `@Size`          | 集合、字符串、数组长度校验 |
|       `@SpelMin`       |          `@Min`           |    数值最小值校验    |
|       `@SpelMax`       |          `@Max`           |    数值最大值校验    |
|     `@SpelFuture`      |         `@Future`         |   时间必须是未来时间   |
| `@SpelFutureOrPresent` |    `@FutureOrPresent`     | 时间必须是未来或当前时间  |
|      `@SpelPast`       |          `@Past`          |   时间必须是过去时间   |
|  `@SpelPastOrPresent`  |     `@PastOrPresent`      | 时间必须是过去或当前时间  |

::: tip 时间日期类型说明
时间日期类型包括：`Date`、`Calendar`、`Instant`、`LocalDate`、`LocalDateTime`、`LocalTime`、`MonthDay`、`OffsetDateTime`、`OffsetTime`、`Year`、`YearMonth`、
`ZonedDateTime` 以及各种 Chrono 类型。
:::

## 公共属性（重要）

所有约束注解（除 `@SpelValid` 外）都包含以下三个通用属性：

- **`condition`**：约束开启条件，支持 SpEL 表达式，表达式的计算结果必须为 `boolean` 类型。当**计算结果为 true** 时，才会对带注解的元素进行校验，默认情况下开启。
- **`message`**：校验失败时的提示信息，支持多语言 key，使用方式参考 [国际化消息](i18n.md)。
- **`group`**：分组条件，支持 SpEL 表达式，当分组条件满足时，才会对带注解的元素进行校验。具体使用方式参考 [分组校验](user-guide.md#分组校验)。

## @SpelValid

**功能说明**：标记开启 SpEL 约束注解的验证，其功能类似于 `@Valid`，用于激活 SpEL 约束校验。

**使用位置**：类、字段、方法参数、构造函数参数

**属性说明**：

- **`condition`**：开启校验的前置条件，值必须为合法的 SpEL 表达式，计算结果必须为 `boolean` 类型。当表达式为空或计算结果为 true 时，表示开启校验。
- **`spelGroups`**：分组功能，值必须为合法的 SpEL 表达式数组。当分组信息为空时，表示不开启分组校验。
- **`message`**：校验失败时的错误消息（通常不需要设置）
- **`groups`**：标准 Jakarta Validation 分组
- **`payload`**：标准 Jakarta Validation 载荷

**使用示例**：

```java
// 基本用法
@Data
@SpelValid
public class UserVo {

  @SpelNotNull
  private String name;

}

// 条件开启
@Data
@SpelValid(condition = "#this.enableValidation == true")
public class ConditionalVo {

  private Boolean enableValidation;

  @SpelNotNull
  private String content;

}

// 分组校验
@Data
@SpelValid(spelGroups = "#this.type")
public class GroupVo {

  private String type; // "add" 或 "update"

  @SpelNotNull(group = "'update'")
  private Integer id;

  @SpelNotNull(group = {"'add'", "'update'"})
  private String name;

}
```

## @SpelAssert

**功能说明**：被标记的元素需要满足指定的断言条件，用于复杂的自定义校验逻辑。

**支持类型**：任何类型

**特有属性**：

- **`assertTrue`**：断言语句，必须为合法的 SpEL 表达式，计算结果必须为 `boolean` 类型。true 为校验成功，false 为校验失败。默认值为 `"true"`。

**使用示例**：

```java

@Data
@SpelValid
public class AssertExampleVo {

  // 枚举值校验
  @SpelAssert(assertTrue = "T(cn.sticki.enums.StatusEnum).isValid(#this.status)",
      message = "状态值不合法")
  private Integer status;

  // 复杂逻辑校验
  @SpelAssert(assertTrue = "#this.startTime < #this.endTime",
      message = "开始时间必须小于结束时间")
  private Long startTime;

  private Long endTime;

  // 调用 Spring Bean 校验
  @SpelAssert(assertTrue = "@userService.existsById(#this.userId)",
      message = "用户不存在")
  private Long userId;

}
```

## @SpelNotNull

**功能说明**：被标记的元素不能为 `null`。

**支持类型**：任何类型

**使用示例**：

```java

@Data
@SpelValid
public class NotNullExampleVo {

  private Boolean switchAudio;

  // 条件校验：当 switchAudio 为 true 时才校验
  @SpelNotNull(condition = "#this.switchAudio == true",
      message = "语音内容不能为空")
  private Object audioContent;

}
```

## @SpelNull

**功能说明**：被标记的元素必须为 `null`。

**支持类型**：任何类型

## @SpelNotEmpty

**功能说明**：被标记的元素不能为 `null` 或空。

**支持类型**：

- `CharSequence`（评估字符序列的长度）
- `Collection`（评估集合大小）
- `Map`（评估 Map 大小）
- 数组（计算数组长度）

**使用示例**：

```java

@Data
@SpelValid
public class NotEmptyExampleVo {

  private String type;

  @SpelNotEmpty(condition = "#this.type == 'list'",
      message = "列表不能为空")
  private List<String> items;

}
```

## @SpelNotBlank

**功能说明**：被标记的元素不能为 `null`、空字符串或只包含空白字符。

**支持类型**：`CharSequence`

## @SpelSize

**功能说明**：被标记的元素大小必须在指定边界（包含）之间。`null` 元素被认为是有效的。

**支持类型**：

- `CharSequence`（评估字符序列的长度）
- `Collection`（评估集合大小）
- `Map`（评估 Map 大小）
- 数组（计算数组长度）

**特有属性**：

- **`min`**：指定元素最小值，必须为合法的 SpEL 表达式，计算结果必须为数字类型。默认值为 `"0"`。
- **`max`**：指定元素最大值，必须为合法的 SpEL 表达式，计算结果必须为数字类型。默认值为 `"T(Integer).MAX_VALUE"`。

**使用示例**：

```java

@Data
@SpelValid
public class SizeExampleVo {

  // 字符串长度校验
  @SpelSize(min = "2", max = "10", message = "用户名长度必须在2-10之间")
  private String username;

  // 动态大小校验
  @SpelSize(min = "#this.minSize", max = "#this.maxSize")
  private List<String> items;

  private Integer minSize;

  private Integer maxSize;

}
```

## @SpelMin

**功能说明**：被标记的元素值必须大于或等于指定的最小值。`null` 元素被认为是有效的。

**支持类型**：所有 `Number` 类型及它们的基本数据类型

**特有属性**：

- **`value`**：指定元素最小值，必须为合法的 SpEL 表达式，计算结果必须为 `Number` 类型。默认值为 `"0"`。

**使用示例**：

```java

@Data
@SpelValid
public class MinExampleVo {

  @SpelMin(value = "18", message = "年龄不能小于18岁")
  private Integer age;

  // 动态最小值
  @SpelMin(value = "#this.minValue")
  private Double score;

  private Double minValue;

}
```

## @SpelMax

**功能说明**：被标记的元素值必须小于或等于指定的最大值。`null` 元素被认为是有效的。

**支持类型**：所有 `Number` 类型及它们的基本数据类型

**特有属性**：

- **`value`**：指定元素最大值，必须为合法的 SpEL 表达式，计算结果必须为 `Number` 类型。默认值为 `"0"`。

## @SpelFuture

**功能说明**：被标记的元素必须是一个将来的时间。`null` 元素被认为是有效的。

**支持类型**：

- `Date`、`Calendar`
- `Instant`、`LocalDate`、`LocalDateTime`、`LocalTime`
- `MonthDay`、`OffsetDateTime`、`OffsetTime`
- `Year`、`YearMonth`、`ZonedDateTime`
- `HijrahDate`、`JapaneseDate`、`MinguoDate`、`ThaiBuddhistDate`

## @SpelFutureOrPresent

**功能说明**：被标记的元素必须是一个将来或现在的时间。`null` 元素被认为是有效的。

**支持类型**：与 `@SpelFuture` 相同

## @SpelPast

**功能说明**：被标记的元素必须是一个过去的时间。`null` 元素被认为是有效的。

**支持类型**：与 `@SpelFuture` 相同

## @SpelPastOrPresent

**功能说明**：被标记的元素必须是一个过去或现在的时间。`null` 元素被认为是有效的。

**支持类型**：与 `@SpelFuture` 相同

**时间校验使用示例**：

```java

@Data
@SpelValid
public class TimeExampleVo {

  @SpelPast(message = "出生日期必须是过去时间")
  private LocalDate birthDate;

  @SpelFutureOrPresent(message = "预约时间不能是过去时间")
  private LocalDateTime appointmentTime;

  // 条件时间校验
  @SpelFuture(condition = "#this.needFutureCheck == true",
      message = "该时间必须是未来时间")
  private Date eventTime;

  private Boolean needFutureCheck;

}
```
