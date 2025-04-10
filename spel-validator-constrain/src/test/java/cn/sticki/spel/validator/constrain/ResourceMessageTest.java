package cn.sticki.spel.validator.constrain;

import cn.sticki.spel.validator.core.message.ResourceBundleMessageResolver;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

/**
 * 资源包消息测试
 *
 * @author 阿杆
 * @since 2025/4/10
 */
@Slf4j
public class ResourceMessageTest {

    @Test
    void testRead() {
        String message = ResourceBundleMessageResolver.getMessage("cn.sticki.spel.validator.constraint.AssertFalse.message", Locale.CHINA);
        Assertions.assertEquals("只能为false", message);
    }

    @Test
    void testReadWithLocale() {
        String key = "cn.sticki.spel.validator.constraint.Size.message";
        String message = ResourceBundleMessageResolver.getMessage(key, Locale.CHINA, 1, 2);
        Assertions.assertEquals("个数必须在1和2之间", message);

        message = ResourceBundleMessageResolver.getMessage(key, Locale.US, 1, 2);
        Assertions.assertEquals("size must be between 1 and 2", message);

        message = ResourceBundleMessageResolver.getMessage(key, Locale.JAPAN, 1, 2);
        Assertions.assertEquals("1 から 2 の間のサイズにしてください", message);
    }

}
