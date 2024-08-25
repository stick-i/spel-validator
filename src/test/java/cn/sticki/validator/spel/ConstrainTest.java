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
    void testSpelAssert() {
        boolean verified = ValidateUtil.checkConstraintResult(SpelAssertTestBean.paramTestCase());
        Assertions.assertTrue(verified);

        boolean emptyTest = ValidateUtil.checkConstraintResult(SpelAssertTestBean.emptyTestCase());
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
        Assertions.assertTrue(verifiedParam, "spelNotEmpty param test failed");

        boolean verifiedType = ValidateUtil.checkConstraintResult(SpelNotEmptyTestBean.typeTestCase());
        Assertions.assertTrue(verifiedType, "spelNotEmpty type test failed");
    }

    @Test
    void testSpelNotNull() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.paramTestCase());
        Assertions.assertTrue(paramTest, "spelNotNull param test failed");

        boolean typeTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.typeTestCase());
        Assertions.assertTrue(typeTest, "spelNotNull type test failed");

        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelNotNullTestBean.repeatableTestCase());
        Assertions.assertTrue(repeatableTest, "spelNotNull repeatable test failed");
    }

    @Test
    void testSpelNull() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.paramTestCase());
        Assertions.assertTrue(paramTest, "spelNull param test failed");

        boolean typeTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.typeTestCase());
        Assertions.assertTrue(typeTest, "spelNull type test failed");

        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelNullTestBean.repeatableTestCase());
        Assertions.assertTrue(repeatableTest, "spelNull repeatable test failed");
    }

    @Test
    void testSpelSize() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelSizeTestBean.paramTestCase());
        Assertions.assertTrue(paramTest, "spelSize param test failed");

        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelSizeTestBean.repeatableTestCase());
        Assertions.assertTrue(repeatableTest, "spelSize repeatable test failed");
    }

    @Test
    void testSpelMin() {
        boolean paramTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.paramTestCase());
        Assertions.assertTrue(paramTest, "spelMin param test failed");

        boolean repeatableTest = ValidateUtil.checkConstraintResult(SpelMinTestBean.repeatableTestCase());
        Assertions.assertTrue(repeatableTest, "spelMin repeatable test failed");
    }
}
