package cn.sticki.spel.validator.jakarta;

import cn.sticki.spel.validator.core.SpelValidExecutor;
import cn.sticki.spel.validator.core.result.FieldError;
import cn.sticki.spel.validator.core.result.ObjectValidResult;
import cn.sticki.spel.validator.test.util.AbstractSpelValidator;
import cn.sticki.spel.validator.test.util.VerifyObject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测试验证工具类
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/13
 */
@Slf4j
public class JakartaSpelValidator extends AbstractSpelValidator {

    private static final JakartaSpelValidator INSTANCE = new JakartaSpelValidator();

    @SuppressWarnings("resource")
    private static final Validator validator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory().getValidator();

    /**
     * 验证约束结果是否符合预期
     */
    public static boolean check(List<VerifyObject> verifyObjectList) {
        return INSTANCE.checkConstraintResult(verifyObjectList);
    }

    /**
     * 参数校验
     * <p>
     * 调用此方法会触发约束校验
     *
     * @return 校验结果
     */
    @Override
    public ObjectValidResult validate(Object obj, String[] spelGroups) {
        // 如果对象没有使用 SpelValid 注解，则直接调用验证执行器进行验证
        // 这种情况下，只会验证本框架提供的约束注解
        if (!obj.getClass().isAnnotationPresent(SpelValid.class)) {
            return SpelValidExecutor.validateObject(obj, spelGroups);
        }

        // 通过 @Valid 的方式进行验证
        Set<ConstraintViolation<Object>> validate = validator.validate(obj);
        if (validate == null || validate.isEmpty()) {
            return ObjectValidResult.EMPTY;
        }
        ObjectValidResult validResult = new ObjectValidResult();
        List<FieldError> list = validate.stream().map(JakartaSpelValidator::convert).collect(Collectors.toList());
        validResult.addFieldError(list);
        return validResult;
    }

    private static FieldError convert(ConstraintViolation<Object> violation) {
        return FieldError.of(violation.getPropertyPath().toString(), violation.getMessage());
    }

}
