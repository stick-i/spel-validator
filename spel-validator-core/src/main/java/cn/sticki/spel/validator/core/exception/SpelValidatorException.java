package cn.sticki.spel.validator.core.exception;

/**
 * Spel 验证器异常，是项目中所有异常的父类。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class SpelValidatorException extends RuntimeException {

    public SpelValidatorException(String message) {
        super(message);
    }

    public SpelValidatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpelValidatorException(Throwable cause) {
        super(cause);
    }

}
