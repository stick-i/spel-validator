package cn.sticki.validator.spel.exception;

/**
 * Spel 校验异常
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class SpelValidException extends RuntimeException {

	public SpelValidException(String message) {
		super(message);
	}

	public SpelValidException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpelValidException(Throwable cause) {
		super(cause);
	}

}
