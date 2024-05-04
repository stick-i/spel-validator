package cn.sticki.validator.spel.constrain;

import cn.sticki.validator.spel.SpelConstraint;
import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.constraintvalidator.SpelAssertValidator;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 基于SpEL断言校验注解，用于标记被注解的元素需要满足指定的断言条件。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
@Repeatable(SpelAssert.List.class)
@SpelConstraint(validatedBy = SpelAssertValidator.class)
public @interface SpelAssert {

	/**
	 * 校验失败时的错误消息
	 */
	String message() default "不能为null";

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
	 * 断言语句，必须为合法的SpEL表达式。
	 * <p>
	 * 计算结果必须为boolean类型，true为校验成功，false为校验失败
	 */
	@Language("SpEL")
	String assertTrue() default "";

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	@interface List {

		SpelAssert[] value();

	}

}