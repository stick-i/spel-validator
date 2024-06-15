package cn.sticki.validator.spel;

import cn.sticki.validator.spel.util.BeanUtil;
import cn.sticki.validator.spel.util.IGetter;
import lombok.Data;

/**
 * 异常字段信息
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
@Data
public class ExceptionField {

	/**
	 * 字段名
	 */
	String name;

	/**
	 * 异常信息
	 */
	String message;

	private ExceptionField() {
	}

	public static ExceptionField of(String fieldName) {
		return ExceptionField.of(fieldName, null);
	}

	public static <T> ExceptionField of(IGetter<T, ?> field) {
		return ExceptionField.of(field, null);
	}

	public static <T> ExceptionField of(IGetter<T, ?> field, String errorMessage) {
		return ExceptionField.of(BeanUtil.getFieldName(field), errorMessage);
	}

	public static ExceptionField of(String fieldName, String errorMessage) {
		ExceptionField exceptionField = new ExceptionField();
		exceptionField.setName(fieldName);
		exceptionField.setMessage(errorMessage);
		return exceptionField;
	}

}
