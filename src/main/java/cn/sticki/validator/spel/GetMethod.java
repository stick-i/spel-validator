package cn.sticki.validator.spel;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;

/**
 * copy from {@link org.hibernate.validator.internal.util.privilegedactions.GetMethod}
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/30
 */
public class GetMethod implements PrivilegedAction<Method> {

	private final Class<?> clazz;

	private final String methodName;

	public static GetMethod action(Class<?> clazz, String methodName) {
		return new GetMethod(clazz, methodName);
	}

	private GetMethod(Class<?> clazz, String methodName) {
		this.clazz = clazz;
		this.methodName = methodName;
	}

	@Override
	public Method run() {
		try {
			return this.clazz.getMethod(this.methodName);
		} catch (NoSuchMethodException var2) {
			return null;
		}
	}

}
