package cn.sticki.spel.validator.core.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 资源包消息解析器
 *
 * @author 阿杆
 * @since 2025/2/25
 */
@Slf4j
public class ResourceBundleMessageResolver {

    private ResourceBundleMessageResolver() {}

    /**
     * The name of the default message bundle.
     */
    public static final String DEFAULT_VALIDATION_MESSAGES = "cn.sticki.spel.validator.ValidationMessages";

    private static final ResourceBundleMessageSource MESSAGE_SOURCE = initMessageSource();

    private static ResourceBundleMessageSource initMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(
                DEFAULT_VALIDATION_MESSAGES
        );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * 重置资源包
     */
    public static void resetBasenames() {
        MESSAGE_SOURCE.setBasenames(
                DEFAULT_VALIDATION_MESSAGES
        );
    }

    /**
     * 添加资源包
     *
     * @param basename 资源包名称
     */
    public static void addBasenames(String... basename) {
        String[] existingBasename = MESSAGE_SOURCE.getBasenameSet().toArray(new String[0]);

        // 创建一个新的 basename 数组，将新添加的放在前面
        String[] combinedBasename = new String[basename.length + existingBasename.length];
        System.arraycopy(basename, 0, combinedBasename, 0, basename.length);
        System.arraycopy(existingBasename, 0, combinedBasename, basename.length, existingBasename.length);
        log.debug("Combined basename: {}", (Object) combinedBasename);

        // 重新设置 basename
        MESSAGE_SOURCE.setBasenames(combinedBasename);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        return MESSAGE_SOURCE.getMessage(key, args, locale);
    }

}
