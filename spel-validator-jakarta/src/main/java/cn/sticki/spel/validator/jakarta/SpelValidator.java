package cn.sticki.spel.validator.jakarta;

import cn.sticki.spel.validator.core.SpelValidExecutor;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldError;
import cn.sticki.spel.validator.core.result.ObjectValidResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link SpelValid} 的实际校验器
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/11
 */
@Slf4j
public class SpelValidator implements ConstraintValidator<SpelValid, Object> {

    private SpelValid spelValid;

    @Override
    public void initialize(SpelValid constraintAnnotation) {
        this.spelValid = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // 表达式不为空且计算结果为 false，跳过校验
        if (!spelValid.condition().isEmpty() && !SpelParser.parse(spelValid.condition(), value, Boolean.class)) {
            log.debug("SpelValid condition is not satisfied, skip validation, condition: {}", spelValid.condition());
            return true;
        }

        // 获取分组信息
        Set<Object> groups = Arrays.stream(spelValid.spelGroups()).map(it -> SpelParser.parse(it, value)).collect(Collectors.toSet());

        // 校验对象
        ObjectValidResult validateObjectResult = SpelValidExecutor.validateObject(value, groups);

        // 构建错误信息
        buildConstraintViolation(validateObjectResult, context);
        return validateObjectResult.noneError();
    }

    /**
     * 生成错误信息并将其添加到验证上下文
     */
    private void buildConstraintViolation(ObjectValidResult validateObjectResult, ConstraintValidatorContext context) {
        if (validateObjectResult.noneError()) {
            return;
        }
        context.disableDefaultConstraintViolation();
        for (FieldError error : validateObjectResult.getErrors()) {
            context.buildConstraintViolationWithTemplate(error.getErrorMessage()).addPropertyNode(error.getFieldName()).addConstraintViolation();
        }
    }

}
