package cn.sticki.spel.validator.jakarta;

import jakarta.validation.Constraint;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 标记开启spel约束注解的验证，其功能类似于 {@link jakarta.validation.Valid}，用于开启 {@link cn.sticki.spel.validator.core.constrain} 包下定义的spel约束。
 * <p>
 * 注意：该注解需要配合 {@link jakarta.validation.Valid} 或 {@link org.springframework.validation.annotation.Validated} 注解一起使用。
 * <p>
 * 这种行为是非递归应用的，只对当前标记对象的属性生效，不会对其属性的下层属性进行验证。
 * <p>
 * 以下是一个简单的例子：
 * <pre>{@code
 *   @PostMapping("/ test")
 *   public void test(@RequestBody @Valid TestParamVo testParamVo) {
 *      ...
 *   }
 *
 *   @Data
 *   @SpelValid
 *   public class TestParamVo {
 *
 *       private Boolean switchVoice;
 *
 *       @SpelNotNull(condition = "#this. switchVoice == true")
 *       private Object voiceContent;
 *
 *       @Valid
 *       @SpelValid
 *       private TestParamVo2 testParamVo2;
 *
 *   }
 *
 *   @Data
 *   public class TestParamVo2 {
 *
 *      @SpelNotNull(condition = "true")
 *      private Object object;
 *
 *   }
 *
 * }</pre>
 * <p>
 * 在上面的例子中，{@code TestParamVo} 和 {@code TestParamVo2} 都成功开启了spel校验。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/11
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Constraint(validatedBy = {SpelValidator.class})
public @interface SpelValid {

    /**
     * 开启校验的前置条件，值必须为合法的 spel 表达式
     * <p>
     * 当 表达式为空 或 计算结果为true 时，表示开启校验
     */
    @Language("SpEL")
    String condition() default "";

    /**
     * 分组功能，值必须为合法的 spel 表达式
     * <p>
     * 当分组信息为空时，表示不开启分组校验
     */
    @Language("SpEL")
    String[] spelGroups() default {};

    String message() default "";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
