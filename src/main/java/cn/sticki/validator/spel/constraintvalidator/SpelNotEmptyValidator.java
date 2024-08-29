package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelNotEmpty;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.util.CalcLengthUtil;

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
            return new FieldValidResult(false);
        }
        int size = CalcLengthUtil.calcFieldSize(object);
        return new FieldValidResult(size > 0);
    }

    @Override
    public Set<Class<?>> supportType() {
        return CalcLengthUtil.SUPPORT_TYPE;
    }

}
