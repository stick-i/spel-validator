package cn.sticki.spel.validator.core.message;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证器消息解析器
 *
 * @author 阿杆
 * @since 2025/4/9
 */
@Slf4j
public class ValidatorMessageInterpolator {

    private static final Pattern LEFT_BRACE = Pattern.compile("\\{", Pattern.LITERAL);

    private static final Pattern RIGHT_BRACE = Pattern.compile("\\}", Pattern.LITERAL);

    private static final Pattern SLASH = Pattern.compile("\\\\", Pattern.LITERAL);

    // private static final Pattern DOLLAR = Pattern.compile("\\$", Pattern.LITERAL);

    public String interpolate(String message, Locale locale, Object... args) {
        return interpolateMessage(message, locale, args);
    }

    /**
     * 解析消息中的key，并从资源包中获取对应的多语言消息
     * <p>
     * @return the interpolated message.
     */
    private String interpolateMessage(String message, Locale locale, Object... args) {
        if (message.indexOf('{') < 0) {
            return replaceEscapedLiterals(message);
        }

        String resolvedMessage = resolveMessage(message, locale, args);
        resolvedMessage = replaceEscapedLiterals(resolvedMessage);

        return resolvedMessage;
    }

    private String resolveMessage(String message, Locale locale, Object... args) {
        if (message.charAt(0) != '{' || message.charAt(message.length() - 1) != '}') {
            return message;
        } else {
            // get key from message
            String key = message.substring(1, message.length() - 1);
            // get message from resource bundle
            return ResourceBundleMessageResolver.getMessage(key, locale, args);
        }
    }

    private String replaceEscapedLiterals(String resolvedMessage) {
        if (resolvedMessage.indexOf('\\') > -1) {
            resolvedMessage = LEFT_BRACE.matcher(resolvedMessage).replaceAll("{");
            resolvedMessage = RIGHT_BRACE.matcher(resolvedMessage).replaceAll("}");
            resolvedMessage = SLASH.matcher(resolvedMessage).replaceAll(Matcher.quoteReplacement("\\"));
            // resolvedMessage = DOLLAR.matcher(resolvedMessage).replaceAll(Matcher.quoteReplacement("$"));
        }
        return resolvedMessage;
    }

}
