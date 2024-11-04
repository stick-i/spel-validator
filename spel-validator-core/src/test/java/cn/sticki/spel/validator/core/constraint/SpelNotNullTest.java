package cn.sticki.spel.validator.core.constraint;

import cn.sticki.spel.validator.core.SpelConstraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 注解测试类
 *
 * @author 阿杆
 * @since 2024/11/4
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, TYPE})
@SpelConstraint(validatedBy = SpelNotNullValidator.class)
public @interface SpelNotNullTest {
}
