package cn.sticki.spel.validator.core.message;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

/**
 * 资源包消息解析器
 *
 * @author 阿杆
 * @since 2025/2/25
 */
public class ResourceBundleMessageResolver {

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
     * 添加资源包
     *
     * @param basename 资源包名称
     */
    public static void addBasenames(String... basename) {
        MESSAGE_SOURCE.addBasenames(basename);
    }

    public static String getMessage(String key, Locale locale, Object... args) {
        return MESSAGE_SOURCE.getMessage(key, args, locale);
    }

}
