package cn.sticki.validator.spel;

import cn.sticki.validator.spel.util.IGetter;
import lombok.Data;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * todo
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/14
 */
@Data
public class VerifyObject {

	/**
	 * 待验证的对象
	 */
	private Object object;

	/**
	 * 异常字段列表
	 */
	private Set<VerifyFailedField> verifyFailedFields;

	/**
	 * 校验不通过字段
	 */

	private VerifyObject() {
	}

	/**
	 * 创建待验证对象
	 *
	 * @param object 待验证对象
	 */
	public static VerifyObject of(Object object) {
		return VerifyObject.of(object, new VerifyFailedField[0]);
	}

	/**
	 * 创建待验证对象
	 *
	 * @param object 待验证对象
	 * @param field  异常字段
	 */
	@SafeVarargs
	public static <T> VerifyObject of(Object object, IGetter<T, ?>... field) {
		return VerifyObject.of(object, Arrays.stream(field).map(VerifyFailedField::of).toArray(VerifyFailedField[]::new));
	}

	/**
	 * 创建待验证对象
	 *
	 * @param object 待验证对象
	 * @param field  异常字段，异常信息
	 */
	public static VerifyObject of(Object object, VerifyFailedField... field) {
		VerifyObject verifyObject = new VerifyObject();
		verifyObject.setObject(object);
		verifyObject.setVerifyFailedFields(Arrays.stream(field).collect(Collectors.toSet()));
		return verifyObject;
	}

}
