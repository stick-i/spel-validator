package cn.sticki.spel.validator.javax;

import cn.sticki.spel.validator.javax.bean.ExampleTestBean;
import cn.sticki.spel.validator.javax.bean.SpelValidTestBean;
import cn.sticki.spel.validator.javax.util.JavaxSpelValidator;
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
        boolean verified = JavaxSpelValidator.check(ExampleTestBean.testCase());
        Assertions.assertTrue(verified);

        boolean innerTest = JavaxSpelValidator.check(ExampleTestBean.innerTestCase());
        Assertions.assertTrue(innerTest);
    }

    @Test
    void testSpelValid() {
        boolean verified = JavaxSpelValidator.check(SpelValidTestBean.paramTestCase());
        Assertions.assertTrue(verified);
    }

}
