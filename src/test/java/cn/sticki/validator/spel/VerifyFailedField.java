package cn.sticki.validator.spel;

import cn.sticki.validator.spel.util.BeanUtil;
import cn.sticki.validator.spel.util.IGetter;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 验证失败的字段
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
@Data
public class VerifyFailedField {

	/**
	 * 字段名
	 */
	String name;

	/**
	 * 异常信息
	 */
	String message;

	private VerifyFailedField() {
	}

	public static VerifyFailedField of(String fieldName) {
		return VerifyFailedField.of(fieldName, null);
	}

	public static <T> VerifyFailedField of(IGetter<T, ?> field) {
		return VerifyFailedField.of(field, null);
	}

	@SafeVarargs
	public static <T> List<VerifyFailedField> of(IGetter<T, ?>... fields) {
		return Arrays.stream(fields).map(VerifyFailedField::of).collect(Collectors.toList());
	}

	public static <T> VerifyFailedField of(IGetter<T, ?> field, String errorMessage) {
		return VerifyFailedField.of(BeanUtil.getFieldName(field), errorMessage);
	}

	public static VerifyFailedField of(String fieldName, String errorMessage) {
		VerifyFailedField verifyFailedField = new VerifyFailedField();
		verifyFailedField.setName(fieldName);
		verifyFailedField.setMessage(errorMessage);
		return verifyFailedField;
	}

}
