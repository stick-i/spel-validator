package cn.sticki.spel.validator.constrain;

import cn.sticki.spel.validator.constraintvalidator.SpelNullValidator;
import cn.sticki.spel.validator.core.SpelConstraint;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 被标记的元素必须为 {@code null}，支持任何类型。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(SpelNull.List.class)
@SpelConstraint(validatedBy = SpelNullValidator.class)
public @interface SpelNull {

    /**
     * 校验失败时的错误消息
     */
    String message() default "{cn.sticki.spel.validator.constraint.Null.message}";

    /**
     * 约束开启条件，必须为合法的SpEL表达式，计算结果必须为boolean类型。
     * <p>
     * 当 表达式为空 或 计算结果为true 时，会对带注解的元素进行校验。
     * <p>
     * 默认情况下，开启校验。
     */
    @Language("SpEL")
    String condition() default "";

    /**
     * 分组条件，必须为合法的SpEL表达式。
     * <p>
     * 当分组信息不为空时，只有当 {@link SpelValid#spelGroups()} 中的分组信息与此处的分组信息有交集时，才会对带注解的元素进行校验。
     * <p>
     * 其计算结果可以是任何类型，但只有两个计算结果完全相等时，才被认为是相等的。
     */
    @Language("SpEL")
    String[] group() default {};

    @Documented
    @Target(FIELD)
    @Retention(RUNTIME)
    @interface List {

        SpelNull[] value();

    }

}
