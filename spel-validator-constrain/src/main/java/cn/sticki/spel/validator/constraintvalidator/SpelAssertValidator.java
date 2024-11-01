package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelAssert;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.exception.SpelArgumentException;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * {@link SpelAssert} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
public class SpelAssertValidator implements SpelConstraintValidator<SpelAssert> {

    @Override
    public FieldValidResult isValid(SpelAssert annotation, Object obj, Field field) {
        if (annotation.assertTrue().isEmpty()) {
            throw new SpelArgumentException("assertTrue must not be empty");
        }

        return new FieldValidResult(SpelParser.parse(annotation.assertTrue(), obj, Boolean.class));
    }

}