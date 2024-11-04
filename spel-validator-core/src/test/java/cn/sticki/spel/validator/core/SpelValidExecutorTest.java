package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.constraint.SpelNotNullTest;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * SpelValidExecutor 测试
 *
 * @author 阿杆
 * @since 2024/11/4
 */
public class SpelValidExecutorTest {

    @Documented
    @Retention(RUNTIME)
    @Target({FIELD, TYPE})
    @SpelConstraint(validatedBy = TestValidator.class)
    @interface TestAnno1 {
    }

    @Documented
    @Retention(RUNTIME)
    @Target({FIELD, TYPE})
    @SpelConstraint(validatedBy = TestValidator.class)
    @interface TestAnno2 {

        String message() default "必须小于或等于 {value}";

    }

    @Documented
    @Retention(RUNTIME)
    @Target({FIELD, TYPE})
    @SpelConstraint(validatedBy = TestValidator.class)
    @interface TestAnno3 {

        String message() default "必须小于或等于 {value}";

        String condition() default "";

    }

    static class TestValidator implements SpelConstraintValidator<SpelNotNullTest> {

        @Override
        public FieldValidResult isValid(SpelNotNullTest annotation, Object obj, Field field) throws IllegalAccessException {
            return null;
        }

    }

    static class TestClass1 {

        @TestAnno1
        private String name;

    }

    static class TestClass2 {

        @TestAnno2
        private String name;

    }

    static class TestClass3 {

        @TestAnno3
        private String name;

    }

    @Test
    void test() {
        Assertions.assertTrue(SpelValidExecutor.validateObject(new TestClass1()).noneError());
        Assertions.assertTrue(SpelValidExecutor.validateObject(new TestClass2()).noneError());
        Assertions.assertTrue(SpelValidExecutor.validateObject(new TestClass3()).noneError());
    }

}

