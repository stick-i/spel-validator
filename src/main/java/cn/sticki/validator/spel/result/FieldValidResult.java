package cn.sticki.validator.spel.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * 字段校验结果
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldValidResult {

	/**
	 * 校验结果，true表示校验通过，false表示校验失败
	 */
	private boolean success;

	/**
	 * 校验失败时的错误信息
	 * <p>
	 * 当校验结果为false时，会将错误信息添加到最终的结果中，若此字段为null，则使用默认的错误信息
	 */
	@NotNull
	private String message = "";

	/**
	 * 验证的字段名称，用于校验失败时构建错误信息
	 */
	@NotNull
	private String fieldName = "";

	public FieldValidResult(boolean success) {
		this.success = success;
	}

	public FieldValidResult(boolean success, @NotNull String message) {
		this.success = success;
		this.message = message;
	}

	public static FieldValidResult success() {
		return new FieldValidResult(true);
	}

}
