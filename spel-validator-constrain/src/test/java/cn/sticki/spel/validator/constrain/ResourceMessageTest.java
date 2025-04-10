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
    void test() {
        String message = ResourceBundleMessageResolver.getMessage("cn.sticki.spel.validator.constraint.AssertFalse.message", Locale.getDefault(), 1, 2);
        log.info("message: {}", message);
        Assertions.assertFalse(message.isEmpty());
    }

}
