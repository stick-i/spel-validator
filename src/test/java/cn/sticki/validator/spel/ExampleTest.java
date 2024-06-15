package cn.sticki.validator.spel;

import cn.sticki.validator.spel.bean.ExampleTestBean;
import cn.sticki.validator.spel.util.ValidateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 这是一个测试示例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/24
 */
public class ExampleTest {

	@Test
	void validateObject_withNoSpelConstraint_shouldReturnSuccess() {
		boolean verified = ValidateUtil.checkConstraintResult(ExampleTestBean.getVariableObjectList());
		Assertions.assertTrue(verified);
	}

}