# 更新日志

## v0.6.2-beta (2026-02-09)

### 🐛 问题修复

- 修复 `ChronoLocalDateTime` / `ChronoZonedDateTime` 类型不支持的问题
- 优化 `SpelParser` 初始化时序下的 BeanResolver 绑定行为，降低并行测试下的时序互踩风险

### ✅ 测试与稳定性

- 新增/补充以下回归测试：
  - `SpelParserInitTimingTest`
  - `AbstractSpelTemporalValidatorExceptionTest`（补充 Chrono 时间语义回归用例）
- `SpelParserInitTimingTest` 增加隔离执行注解，避免并发干扰


## v0.6.1-beta (2025-12-19)

### 🎉 新增功能

- 支持在 Kotlin 类上使用 `@SpelValid` 注解，并补充了相关测试用例

### 🔧 工程调整

- 升级发布相关构建插件，完善版本发布流程


## v0.6.0-beta (2025-08-13)

### 🎉 新增功能

- **新增 `@SpelDigits` 注解**：用于校验数字的整数部分和小数部分位数
  - 支持所有 `Number` 类型及它们的基本数据类型
  - 支持使用 `CharSequence` 表示的数字
  - 提供 `integer` 和 `fraction` 参数分别控制整数和小数部分的最大位数

### ✨ 功能增强

- **`@SpelMin` 和 `@SpelMax` 支持 `CharSequence` 类型**：现在可以对字符串形式的数字进行范围校验
- **`@SpelMin` 和 `@SpelMax` 新增 `inclusive` 参数**：
  - 支持配置边界值是否包含在内
  - `inclusive = true`（默认）：`@SpelMin` 验证 `value >= min`，`@SpelMax` 验证 `value <= max`
  - `inclusive = false`：`@SpelMin` 验证 `value > min`，`@SpelMax` 验证 `value < max`

> 本次增强 `@SpelMin` 和 `@SpelMax` 的目的，是为了对标 `@DecimalMax`、`@DecimalMin` 两个注解，参考 https://github.com/stick-i/spel-validator/issues/65

### 💥 破坏性变更

- **`@SpelMin` 和 `@SpelMax` 的 `value` 参数改为必填**：
  - 移除了默认值，提高使用的明确性
  - 升级时需要为所有使用这两个注解的地方显式指定 `value` 值

### ⚠️ 升级注意事项

如果你从旧版本升级到 0.6.0-beta，请注意：

1. **必须为 `@SpelMin` 和 `@SpelMax` 指定 `value` 值**：
   ```java
   // 旧版本（不再支持）
   @SpelMin
   private Integer count;
   
   // 新版本（必须指定 value）
   @SpelMin(value = "0")
   private Integer count;
   ```

---

## 历史版本

更多历史版本信息请查看 [GitHub Releases](https://github.com/stick-i/spel-validator/releases)。
