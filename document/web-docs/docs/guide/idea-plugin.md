# IDEA 插件支持

[SpEL Validator Support](https://github.com/stick-i/spel-validator-support) 是一个为 SpEL Validator 框架提供智能开发支持的 IntelliJ IDEA 插件。

## 安装方式

### 方式一：从 JetBrains Marketplace 安装（推荐）

1. 打开 IntelliJ IDEA
2. 进入 **Settings/Preferences → Plugins**
3. 搜索 **"SpEL Validator Support"**
4. 点击 **Install**
5. 重启 IDEA

### 方式二：手动安装

1. 从 [Releases](https://github.com/stick-i/spel-validator-support/releases) 下载最新的插件 ZIP 文件
2. 打开 IntelliJ IDEA
3. 进入 **Settings/Preferences → Plugins**
4. 点击齿轮图标 → **Install Plugin from Disk**
5. 选择下载的 ZIP 文件
6. 重启 IDEA

## 主要功能

### 1. SpEL 语言注入

自动识别 SpEL Validator 注解并注入 SpEL 语言支持：

- 语法高亮显示
- SpEL 表达式基础补全（如 `@beanName`、`T(ClassName)`）

::: tip
需要安装 Spring 插件以获得完整的 SpEL 支持，IDEA 默认已启用该插件。
:::

### 2. 智能字段补全

在 SpEL 表达式中输入 `#this.` 时自动补全：

- 当前类的所有字段（包括私有字段）
- 父类继承的字段
- 嵌套字段访问（如 `#this.user.address.city`）
- 显示字段类型信息

### 3. 字段引用导航

- **Ctrl+Click 跳转**：点击字段名跳转到字段定义
- **Find Usages**：查找字段在 SpEL 表达式中的所有使用
- 支持嵌套字段的导航

### 4. 重构支持

- 字段重命名时自动更新 SpEL 表达式中的引用
- 支持重命名预览
- 支持嵌套字段的重命名

### 5. 错误检查

- 实时检查字段引用的有效性
- 对不存在的字段显示警告
- 鼠标悬停显示错误消息
- 支持嵌套字段的错误检查

## 支持的注解

插件支持以下 SpEL Validator 内置注解，同时支持使用 `@SpelConstraint` 元注解标注的自定义约束注解。

## 系统要求

- IntelliJ IDEA 2023.2 或更高版本
- 需要启用 Spring 插件以获得完整的 SpEL 语言支持（默认已启用）

## 使用示例

安装插件后，在使用 SpEL Validator 注解时，插件会自动提供智能支持：

```java
@Data
public class UserDto {
    private String userName;
    private Integer age;
    private Address address;
    
    // 输入 #this. 时会自动补全 userName、age、address
    @SpelNotNull(condition = "#this.age != null")
    private String userNameCheck;
    
    // 支持嵌套字段补全：#this.address. 会补全 Address 类的字段
    @SpelAssert(assertTrue = "#this.address.city != null")
    private String addressCheck;
}

@Data
public class Address {
    private String city;
    private String street;
}
```

## 自定义约束注解

如果你创建了自定义的约束注解，需要使用 `@SpelConstraint` 元注解标注，并且使用 `@Language("SpEL")` 标注对应的方法，插件才能识别：

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@SpelConstraint  // 添加此注解
public @interface MyCustomConstraint {
    @Language("SpEL")  // 标注需要 SpEL 支持的属性
    String condition() default "";
    
    String message() default "validation failed";
}
```

## 常见问题

### Q: 为什么没有 SpEL 语法高亮？

请确保已安装 Spring 插件。SpEL 语言支持由 Spring 插件提供。

### Q: 为什么字段补全不工作？

请检查：
- 注解是否为 SpEL Validator 的约束注解
- 光标是否在 `#this.` 之后
- 项目是否正确配置了 SpEL Validator 依赖

## 相关链接

- [插件 GitHub 仓库](https://github.com/stick-i/spel-validator-support)
- [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/29693-spel-validator-support)
