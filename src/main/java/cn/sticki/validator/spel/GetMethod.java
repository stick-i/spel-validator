package cn.sticki.validator.spel;

import lombok.EqualsAndHashCode;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * copy from {@link org.hibernate.validator.internal.util.privilegedactions.GetMethod}
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/30
 */
@EqualsAndHashCode
public class GetMethod implements PrivilegedAction<Method> {

	private static final Map<GetMethod, Method> runCache = new ConcurrentHashMap<>();

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
		updateCache();
		return runCache.get(this);
	}

	private void updateCache() {
		if (runCache.containsKey(this)) {
			return;
		}
		synchronized (runCache) {
			if (!runCache.containsKey(this)) {
				runCache.put(this, this.internalRun());
			}
		}
	}

	private Method internalRun() {
		try {
			return this.clazz.getMethod(this.methodName);
		} catch (NoSuchMethodException var2) {
			return null;
		}
	}

}
