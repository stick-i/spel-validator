package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.ExceptionField;
import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelAssert;
import cn.sticki.validator.spel.util.ID;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SpelAssert 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
@Data
@SpelValid(spelGroups = "#this.group")
public class SpelAssertTestBean implements ID {

	private int id;

	@SpelAssert(condition = "false", assertTrue = "false")
	private Integer test;

	@SpelAssert(condition = "true", assertTrue = "false ")
	private List<Integer> test2;

	@SpelAssert(condition = "true", assertTrue = "true")
	private Double test3;

	@SpelAssert(assertTrue = "#this.test4 == 'test4'", message = "test4")
	private String test4;

	private String group;

	@SpelAssert(assertTrue = "false", message = "group1", group = "'group1'")
	private Boolean test5;

	private String group2 = "group2";

	@SpelAssert(assertTrue = "false", message = "group2", group = "#this.group2")
	private Boolean test6;

	public static List<VerifyObject> testCase() {
		ArrayList<VerifyObject> list = new ArrayList<>();

		SpelAssertTestBean bean = new SpelAssertTestBean();
		bean.setId(1);
		list.add(VerifyObject.of(
				bean,
				ExceptionField.of(SpelAssertTestBean::getTest2),
				ExceptionField.of(SpelAssertTestBean::getTest4, "test4")
		));

		SpelAssertTestBean bean2 = new SpelAssertTestBean();
		bean2.setId(2);
		bean2.setTest4("test4");
		bean2.setGroup("group1");
		list.add(VerifyObject.of(
				bean2,
				ExceptionField.of(SpelAssertTestBean::getTest2),
				ExceptionField.of(SpelAssertTestBean::getTest5, "group1")
		));

		SpelAssertTestBean bean3 = new SpelAssertTestBean();
		bean3.setId(3);
		bean3.setTest4("test4");
		bean3.setGroup("00");
		list.add(VerifyObject.of(
				bean3,
				ExceptionField.of(SpelAssertTestBean::getTest2)
		));

		SpelAssertTestBean bean4 = new SpelAssertTestBean();
		bean4.setId(4);
		bean4.setGroup("group2");
		// bean4.setGroup2("group2");
		list.add(VerifyObject.of(
				bean4,
				ExceptionField.of(SpelAssertTestBean::getTest2),
				ExceptionField.of(SpelAssertTestBean::getTest4, "test4"),
				ExceptionField.of(SpelAssertTestBean::getTest6, "group2")
		));

		return list;
	}

}
