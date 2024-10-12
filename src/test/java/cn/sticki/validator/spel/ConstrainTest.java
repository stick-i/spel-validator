package cn.sticki.validator.spel;

import cn.sticki.validator.spel.bean.*;
import cn.sticki.validator.spel.util.ValidateUtil;
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
        boolean verified = ValidateUtil.checkConstraintResult(ExampleTestBean.testCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testSpelValid() {
        boolean verified = ValidateUtil.checkConstraintResult(SpelValidTestBean.paramTestCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testSpelAssert() {
        boolean verified = ValidateUtil.checkConstraintResult(SpelAssertTestBean.paramTestCase());
        boolean emptyTest = ValidateUtil.checkConstraintResult(SpelAssertTestBean.emptyTestCase());

        Assertions.assertTrue(verified);
        Assertions.assertTrue(emptyTest);
    }

    @Test
    void testSpelNotBlank() {
        boolean verified = ValidateUtil.checkConstraintResult(SpelNotBlankTestBean.testCase());
        Assertions.assertTrue(verified);
    }

    @Test
    void testSpelNotEmpty() {
        boolean verifiedParam = ValidateUtil.checkConstraintResult(SpelNotEmptyTestBean.paramTestCase());
        boolean verifiedType = ValidateUtil.checkConstraintResult(SpelNotEmptyTestBean.typeTestCase());

        Assertions.assertTrue(verifiedParam, "spelNotEmpty param test failed");
        Assertions.assertTrue(verifiedType, "spelNotEmpty type test failed");
    }

    @Test
    void testSpelNotNull() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.paramTestCase());
        boolean typeTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.typeTestCase());
        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelNotNull param test failed");
        Assertions.assertTrue(typeTest, "spelNotNull type test failed");
        Assertions.assertTrue(repeatableTest, "spelNotNull repeatable test failed");
    }

    @Test
    void testSpelNull() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.paramTestCase());
        boolean typeTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.typeTestCase());
        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelNull param test failed");
        Assertions.assertTrue(typeTest, "spelNull type test failed");
        Assertions.assertTrue(repeatableTest, "spelNull repeatable test failed");
    }

    @Test
    void testSpelSize() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelSizeTestBean.paramTestCase());
        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelSizeTestBean.repeatableTestCase());

        Assertions.assertTrue(paramTest, "spelSize param test failed");
        Assertions.assertTrue(repeatableTest, "spelSize repeatable test failed");
    }

    @Test
    void testSpelMin() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.paramTestCase());
        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.repeatableTestCase());
        boolean paramTest2 = ValidateUtil.checkConstraintResult(SpelMinTestBean.paramTest2Case());
        boolean valueTypeTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.valueTypeTestCase());
        boolean notSupportTypeTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.notSupportTypeTestCase());

        Assertions.assertTrue(paramTest, "spelMin param test failed");
        Assertions.assertTrue(repeatableTest, "spelMin repeatable test failed");
        Assertions.assertTrue(paramTest2, "spelMin param test2 failed");
        Assertions.assertTrue(valueTypeTest, "spelMin valueType test failed");
        Assertions.assertTrue(notSupportTypeTest, "spelMin notSupportType test failed");
    }

    @Test
    void testSpelMax() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelMaxTestBean.paramTestCase());

        Assertions.assertTrue(paramTest, "spelMax param test failed");
    }
}
