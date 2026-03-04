# SpEL Validator 代码审查报告与版本路线图

> 审查日期：2026-03-02
>
> 当前版本：0.6.2-beta

---

## 一、现有代码问题总结

### 1.1 严重问题

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| S1 | `StandardEvaluationContext` 线程不安全 | `SpelParser.java:34` | 静态共享的 `StandardEvaluationContext` 在并发场景下可能产生数据竞争，Spring 官方文档明确不建议跨线程共享。`parse()` 方法无同步保护，`bindBeanResolver()` 仅对写入加锁 |
| S2 | `ObjectValidResult.EMPTY` 可变单例 | `ObjectValidResult.java:18` | `EMPTY` 为 public 可变对象，任何代码误调用 `addFieldResult()` / `addFieldError()` 都会污染全局状态。当前主路径 `SpelValidExecutor.validateObject()` 未使用 `EMPTY`，属于潜在风险 |
| S3 | `AnnotationMethodManager` null 缓存失效 | `AnnotationMethodManager.java:30-36` | `ConcurrentHashMap.computeIfAbsent` 的 mappingFunction 返回 null 时不会缓存该键，对不存在的方法每次都重新反射并触发异常捕获 |
| S4 | `NumberComparatorUtil` API 设计不一致 | `NumberComparatorUtil.java` | `FINITE_VALUE`（`OptionalInt.empty()`）作为 public 常量与 `LESS_THAN`、`GREATER_THAN` 并列暴露，暗示可作为 `treatNanAs` 参数使用，但 `compare()` 方法的参数校验会直接拒绝它。实际上 `FINITE_VALUE` 仅设计为 `infinityCheck()` 的内部返回值 |

### 1.2 中等问题

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| M1 | 容器注解靠字符串后缀 `"$List"` / `"Container"` 启发式判断 | `SpelValidExecutor.java:166` | 不够健壮，应改用 `@Repeatable` 元注解检测 |
| M2 | `ResourceBundleMessageResolver.addBasenames()` 复合操作非原子 | `ResourceBundleMessageResolver.java:55-65` | `addBasenames()` 先读 `getBasenameSet()` 再写 `setBasenames()`，读-改-写操作无同步保护 |
| M3 | `spring.factories` 注册非 `@Configuration` 类做"预热" | `META-INF/spring.factories` | 利用类加载触发 static 块，非标准用法 |
| M4 | SpEL 表达式求值为 null 时错误提示不友好 | `SpelValidator.java:37`、`SpelValidExecutor.java:221` | 当 condition 表达式求值为 null 时，`SpelParser.parse()` 抛出通用的 `SpelParserException`（非 NPE），错误信息不够明确，用户难以定位问题 |
| M5 | jakarta/javax 模块存在差异不一致 | `SpelValid.java` | javax 版 `message()` 上多了 `@NotNull` 注解；javax 版 `@Target` 缺少 `TYPE` |
| M6 | javax 模块缺少 `ParentClassTestBean` 和 `I18nTestBean` 测试 | javax test | jakarta 有 7 个测试文件，javax 仅 5 个 |
| M7 | `ConstraintViolationSet` 集合修改依赖实现细节 | `ConstraintViolationSet.java:52-56` | 增强 for 循环中直接 `list.remove()` 后立即 `return`，虽因不再调用 `iterator.next()` 而不会抛 `ConcurrentModificationException`，但属于不良实践。该类位于 test 模块，不影响生产代码 |
| M8 | `SUPPORT_TYPE` 在 `SpelDigitsValidator` 和 `AbstractSpelNumberCompareValidator` 中重复定义 | constrain 模块 | 两处定义完全一致（`Number.class` + 6 种基本类型 + `CharSequence.class`） |
| M9 | `supperType` 拼写错误 | `SpelNotSupportedTypeException.java:19` | 字段名和异常消息均拼写错误，且为 public 字段（有 `@Getter`），修改属于破坏性变更 |

---

## 二、版本路线图

### v0.7.0 — 稳定性与健壮性

**目标**：修复所有严重问题和关键中等问题，发布第一个稳定版。

#### 2.1.1 修复 `SpelParser` 线程安全问题 [S1]

- 每次 `parse()` 调用创建新的 `StandardEvaluationContext`，共享 BeanResolver 引用
- 注意：共享 BeanResolver 引用时需确保其线程安全读取

#### 2.1.2 修复 `ObjectValidResult.EMPTY` [S2]

- 改为 `empty()` 工厂方法每次返回新实例

#### 2.1.3 修复 `AnnotationMethodManager` null 缓存 [S3]

- 使用 `Optional<Method>` 哨兵值缓存"不存在"的结果

#### 2.1.4 修复 `NumberComparatorUtil` API 设计 [S4]

- 将 `OptionalInt` 三态改为语义清晰的 `enum { LESS_THAN, FINITE_VALUE, GREATER_THAN }`
- 明确区分"可作为参数传入"的值和"仅用于内部返回"的值

#### 2.1.5 优化表达式求值错误提示 [M4]

- `SpelParser.parse()` 在 condition 表达式求值为 null 时，提供更明确的错误消息（如包含字段名和表达式内容）
- 说明：经代码核查，当前实现已在 `SpelParser.parse(expr, obj, requiredType)` 中做了 null 检查并抛出 `SpelParserException`（非 NPE），`SpelSizeValidator` 的 min/max 同理不存在自动拆箱 NPE 风险

#### 2.1.6 修复 `ResourceBundleMessageResolver` 并发问题 [M2]

- 仅对写方法（`addBasenames()`、`resetBasenames()`）加 `synchronized` 互斥锁，保证读-改-写操作的原子性
- `getMessage()` 为热路径，不加锁，依赖 Spring `ResourceBundleMessageSource` 内部的线程安全保护

#### 2.1.7 统一 jakarta/javax 差异 [M5][M6]

- 移除 javax `SpelValid.message()` 上不恰当的 `@NotNull`
- 统一 `@Target` 中的 `TYPE`
- 补齐 javax 测试用例（`ParentClassTestBean`、`I18nTestBean`）

#### 2.1.8 其他修复

- 修复拼写错误 `supperType` → `supportedType` [M9]（注意：public 字段变更属于破坏性变更，需在 changelog 中说明）
- 修复 `ConstraintViolationSet` 集合操作 [M7]（改用迭代器 `remove()` 或索引遍历）

---

### v0.8.0 — 架构优化

**目标**：减少技术债务，提升可扩展性。

#### 2.2.1 jakarta/javax 代码生成（从 v1.0.0 提前）

- 使用 Maven 插件（如 `maven-replacer-plugin`）从模板生成两个模块代码
- 彻底消除手动同步维护的负担

#### 2.2.2 缓存治理（从 v1.0.0 提前）

- 为 `FIELD_CACHE`、`FIELD_ANNOTATION_CACHE`、`expressionCache` 添加弱引用或大小上限
- 防止热部署/OSGi 环境下的 ClassLoader 泄漏

#### 2.2.3 引入实例化架构

当前全静态架构导致：无法 mock 测试、全局状态共享、无法多配置共存。

- 提供 `SpelValidatorFactory` 实例类，持有 parser/executor/message resolver
- 保留静态方法作为便捷入口（委托给默认实例），保持向后兼容

#### 2.2.4 改进容器注解判断 [M1]

- 替换字符串后缀启发式判断为 `@Repeatable` 元注解检测

#### 2.2.5 改进自动配置机制 [M3]

- 创建专门的 `SpelValidatorAutoConfiguration` 类替代当前的"预热 hack"
- 在 `@PostConstruct` 中做初始化，而非靠类加载副作用
- 注意同时支持 Spring Boot 2.x（`spring.factories`）和 3.x（`AutoConfiguration.imports`）

#### 2.2.6 校验器 Spring Bean 集成

- `ValidatorInstanceManager` 优先从 Spring 容器获取校验器实例
- 使校验器可以注入 Spring Bean（如需访问数据库、调用远程服务等）

#### 2.2.7 消息插值增强

- 支持混合消息模板如 `"字段 {fieldName} 不能为空"`
- 当前只支持整体 key 如 `"{cn.sticki.xxx.message}"`

#### 2.2.8 提取 `SUPPORT_TYPE` 常量 [M8]

- 将 `SpelDigitsValidator` 和 `AbstractSpelNumberCompareValidator` 中重复的 `SUPPORT_TYPE` 提取到共享位置

---

### v0.9.0 — 功能扩展

**目标**：补全约束类型，增强开发体验。

#### 2.3.1 新增约束注解（对标 JSR 380 完整集）

| 注解 | 功能 | 实现难度 |
|------|------|---------|
| `SpelPattern` | SpEL 动态正则校验 | 中（差异化亮点） |
| `SpelPositive` / `SpelPositiveOrZero` | 正数/非负数 | 低（复用 `AbstractSpelNumberCompareValidator`） |
| `SpelNegative` / `SpelNegativeOrZero` | 负数/非正数 | 低 |
| `SpelEmail` | 邮箱格式 | 低 |
| `SpelAssertFalse` | 断言为 false | 低 |

#### 2.3.2 编译期注解处理器

- 为 `@SpelConstraint` 标记的自定义注解提供编译期检查
- 确保必须包含 `message()`、`condition()`、`group()` 方法
- 将运行时错误前移到编译期

---

### v1.0.0 — 正式发布

**目标**：API 稳定化，补全文档和测试。

#### 2.4.1 API 稳定性审查

- 明确 public API 与 internal API 的边界（考虑使用 `@API` 注解标记）
- `parseGroups` 等目前 public 但面向内部的方法降低可见性

#### 2.4.2 测试覆盖率提升

- 并发安全测试
- `spelGroups` 分组功能测试
- 非法 SpEL 表达式的错误处理测试
- 边界值测试（NaN、Infinity、极大数字、空白字符串等）
- 引入参数化测试替代手动 `List<VerifyObject>` 管理

#### 2.4.3 文档完善

- 校验器线程安全和无状态的要求写入正式文档
- 自定义约束开发指南
- 性能调优指南

---

### v1.x+ — 远期方向

| 方向 | 说明 |
|------|------|
| 脱离 Spring 依赖的轻量模式 | 提供不依赖 Spring 的纯 Java 校验能力 |
| 响应式支持 | 适配 Spring WebFlux 的校验场景 |
| 校验链组合 | 支持多个约束之间的 AND/OR 逻辑组合 |
| 异步校验 | 对于耗时的校验逻辑（如查数据库唯一性）提供异步支持 |
| GraalVM Native Image 兼容 | 确保反射配置在 native 编译下可用 |
