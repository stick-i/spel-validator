package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelNull;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * {@link SpelNull} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNullValidator implements SpelConstraintValidator<SpelNull> {

    @Override
    public FieldValidResult isValid(SpelNull annotation, Object obj, Field field) throws IllegalAccessException {
        return new FieldValidResult(field.get(obj) == null);
    }

}
