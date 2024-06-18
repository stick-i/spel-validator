package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelNotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SpelNotBlank 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/17
 */
@Data
@SpelValid
public class SpelNotBlankTestBean {

	@SpelNotBlank
	private String typeTest1;

	@SpelNotBlank
	private StringBuilder typeTest2;

	@SpelNotBlank
	private StringBuffer typeTest3;

	@SpelNotBlank(condition = "false")
	private String test;

	@SpelNotBlank(condition = "true")
	private String test2;

	@SpelNotBlank(condition = "true", message = "test3")
	private String test3;

	public static List<VerifyObject> testCase() {
		ArrayList<VerifyObject> list = new ArrayList<>();

		// 测试用例1：类型测试
		list.add(VerifyObject.of(new TestBean2(), true));

		// 测试用例2：条件测试
		VerifyObject verifyObject2 = VerifyObject.of(new SpelNotBlankTestBean(),
				SpelNotBlankTestBean::getTypeTest1,
				SpelNotBlankTestBean::getTypeTest2,
				SpelNotBlankTestBean::getTypeTest3,
				SpelNotBlankTestBean::getTest2
		);
		verifyObject2.getVerifyFailedFields().add(VerifyFailedField.of(SpelNotBlankTestBean::getTest3, "test3"));
		list.add(verifyObject2);

		return list;
	}

	@Data
	@SpelValid
	public static class TestBean2 {

		@SpelNotBlank
		private Object objectTypeTest;

	}

}

