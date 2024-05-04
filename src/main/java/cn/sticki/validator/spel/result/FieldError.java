package cn.sticki.validator.spel.result;

import lombok.Getter;
import lombok.ToString;

/**
 * 字段错误信息
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
@ToString
@Getter
public class FieldError {

	/**
	 * 字段名称
	 */
	private final String fieldName;

	/**
	 * 错误信息
	 */
	private final String errorMessage;

	public FieldError(String fieldName, String errorMessage) {
		this.fieldName = fieldName;
		this.errorMessage = errorMessage;
	}

}