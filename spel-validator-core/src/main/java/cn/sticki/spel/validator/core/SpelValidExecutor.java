package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.exception.SpelNotSupportedTypeException;
import cn.sticki.spel.validator.core.exception.SpelValidatorException;
import cn.sticki.spel.validator.core.manager.AnnotationMethodManager;
import cn.sticki.spel.validator.core.manager.ValidatorInstanceManager;
import cn.sticki.spel.validator.core.message.ValidatorMessageInterpolator;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.result.ObjectValidResult;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spel 相关注解的执行器，对使用了 {@link SpelConstraint} 进行标记的注解执行校验。
 *
 * @author 阿杆
 * @version 2.0
 * @see SpelConstraint
 * @since 2024/4/29
 */
@Slf4j
public class SpelValidExecutor {

    private SpelValidExecutor() {
    }

    private static final String MESSAGE = "message";

    private static final String CONDITION = "condition";

    private static final String GROUP = "group";

    /**
     * 字段缓存，仅缓存包含 Spel 约束注解的字段
     */
    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 字段注解缓存
     */
    private static final ConcurrentHashMap<Field, List<Annotation>> FIELD_ANNOTATION_CACHE = new ConcurrentHashMap<>();

    private static final ValidatorMessageInterpolator MESSAGE_INTERPOLATOR = new ValidatorMessageInterpolator();

    /**
     * @see #validateObject(Object, String[], SpelValidContext)
     */
    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject) {
        return validateObject(verifiedObject, (String[]) null);
    }

    /**
     * @see #validateObject(Object, String[], SpelValidContext)
     */
    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, String... groups) {
        return validateObject(verifiedObject, groups, null);
    }

    /**
     * 验证对象
     * <p>
     * 如果对象中有任意使用了 spel 约束注解的字段，则会对该字段进行校验。
     *
     * @param verifiedObject 被校验的对象
     * @param groups         分组信息，被作为spel表达式进行解析
     * @param context        上下文信息
     * @return 对象校验结果
     */
    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, String[] groups, SpelValidContext context) {
        groups = groups == null ? new String[0] : groups;
        context = context == null ? SpelValidContext.getDefault() : context;
        return validateObject(verifiedObject, parseGroups(verifiedObject, groups), context);
    }

    /**
     * 验证对象
     * <p>
     * 如果对象中有任意使用了 spel 约束注解的字段，则会对该字段进行校验。
     *
     * @param verifiedObject 被校验的对象
     * @param validateGroups 分组信息，只有同组的注解才会被校验
     * @param context        上下文信息
     * @return 对象校验结果
     */
    @NotNull
    public static ObjectValidResult validateObject(@NotNull Object verifiedObject, @NotNull Set<Object> validateGroups, @NotNull SpelValidContext context) {
        Objects.requireNonNull(verifiedObject);
        Objects.requireNonNull(validateGroups);
        Objects.requireNonNull(context);

        long startTime = System.nanoTime();
        log.debug("Spel validate start, class [{}], groups [{}], context [{}]", verifiedObject.getClass().getName(), validateGroups, context);
        log.debug("Verified object [{}]", verifiedObject);

        ObjectValidResult validResult = new ObjectValidResult();

        // 获取字段
        List<Field> spelConstraintFields = getSpelConstraintFields(verifiedObject.getClass());
        for (Field field : spelConstraintFields) {
            // 获取注解
            List<Annotation> spelConstraintAnnotations = getSpelConstraintAnnotations(field);
            for (Annotation annotation : spelConstraintAnnotations) {
                // 获取验证器实例
                SpelConstraintValidator<? extends Annotation> validator = ValidatorInstanceManager.getInstance(annotation);
                // 执行校验
                FieldValidResult validationResult = validateFieldAnnotation(annotation, validator, verifiedObject, field, validateGroups, context);
                if (validationResult != null) {
                    validResult.addFieldResult(validationResult);
                }
            }
        }

        log.debug("Spel validate over,error list {}", validResult.getErrors());
        log.debug("Spel validate cost time {} ms", (System.nanoTime() - startTime) / 1000000);
        return validResult;
    }

    /**
     * 获取包含 Spel 约束注解的字段列表
     */
    @NotNull
    private static List<Field> getSpelConstraintFields(@NotNull Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, aClass -> {
            List<Field> list = new ArrayList<>();
            while (aClass != null) {
                Field[] fields = aClass.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (!getSpelConstraintAnnotations(field).isEmpty()) {
                        list.add(field);
                    }
                }
                // 获取父类，继续处理
                aClass = aClass.getSuperclass();
            }
            return Collections.unmodifiableList(list);
        });
    }

    /**
     * 获取字段上符合 Spel约束规范 的注解
     *
     * @param field 字段
     * @return 注解列表
     */
    @NotNull
    private static List<Annotation> getSpelConstraintAnnotations(@NotNull Field field) {
        return FIELD_ANNOTATION_CACHE.computeIfAbsent(field, f -> {
            Annotation[] annotations = f.getAnnotations();
            List<Annotation> tempList = new ArrayList<>();

            for (Annotation originalAnno : annotations) {
                String annoName = originalAnno.annotationType().getName();
                if (annoName.endsWith("$List") || annoName.endsWith("Container")) {
                    // 容器注解，需要获取容器内部的注解类型，其声明类为真实的注解类
                    //noinspection unchecked
                    Class<? extends Annotation> clazz = (Class<? extends Annotation>) originalAnno.annotationType().getDeclaringClass();
                    Annotation[] originalAnnoArray = f.getAnnotationsByType(clazz);
                    tempList.addAll(Arrays.asList(originalAnnoArray));
                } else {
                    tempList.add(originalAnno);
                }
            }

            // 验证注解的合法性，移除不合法的注解
            tempList.removeIf(annotation -> !isSpelConstraintAnnotation(annotation.annotationType()));

            return Collections.unmodifiableList(tempList);
        });
    }

    /**
     * 对任意的注解执行校验，当注解不是合法的约束注解时，将返回null。
     *
     * @param annotation     注解数组，数组内的注解必须为同一类型
     * @param verifiedObject 被校验的对象
     * @param verifiedField  被校验的字段，必须存在于被校验的对象中
     * @param validateGroups 分组信息
     * @return 校验结果
     */
    private static @Nullable FieldValidResult validateFieldAnnotation(
            @NotNull Annotation annotation,
            @NotNull SpelConstraintValidator<? extends Annotation> validator,
            @NotNull Object verifiedObject,
            @NotNull Field verifiedField,
            @NotNull Set<Object> validateGroups,
            @NotNull SpelValidContext context
    ) {
        log.debug("===> Find target annotation [{}], verifiedField [{}]", annotation.annotationType().getSimpleName(), verifiedField.getName());
        log.debug("===> Annotation object [{}]", annotation);

        // 判断字段的类型是否受支持
        Set<Class<?>> supported = validator.supportType();
        Class<?> verifiedFieldClass = verifiedField.getType();
        if (supported.stream().noneMatch(clazz -> clazz.isAssignableFrom(verifiedFieldClass))) {
            log.error("===> Object type not supported, skip validate. Current type[{}], supported types [{}]", verifiedFieldClass, supported);
            throw new SpelNotSupportedTypeException(verifiedFieldClass, supported);
        }

        // 匹配分组
        Set<Object> annoGroups = parseGroups(verifiedObject, getAnnotationValue(annotation, GROUP));
        if (!matchGroup(validateGroups, annoGroups)) {
            log.debug("===> Group not matched, skip validate. annotation groups [{}]", annoGroups);
            return null;
        }

        // 判断condition条件是否成立
        @Language("spel") String condition = getAnnotationValue(annotation, CONDITION);
        if (!condition.isEmpty() && !SpelParser.parse(condition, verifiedObject, Boolean.class)) {
            log.debug("===> Condition not valid, skip validate. condition [{}]", condition);
            return null;
        }

        // 执行校验
        FieldValidResult validationResult = doValidate(validator, annotation, verifiedObject, verifiedField);
        fillValidResult(validationResult, annotation, verifiedField, context.getLocale());
        log.debug("===> Validate result [{}]", validationResult.isSuccess());
        return validationResult;
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
        try {
            // noinspection unchecked
            return ((SpelConstraintValidator<A>) validator).isValid(annotation, verifiedObject, verifiedField);
        } catch (SpelValidatorException e) {
            log.error("Spel validate error: {}; Located in the annotation [{}] of class [{}] field [{}]",
                    e.getMessage(), annotation.annotationType().getName(), verifiedObject.getClass().getName(), verifiedField.getName());
            throw e;
        } catch (IllegalAccessException e) {
            // 被验证的字段在类中无法访问
            log.error("The validated field [{}] is not accessible in the class [{}]",
                    verifiedField.getName(), verifiedObject.getClass().getName());
            throw new SpelValidatorException("Failed to access field value", e);
        }
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
     *
     * @param validateGroups 需要匹配的组别
     * @param annoGroups     注解所属的组别
     */
    private static boolean matchGroup(@NotNull Set<Object> validateGroups, @NotNull Set<Object> annoGroups) {
        if (annoGroups.isEmpty() || validateGroups.isEmpty()) {
            return true;
        }
        return validateGroups.stream().anyMatch(annoGroups::contains);
    }

    /**
     * 通过 Spel 表达式解析分组信息
     *
     * @param object 解析时的根对象
     * @param groups 待解析的分组信息
     * @return 解析后的结果
     */
    @NotNull
    public static Set<Object> parseGroups(@NotNull Object object, @NotNull String... groups) {
        Objects.requireNonNull(object);
        Objects.requireNonNull(groups);
        Set<Object> parsedGroups = new HashSet<>();
        for (@Language("spel") String group : groups) {
            parsedGroups.add(SpelParser.parse(group, object));
        }
        return parsedGroups;
    }

    /**
     * 填充校验结果的错误信息
     */
    private static void fillValidResult(@NotNull FieldValidResult result, @NotNull Annotation annotation, @NotNull Field verifiedField, Locale locale) {
        if (result.isSuccess()) {
            return;
        }
        // 获取错误信息
        String message = result.getMessage().isEmpty() ? getAnnotationValue(annotation, MESSAGE) : result.getMessage();
        String interpolateMessage = MESSAGE_INTERPOLATOR.interpolate(message, locale, result.getArgs());
        result.setMessage(interpolateMessage);
        if (result.getFieldName().isEmpty()) {
            result.setFieldName(verifiedField.getName());
        }
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
            throw new SpelValidatorException("Get method [" + annotation.annotationType().getName() + "." + methodName + "] error: " + e.getMessage(), e);
        }
    }

}
