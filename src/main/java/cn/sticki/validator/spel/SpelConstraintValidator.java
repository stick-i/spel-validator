package cn.sticki.validator.spel;

import cn.sticki.validator.spel.result.FieldValidResult;

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

	/**
	 * 校验被标记的注解。
	 *
	 * @param annotation 注解值
	 * @param obj        被校验的对象
	 * @param field      被校验的字段，该字段存在于 obj 中
	 * @return 校验结果
	 */
	FieldValidResult isValid(A annotation, Object obj, Field field);

	Set<Class<?>> DEFAULT_SUPPORT_TYPE = Collections.singleton(Object.class);

	/**
	 * 校验器支持的对象类型列表，默认为 Object。
	 */
	default Set<Class<?>> supportType() {
		return DEFAULT_SUPPORT_TYPE;
	}

}
