package cn.sticki.validator.spel.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 计算长度工具类
 * <p>
 * 支持的类型有：
 * <ul>
 *     <li>{@link CharSequence}（评估字符序列的长度）</li>
 *     <li>{@link java.util.Collection}（评估集合大小）</li>
 *     <li>{@link java.util.Map}（评估Map大小）</li>
 *     <li>数组（计算数组长度）</li>
 * </ul>
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class CalcLengthUtil {

	public static final Set<Class<?>> SUPPORT_TYPE;

	static {
		HashSet<Class<?>> hashSet = new HashSet<>();
		hashSet.add(CharSequence.class);
		hashSet.add(java.util.Collection.class);
		hashSet.add(java.util.Map.class);
		hashSet.add(Object[].class);
		SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
	}

	public static int calcFieldSize(Object object) {
		if (object instanceof CharSequence) {
			return ((CharSequence) object).length();
		} else if (object instanceof java.util.Collection) {
			return ((java.util.Collection<?>) object).size();
		} else if (object instanceof java.util.Map) {
			return ((java.util.Map<?, ?>) object).size();
		} else if (object instanceof Object[]) {
			return ((Object[]) object).length;
		} else {
			return 0;
		}
	}

	public static String getFieldSign(Object object) {
		if (object instanceof CharSequence) {
			return "长度";
		} else {
			return "大小";
		}
	}

}
