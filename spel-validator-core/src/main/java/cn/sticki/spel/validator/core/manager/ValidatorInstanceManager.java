package cn.sticki.spel.validator.core.manager;

import cn.sticki.spel.validator.core.SpelConstraint;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.exception.SpelValidatorException;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证器实例管理器
 *
 * @author 阿杆
 * @since 2024/11/4
 */
public class ValidatorInstanceManager {

    private ValidatorInstanceManager() {
    }

    /**
     * 校验器实例管理器，避免重复创建校验器实例。
     */
    private static final ConcurrentHashMap<Annotation, SpelConstraintValidator<?>> VALIDATOR_INSTANCE_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取校验器实例，当实例不存在时会创建一个新的实例。
     */
    @NotNull
    public static SpelConstraintValidator<? extends Annotation> getInstance(@NotNull Annotation annotation) {
        return VALIDATOR_INSTANCE_CACHE.computeIfAbsent(annotation, key -> {
            try {
                Class<? extends Annotation> annoClazz = annotation.annotationType();
                SpelConstraint constraint = annoClazz.getAnnotation(SpelConstraint.class);
                if (constraint == null) {
                    throw new SpelValidatorException("Annotation [" + annoClazz.getName() + "] is not a Spel Constraint annotation");
                }
                Class<? extends SpelConstraintValidator<?>> validatorClass = constraint.validatedBy();
                return validatorClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new SpelValidatorException("Failed to create validator instance, annotation [" + annotation.annotationType().getName() + "]", e);
            }
        });
    }

}
