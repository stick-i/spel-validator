package cn.sticki.spel.validator.core.exception;

/**
 * 表达式解析异常
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class SpelParserException extends SpelValidatorException {

    public SpelParserException(String message) {
        super(message);
    }

    public SpelParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpelParserException(Throwable cause) {
        super(cause);
    }

}
