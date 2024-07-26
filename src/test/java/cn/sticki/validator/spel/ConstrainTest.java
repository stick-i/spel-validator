package cn.sticki.validator.spel;

import cn.sticki.validator.spel.bean.SpelAssertTestBean;
import cn.sticki.validator.spel.bean.SpelNotBlankTestBean;
import cn.sticki.validator.spel.bean.SpelNotEmptyTestBean;
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

	@Test
	void testSpelAssert() {
		boolean verified = ValidateUtil.checkConstraintResult(SpelAssertTestBean.testCase());
		Assertions.assertTrue(verified);
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
}
