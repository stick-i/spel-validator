package cn.sticki.validator.spel.manager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解类方法管理器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/9
 */
public class AnnotationMethodManager {

    private final static ConcurrentHashMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取方法。
     *
     * @param clazz      注解类
     * @param methodName 方法名
     * @return 方法，如果不存在则返回null
     */
    public static Method get(Class<? extends Annotation> clazz, String methodName) {
        // 由于注解类的方法无法重载，可以通过注解类和方法名来唯一确定一个方法
        return METHOD_CACHE.computeIfAbsent(clazz.toString() + "#" + methodName, s -> {
            try {
                return clazz.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                return null;
            }
        });
    }

}
