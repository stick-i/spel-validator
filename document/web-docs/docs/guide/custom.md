# 自定义约束注解

::: tip
如果你使用过 `jakarta.validation-api` 的自定义约束注解，那么你会发现 `SpEL Validator` 的自定义约束注解几乎与 `jakarta.validation-api` 一致。
:::

下面以 `@SpelNotBlank` 为例，展示如何实现自定义约束注解。

## 创建约束注解类

每个约束注释必须包含以下属性:
- `String message() default "";` 用于指定约束校验失败时的错误消息。
- `String condition() default "";` 用于指定约束开启条件的SpEL表达式。
- `String[] group() default {};` 用于指定分组条件的SpEL表达式。

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(SpelNotBlank.List.class)
public @interface SpelNotBlank {

  String message() default "不能为空字符串";

  String condition() default "";

  String[] group() default {};

  @Target(FIELD)
  @Retention(RUNTIME)
  @Documented
  @interface List {

    SpelNotBlank[] value();

  }

}
```

## 创建约束验证器

创建类 `SpelNotBlankValidator`，实现 `SpelConstraintValidator<T>` 接口，其中泛型 `T` 为要校验的约束注解类，在这里是 `SpelNotBlank`。

实现 `isValid` 方法，校验逻辑在该方法中实现。

`isValid` 方法的参数说明如下：
- `annotation`：当前约束注解的实例。
- `obj`：当前校验的根对象。
- `field`：当前校验的字段。

```java
public class SpelNotBlankValidator implements SpelConstraintValidator<SpelNotBlank> {

  @Override
  public FieldValidResult isValid(SpelNotBlank annotation, Object obj, Field field) throws IllegalAccessException {
    CharSequence fieldValue = (CharSequence) field.get(obj);
    return FieldValidResult.of(StringUtils.hasText(fieldValue));
  }

}
```

一般情况下，只需要校验当前字段的值，通过 `field.get(obj)` 即可获取。

有些约束注解可能仅支持特定类型的字段，可以通过重写 `supportType()` 方法来指定支持的类型。默认情况下，支持所有类型。

```java
public class SpelNotBlankValidator implements SpelConstraintValidator<SpelNotBlank> {

  @Override
  public FieldValidResult isValid(SpelNotBlank annotation, Object obj, Field field) throws IllegalAccessException {
    CharSequence fieldValue = (CharSequence) field.get(obj);
    return FieldValidResult.of(StringUtils.hasText(fieldValue));
  }

  private static final Set<Class<?>> SUPPORT_TYPE = Collections.singleton(CharSequence.class);

  @Override
  public Set<Class<?>> supportType() {
    return SUPPORT_TYPE;
  }

}
```

## 关联注解和验证器

在 `SpelNotBlank` 注解上添加 `@SpelConstraint` 注解，指定该注解的验证器为 `SpelNotBlankValidator`。

```java
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(SpelNotBlank.List.class)
@SpelConstraint(validatedBy = SpelNotBlankValidator.class) // 关联验证器
public @interface SpelNotBlank {
  // ...
}
```

## 使用自定义约束注解

完成上面的步骤，就可以在需要校验的字段上使用 `@SpelNotBlank` 注解了，使用方法就和内置的约束注解一样，[使用指南](user-guide.md)。

已经大功告成了，这里我就不举例了。
