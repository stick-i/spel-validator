package cn.sticki.spel.validator.constrain;

import cn.sticki.spel.validator.constraintvalidator.SpelFutureOrPresentValidator;
import cn.sticki.spel.validator.core.SpelConstraint;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 被标记的元素必须是一个将来或现在的时间。{@code null} 元素被认为是有效的。
 * <p>
 * 支持的类型有：
 * <ul>
 *     <li>{@link java.time.Instant}</li>
 *     <li>{@link java.time.LocalDate}</li>
 *     <li>{@link java.time.LocalDateTime}</li>
 *     <li>{@link java.time.LocalTime}</li>
 *     <li>{@link java.time.MonthDay}</li>
 *     <li>{@link java.time.OffsetDateTime}</li>
 *     <li>{@link java.time.OffsetTime}</li>
 *     <li>{@link java.time.Year}</li>
 *     <li>{@link java.time.YearMonth}</li>
 *     <li>{@link java.time.ZonedDateTime}</li>
 *     <li>{@link java.time.chrono.HijrahDate}</li>
 *     <li>{@link java.time.chrono.JapaneseDate}</li>
 *     <li>{@link java.time.chrono.MinguoDate}</li>
 *     <li>{@link java.time.chrono.ThaiBuddhistDate}</li>
 *     <li>{@link java.util.Date}</li>
 *     <li>{@link java.util.Calendar}</li>
 * </ul>
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(SpelFutureOrPresent.List.class)
@SpelConstraint(validatedBy = SpelFutureOrPresentValidator.class)
public @interface SpelFutureOrPresent {

    /**
     * 校验失败时的错误消息
     */
    String message() default "{cn.sticki.spel.validator.constraint.FutureOrPresent.message}";

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

    /**
     * 在同一元素上定义多个注解。
     *
     * @see SpelFutureOrPresent
     */
    @Target(FIELD)
    @Retention(RUNTIME)
    @Documented
    @interface List {

        SpelFutureOrPresent[] value();

    }

}
