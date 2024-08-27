# 自定义约束注解

如果你使用过 `javax.validation` 的自定义约束注解，那么你会发现 `SpEL Validator` 的自定义约束注解几乎与 `javax.validation`
一致。

下面以 `@SpelNotNull` 为例，展示如何实现自定义约束注解。

**以下内容还没写完**

## 创建约束注解类

```java
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SpelNotNullValidator.class)
public @interface SpelNotNull {

  String message() default "{javax.validation.constraints.NotNull.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String condition() default "";

}
```

## 创建约束验证器

```java
public class SpelNotNullValidator implements ConstraintValidator<SpelNotNull, Object> {

  private String condition;

  @Override
  public void initialize(SpelNotNull constraintAnnotation) {
    this.condition = constraintAnnotation.condition();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return false;
    }
    if (StringUtils.hasText(condition)) {
      return SpelUtils.evaluate(condition, value, Boolean.class);
    }
    return true;
  }

}
```
