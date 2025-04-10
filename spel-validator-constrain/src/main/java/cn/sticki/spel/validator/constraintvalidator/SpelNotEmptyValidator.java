package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelNotEmpty;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.util.CalcLengthUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * {@link SpelNotEmpty} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNotEmptyValidator implements SpelConstraintValidator<SpelNotEmpty> {

    @Override
    public FieldValidResult isValid(SpelNotEmpty annotation, Object obj, Field field) throws IllegalAccessException {
        Object object = field.get(obj);
        if (object == null) {
            return FieldValidResult.of(false);
        }
        int size = CalcLengthUtil.calcFieldSize(object);
        return FieldValidResult.of(size > 0);
    }

    @Override
    public Set<Class<?>> supportType() {
        return CalcLengthUtil.SUPPORT_TYPE;
    }

}
