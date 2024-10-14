package cn.sticki.spel.validator.core.constraintvalidator;

import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.constrain.SpelNotNull;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * {@link SpelNotNull} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
public class SpelNotNullValidator implements SpelConstraintValidator<SpelNotNull> {

    @Override
    public FieldValidResult isValid(SpelNotNull annotation, Object obj, Field field) throws IllegalAccessException {
        return new FieldValidResult(field.get(obj) != null);
    }

}
