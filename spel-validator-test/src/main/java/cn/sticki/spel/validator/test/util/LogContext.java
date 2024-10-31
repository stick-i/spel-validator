package cn.sticki.spel.validator.test.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * 日志上下文
 *
 * @author 阿杆
 * @since 2024/10/29
 */
@Slf4j
public class LogContext {

    /**
     * 设置验证对象日志上下文
     */
    public static void setValidateObject(Object object) {
        String className = object.getClass().getSimpleName();
        Class<?> enclosingClass = object.getClass().getEnclosingClass();
        if (enclosingClass != null) {
            className = enclosingClass.getSimpleName() + "." + className;
        }

        MDC.put("className", className);
        MDC.put("fullClassName", abbreviate(object.getClass().getName()));
        if (object instanceof ID) {
            MDC.put("id", String.valueOf(((ID) object).getId()));
        }
    }

    /**
     * 清除验证对象日志上下文
     */
    public static void clearValidateObject() {
        MDC.remove("id");
        MDC.remove("className");
        MDC.remove("fieldName");
        MDC.remove("fullClassName");
    }

    public static void set(String key, String value) {
        MDC.put(key, value);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * 缩写类名
     */
    private static String abbreviate(String className) {
        String[] parts = className.split("\\.");
        StringBuilder abbreviated = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            abbreviated.append(parts[i].charAt(0)).append(".");
        }
        abbreviated.append(parts[parts.length - 1]);
        return abbreviated.toString();
    }

}
