package cn.sticki.validator.spel;

import cn.sticki.validator.spel.exception.SpelNotSupportedTypeException;
import cn.sticki.validator.spel.exception.SpelValidException;
import cn.sticki.validator.spel.manager.AnnotationMethodManager;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.result.ObjectValidResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spel 相关注解的执行器，对使用了 {@link SpelConstraint} 进行标记的注解执行校验。
 *
 * @author 阿杆
 * @version 1.0
 * @see SpelConstraint
 * @since 2024/4/29
 */
@Slf4j
public class SpelValidExecutor {

	private static final String MESSAGE = "message";

	private static final String CONDITION = "condition";

	private static final String GROUP = "group";

	/**
	 * 验证对象
	 * <p>
	 * 如果对象中有任意使用了 spel 约束注解的字段，则会对该字段进行校验。
	 *
	 * @param verifiedObject 被校验的对象
	 * @return 对象校验结果
	 */
	@SuppressWarnings("unused")
	@NotNull
	public static ObjectValidResult validateObject(@NotNull Object verifiedObject) {
		return validateObject(verifiedObject, Collections.emptySet());
	}

	/**
	 * 验证对象
	 * <p>
	 * 如果对象中有任意使用了 spel 约束注解的字段，则会对该字段进行校验。
	 *
	 * @param verifiedObject 被校验的对象
	 * @param validateGroups 分组信息，只有同组的注解才会被校验
	 * @return 对象校验结果
	 */
	@NotNull
	public static ObjectValidResult validateObject(@NotNull Object verifiedObject, @NotNull Set<Object> validateGroups) {
		log.debug("Spel validate start, class [{}], groups [{}]", verifiedObject.getClass().getName(), validateGroups);
		log.debug("Verified object [{}]", verifiedObject);
		long startTime = System.nanoTime();

		List<FieldValidResult> validationResults = new ArrayList<>();

		// 获取类的字段 todo 缓存
		Field[] fields = verifiedObject.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			validateField(verifiedObject, field, validateGroups, validationResults);
		}

		ObjectValidResult validResult = new ObjectValidResult();
		validResult.addFieldResults(validationResults);

		log.debug("Spel validate over,error list {}", validResult.getErrors());
		log.debug("Spel validate cost time {} ms", (System.nanoTime() - startTime) / 1000000);
		return validResult;
	}

	/**
	 * 验证字段
	 */
	private static void validateField(
			@NotNull Object verifiedObject,
			@NotNull Field verifiedField,
			@NotNull Set<Object> validGroups,
			@NotNull List<FieldValidResult> validationResults
	) {
		// 获取字段上的注解 todo 缓存
		Annotation[] annotations = verifiedField.getAnnotations();
		for (Annotation originalAnno : annotations) {
			String annoName = originalAnno.annotationType().getName();
			if (annoName.endsWith("$List") || annoName.endsWith("Container")) {
				// 容器注解，需要获取容器内部的注解类型，其声明类为真实的注解类
				Class<?> clazz = originalAnno.annotationType().getDeclaringClass();
				//noinspection unchecked
				Annotation[] originalAnnoArray = verifiedField.getAnnotationsByType((Class<Annotation>) clazz);
				for (Annotation anno : originalAnnoArray) {
					// 执行验证
					validateFieldAnnotation(anno, verifiedObject, verifiedField, validationResults, validGroups);
				}
			} else {
				// 执行验证
				validateFieldAnnotation(originalAnno, verifiedObject, verifiedField, validationResults, validGroups);
			}
		}
	}

	/**
	 * 对任意的注解执行校验，当注解不是Ex约束注解时，将返回null。
	 *
	 * @param annotation        注解数组，数组内的注解必须为同一类型
	 * @param verifiedObject    被校验的对象
	 * @param verifiedField     被校验的字段，必须存在于被校验的对象中
	 * @param validationResults 校验结果列表
	 * @param validateGroups    分组信息
	 */
	private static void validateFieldAnnotation(
			@NotNull Annotation annotation,
			@NotNull Object verifiedObject,
			@NotNull Field verifiedField,
			@NotNull List<FieldValidResult> validationResults,
			@NotNull Set<Object> validateGroups
	) {
		Class<? extends Annotation> annoClazz = annotation.annotationType();
		// 验证注解的合法性
		if (!isSpelConstraintAnnotationCached(annoClazz)) {
			return;
		}

		log.debug("===> Find target annotation [{}], verifiedField [{}]", annoClazz.getSimpleName(), verifiedField.getName());
		log.debug("===> Annotation object [{}]", annotation);

		// 获取验证器实例 todo 缓存
		Class<? extends SpelConstraintValidator<?>> validatorClass = annoClazz.getAnnotation(SpelConstraint.class).validatedBy();
		SpelConstraintValidator<? extends Annotation> validator = getValidatorInstance(validatorClass);

		// 判断对象的类型是否受支持
		Set<Class<?>> supported = validator.supportType();
		Class<?> verifiedFieldClass = verifiedField.getType();
		if (supported.stream().noneMatch(clazz -> clazz.isAssignableFrom(verifiedFieldClass))) {
			log.error("===> Object type not supported, skip validate. supported types [{}]", supported);
			throw new SpelNotSupportedTypeException(verifiedFieldClass, supported);
		}

		// 匹配分组
		Set<Object> annoGroups = parseGroups(annotation, verifiedObject);
		if (!annoGroups.isEmpty() && !matchGroup(validateGroups, annoGroups)) {
			log.debug("===> Group not matched, skip validate. annotation groups [{}]", annoGroups);
			return;
		}

		// 判断condition条件是否成立
		String condition = getAnnotationValue(annotation, CONDITION);
		if (!condition.isEmpty() && !SpelParser.parse(condition, verifiedObject, Boolean.class)) {
			log.debug("===> Condition not valid, skip validate. condition [{}]", condition);
			return;
		}

		// 执行校验
		FieldValidResult validationResult = doValidate(validator, annotation, verifiedObject, verifiedField);
		validationResults.add(validationResult);

		log.debug("===> Validate result [{}]", validationResult.isSuccess());
	}

	/**
	 * 执行校验
	 *
	 * @param validator      校验器
	 * @param annotation     注解
	 * @param verifiedObject 被校验的对象
	 * @param verifiedField  被校验的字段，必须存在于被校验的对象中
	 */
	@NotNull
	private static <A extends Annotation> FieldValidResult doValidate(
			@NotNull SpelConstraintValidator<?> validator,
			@NotNull A annotation,
			@NotNull Object verifiedObject,
			@NotNull Field verifiedField
	) {
		FieldValidResult result;
		try {
			// noinspection unchecked
			result = ((SpelConstraintValidator<A>) validator).isValid(annotation, verifiedObject, verifiedField);
		} catch (RuntimeException e) {
			log.error("Spel validate error: {} ;Located in the annotation [{}] of class [{}] field [{}]",
					e.getMessage(), annotation.annotationType().getName(), verifiedObject.getClass().getName(), verifiedField.getName());
			throw new SpelValidException(e);
		} catch (IllegalAccessException e) {
			// 被验证的字段在类中无法访问
			log.error("The validated field [{}] is not accessible in the class [{}]",
					verifiedField.getName(), verifiedObject.getClass().getName());
			throw new SpelValidException("Failed to access field value", e);
		}
		autoFillValidResult(result, annotation, verifiedField);
		return result;
	}

	/**
	 * 约束注解缓存
	 */
	private static final ConcurrentHashMap<Class<? extends Annotation>, Boolean> CONSTRAINT_ANNOTATION_CACHE = new ConcurrentHashMap<>();

	/**
	 * 判断注解是否为合法的约束注解，该方法会缓存结果。
	 */
	private static boolean isSpelConstraintAnnotationCached(@NotNull Class<? extends Annotation> annotationType) {
		return CONSTRAINT_ANNOTATION_CACHE.computeIfAbsent(annotationType, SpelValidExecutor::isSpelConstraintAnnotation);
	}

	/**
	 * 判断注解是否为合法的约束注解
	 */
	private static boolean isSpelConstraintAnnotation(@NotNull Class<? extends Annotation> annotationType) {
		if (!annotationType.isAnnotationPresent(SpelConstraint.class)) {
			return false;
		}

		if (AnnotationMethodManager.get(annotationType, MESSAGE) == null) {
			log.warn("The annotation [{}] must have a method named [message] that returns a string.", annotationType.getName());
			return false;
		}

		if (AnnotationMethodManager.get(annotationType, CONDITION) == null) {
			log.warn("The annotation [{}] must have a method named [condition] that returns a string.", annotationType.getName());
			return false;
		}

		if (AnnotationMethodManager.get(annotationType, GROUP) == null) {
			log.warn("The annotation [{}] must have a method named [group] that returns a Array<String>.", annotationType.getName());
			return false;
		}

		return true;
	}

	/**
	 * 判断组别是否匹配
	 */
	@SuppressWarnings("RedundantIfStatement")
	private static boolean matchGroup(@NotNull Set<Object> validateGroups, @NotNull Set<Object> annoGroups) {
		if (validateGroups.isEmpty()) {
			return true;
		}
		if (validateGroups.stream().anyMatch(annoGroups::contains)) {
			return true;
		}
		return false;
	}

	/**
	 * 解析组别信息
	 */
	@NotNull
	private static Set<Object> parseGroups(@NotNull Annotation annotation, @NotNull Object value) {
		String[] groups = getAnnotationValue(annotation, GROUP);
		Set<Object> parsedGroups = new HashSet<>();
		for (String group : groups) {
			parsedGroups.add(SpelParser.parse(group, value, Object.class));
		}
		return parsedGroups;
	}

	/**
	 * 自动填充校验结果的错误信息
	 */
	private static void autoFillValidResult(@NotNull FieldValidResult result, @NotNull Annotation annotation, @NotNull Field verifiedField) {
		if (!result.isSuccess()) {
			// 错误信息收集
			if (result.getMessage().isEmpty()) {
				String message = getAnnotationValue(annotation, MESSAGE);
				result.setMessage(message);
			}
			if (result.getFieldName().isEmpty()) {
				result.setFieldName(verifiedField.getName());
			}
		}
	}

	/**
	 * 校验器实例管理器，避免重复创建校验器实例。
	 */
	private static final ConcurrentHashMap<Class<? extends SpelConstraintValidator<?>>, SpelConstraintValidator<?>> VALIDATOR_INSTANCE_CACHE = new ConcurrentHashMap<>();

	/**
	 * 获取校验器实例，当实例不存在时会创建一个新的实例。
	 */
	@NotNull
	private static <T extends SpelConstraintValidator<?>> SpelConstraintValidator<?> getValidatorInstance(@NotNull Class<T> validator) {
		return VALIDATOR_INSTANCE_CACHE.computeIfAbsent(validator, key -> {
			try {
				return key.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new SpelValidException("Create validator [" + validator.getName() + "] instance error: " + e.getMessage(), e);
			}
		});
	}

	/**
	 * 获取注解方法的属性值
	 *
	 * @param annotation 注解对象
	 * @param methodName 方法名
	 * @param <T>        返回值类型
	 * @return 属性值
	 */
	private static <T> T getAnnotationValue(@NotNull Annotation annotation, @NotNull String methodName) {
		Method method = AnnotationMethodManager.get(annotation.annotationType(), methodName);
		try {
			//noinspection unchecked
			return (T) method.invoke(annotation);
		} catch (Exception e) {
			throw new SpelValidException("Get method [" + annotation.annotationType().getName() + "." + methodName + "] error: " + e.getMessage(), e);
		}
	}

}
