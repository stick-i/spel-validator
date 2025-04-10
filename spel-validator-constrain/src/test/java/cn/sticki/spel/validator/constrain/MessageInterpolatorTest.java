package cn.sticki.spel.validator.constrain;

import cn.sticki.spel.validator.core.message.ResourceBundleMessageResolver;
import cn.sticki.spel.validator.core.message.ValidatorMessageInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * 消息插值器测试
 *
 * @author 阿杆
 * @since 2025/4/10
 */
public class MessageInterpolatorTest {

    private final ValidatorMessageInterpolator messageInterpolator = new ValidatorMessageInterpolator();

    @Test
    void testEscapedLiterals() {
        ResourceBundleMessageResolver.addBasenames("testMessages");

        String message = "{test.info}";
        String interpolate = messageInterpolator.interpolate(message, Locale.getDefault());
        Assertions.assertEquals("Test info", interpolate);

        String originalMessage = "{test.info}";
        message = "\\{test.info}";
        interpolate = messageInterpolator.interpolate(message, Locale.getDefault());
        Assertions.assertEquals(originalMessage, interpolate);

        message = "\\{test.info\\}";
        interpolate = messageInterpolator.interpolate(message, Locale.getDefault());
        Assertions.assertEquals(originalMessage, interpolate);

        message = "{test.info}\\\\";
        interpolate = messageInterpolator.interpolate(message, Locale.getDefault());
        Assertions.assertEquals(originalMessage + "\\", interpolate);
    }

    @Test
    void testSequence() {
        ResourceBundleMessageResolver.addBasenames("testMessages");
        String message = "{cn.sticki.spel.validator.constraint.Size.message}";
        String interpolate = messageInterpolator.interpolate(message, Locale.getDefault(), 1, 2);
        Assertions.assertEquals("size must be between 1 and 2 (test)", interpolate);

        ResourceBundleMessageResolver.addBasenames("testMessages2");
        interpolate = messageInterpolator.interpolate(message, Locale.getDefault(), 1, 2);
        Assertions.assertEquals("size must be between 1 and 2 (test2)", interpolate);
    }

}
