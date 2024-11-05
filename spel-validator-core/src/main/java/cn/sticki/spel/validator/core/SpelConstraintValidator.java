package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

/**
 * Spel 约束校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/11
 */
public interface SpelConstraintValidator<A extends Annotation> {

    Set<Class<?>> DEFAULT_SUPPORT_TYPE = Collections.singleton(Object.class);

    /**
     * 校验被标记的字段
     * <p>
     * 限制：
     * <ul>
     *     <li>该方法会被并发访问，实现时需要保证线程安全。</li>
     *     <li>校验时不能改变 obj 对象的值，这个对象与接口参数的对象是同一个。</li>
     * </ul>
     *
     * @param annotation 注解值
     * @param obj        被校验的对象
     * @param field      被校验的字段，该字段存在于 obj 中
     * @return 校验结果
     */
    FieldValidResult isValid(A annotation, Object obj, Field field) throws IllegalAccessException;

    /**
     * 校验器支持的对象类型列表，默认为 Object
     *
     * @return 支持的对象类型列表
     */
    default Set<Class<?>> supportType() {
        return DEFAULT_SUPPORT_TYPE;
    }

}
