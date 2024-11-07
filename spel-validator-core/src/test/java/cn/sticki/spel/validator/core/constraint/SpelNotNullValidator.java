package cn.sticki.spel.validator.core.constraint;

import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * Test
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
public class SpelNotNullValidator implements SpelConstraintValidator<SpelNotNullTest> {

    private SpelNotNullValidator() {
    }

    @Override
    public FieldValidResult isValid(SpelNotNullTest annotation, Object obj, Field field) throws IllegalAccessException {
        return new FieldValidResult(field.get(obj) != null);
    }

}
