package cn.sticki.validator.spel.parse;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用SpelValidatorBeanRegistrar，启用后可以在 spel-validator 的相关注解中引用 Spring Bean。
 * <p>
 * 例如：
 * <pre>
 * &#064;SpelAssert(assertTrue = "@userService.getUserById(#this.userId) != null")
 * private Integer userId;
 * </pre>
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/4
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpelValidatorBeanRegistrar.class)
public @interface EnableSpelValidatorBeanRegistrar {
}
