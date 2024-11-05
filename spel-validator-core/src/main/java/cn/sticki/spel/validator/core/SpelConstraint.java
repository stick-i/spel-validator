package cn.sticki.spel.validator.core;

import java.lang.annotation.*;

/**
 * 将注解标记为由 {@link cn.sticki.spel.validator.javax.SpelValid} 进行校验的Bean验证约束。
 * <p>
 * 该注解的属性 {@link SpelConstraint#validatedBy()} 用于指定校验器的实现类，实现类需要实现 {@link SpelConstraintValidator} 接口。
 * <p>
 * 每个约束注释必须包含以下属性:
 * <ul>
 *     <li>{@code String message() default [...];} 用于指定约束校验失败时的错误消息。
 *     </li>
 *     <li>{@code String condition() default "";} 用于指定约束开启条件的SpEL表达式。
 *     当 表达式为空 或 计算结果为true 时，才会对带注解的元素进行校验。
 *     </li>
 *     <li>{@code String[] group() default {};} 用于指定约束开启的分组条件，必须为合法的SpEL表达式。
 *     当分组信息不为空时，只有当 {@link cn.sticki.spel.validator.javax.SpelValid#spelGroups()} 中的分组信息与此处的分组信息有交集时，才会对带注解的元素进行校验。
 *     其计算结果可以是任何类型，但只有两个计算结果完全相等时，才被认为是相等的。
 *     </li>
 * </ul>
 * <p>
 * 这里有一些定义约束的例子，可以参考：
 * <ul>
 *     <li>{@link cn.sticki.spel.validator.constrain.SpelAssert}</li>
 *     <li>{@link cn.sticki.spel.validator.constrain.SpelNotNull}</li>
 * </ul>
 *
 * @author 阿杆
 * @version 1.0
 * @see cn.sticki.spel.validator.javax.SpelValid
 * @see SpelConstraintValidator
 * @since 2024/4/11
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpelConstraint {

    /**
     * 校验器的实现类，用于校验被标记的注解。
     */
    Class<? extends SpelConstraintValidator<?>> validatedBy();

}
