package cn.sticki.spel.validator.jakarta;

import cn.sticki.spel.validator.jakarta.bean.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 约束类测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
public class ConstrainTest {

    /**
     * 这是一个测试示例
     */
    @Test
    void testExample() {
        boolean verified = JakartaSpelValidator.check(ExampleTestBean.testCase());
        Assertions.assertTrue(verified);

        boolean innerTest = JakartaSpelValidator.check(ExampleTestBean.innerTestCase());
        Assertions.assertTrue(innerTest);
    }

    @Test
    void testSpelValid() {
        boolean verified = JakartaSpelValidator.check(SpelValidTestBean.paramTestCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testParentClass() {
        boolean verified = JakartaSpelValidator.check(ParentClassTestBean.paramTestCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testI18n() {
        boolean testUs = JakartaSpelValidator.check(I18nTestBean.testUs());
        Assertions.assertTrue(testUs, "I18nTestBean.testUs() failed");

        boolean testZh = JakartaSpelValidator.check(I18nTestBean.testZh());
        Assertions.assertTrue(testZh, "I18nTestBean.testZh() failed");
    }

}
