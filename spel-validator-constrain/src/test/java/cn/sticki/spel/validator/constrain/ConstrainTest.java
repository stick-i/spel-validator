package cn.sticki.spel.validator.constrain;

import cn.sticki.spel.validator.constrain.bean.*;
import cn.sticki.spel.validator.test.util.BaseSpelValidator;
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

    @Test
    void testSpelAssert() {
        boolean verified = BaseSpelValidator.check(SpelAssertTestBean.paramTestCase());
        boolean emptyTest = BaseSpelValidator.check(SpelAssertTestBean.emptyTestCase());

        Assertions.assertTrue(verified);
        Assertions.assertTrue(emptyTest);
    }

    @Test
    void testSpelNotBlank() {
        boolean verified = BaseSpelValidator.check(SpelNotBlankTestBean.testCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testSpelNotEmpty() {
        boolean verifiedParam = BaseSpelValidator.check(SpelNotEmptyTestBean.paramTestCase());
        boolean verifiedType = BaseSpelValidator.check(SpelNotEmptyTestBean.typeTestCase());

        Assertions.assertTrue(verifiedParam, "spelNotEmpty param test failed");
        Assertions.assertTrue(verifiedType, "spelNotEmpty type test failed");
    }

    @Test
    void testSpelNotNull() {
        boolean paramTest = BaseSpelValidator.check(SpelNotNullTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelNotNullTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelNotNullTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelNotNull param test failed");
        Assertions.assertTrue(typeTest, "spelNotNull type test failed");
        Assertions.assertTrue(repeatableTest, "spelNotNull repeatable test failed");
    }

    @Test
    void testSpelNull() {
        boolean paramTest = BaseSpelValidator.check(SpelNullTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelNullTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelNullTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelNull param test failed");
        Assertions.assertTrue(typeTest, "spelNull type test failed");
        Assertions.assertTrue(repeatableTest, "spelNull repeatable test failed");
    }

    @Test
    void testSpelSize() {
        boolean paramTest = BaseSpelValidator.check(SpelSizeTestBean.paramTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelSizeTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelSize param test failed");
        Assertions.assertTrue(repeatableTest, "spelSize repeatable test failed");
    }

    @Test
    void testSpelMin() {
        boolean paramTest = BaseSpelValidator.check(SpelMinTestBean.paramTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelMinTestBean.repeatableTestCase());
        boolean paramTest2 = BaseSpelValidator.check(SpelMinTestBean.paramTest2Case());
        boolean valueTypeTest = BaseSpelValidator.check(SpelMinTestBean.valueTypeTestCase());
        boolean notSupportTypeTest = BaseSpelValidator.check(SpelMinTestBean.notSupportTypeTestCase());

        Assertions.assertTrue(paramTest, "spelMin param test failed");
        Assertions.assertTrue(repeatableTest, "spelMin repeatable test failed");
        Assertions.assertTrue(paramTest2, "spelMin param test2 failed");
        Assertions.assertTrue(valueTypeTest, "spelMin valueType test failed");
        Assertions.assertTrue(notSupportTypeTest, "spelMin notSupportType test failed");
    }

    @Test
    void testSpelMax() {
        boolean paramTest = BaseSpelValidator.check(SpelMaxTestBean.paramTestCase());

        Assertions.assertTrue(paramTest, "spelMax param test failed");
    }

    @Test
    void testSpelFuture() {
        boolean paramTest = BaseSpelValidator.check(SpelFutureTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelFutureTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelFutureTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelFuture param test failed");
        Assertions.assertTrue(typeTest, "spelFuture type test failed");
        Assertions.assertTrue(repeatableTest, "spelFuture repeatable test failed");
    }

    @Test
    void testSpelFutureOrPresent() {
        boolean paramTest = BaseSpelValidator.check(SpelFutureOrPresentTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelFutureOrPresentTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelFutureOrPresentTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelFutureOrPresent param test failed");
        Assertions.assertTrue(typeTest, "spelFutureOrPresent type test failed");
        Assertions.assertTrue(repeatableTest, "spelFutureOrPresent repeatable test failed");
    }

    @Test
    void testSpelPast() {
        boolean paramTest = BaseSpelValidator.check(SpelPastTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelPastTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelPastTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelPast param test failed");
        Assertions.assertTrue(typeTest, "spelPast type test failed");
        Assertions.assertTrue(repeatableTest, "spelPast repeatable test failed");
    }

    @Test
    void testSpelPastOrPresent() {
        boolean paramTest = BaseSpelValidator.check(SpelPastOrPresentTestBean.paramTestCase());
        boolean typeTest = BaseSpelValidator.check(SpelPastOrPresentTestBean.typeTestCase());
        boolean repeatableTest = BaseSpelValidator.check(SpelPastOrPresentTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelPastOrPresent param test failed");
        Assertions.assertTrue(typeTest, "spelPastOrPresent type test failed");
        Assertions.assertTrue(repeatableTest, "spelPastOrPresent repeatable test failed");
    }

}
