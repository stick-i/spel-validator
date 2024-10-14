package cn.sticki.spel.validator.core.constraintvalidator;

import cn.sticki.spel.validator.core.constrain.SpelMax;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.util.NumberComparatorUtil;

import java.lang.reflect.Field;

/**
 * {@link SpelMax} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/9/29
 */
public class SpelMaxValidator extends AbstractSpelNumberCompareValidator<SpelMax> {

    @Override
    protected boolean compare(Number fieldValue, Number compareValue) {
        return NumberComparatorUtil.compare(fieldValue, compareValue, NumberComparatorUtil.GREATER_THAN) <= 0;
    }

    @Override
    public FieldValidResult isValid(SpelMax annotation, Object obj, Field field) throws IllegalAccessException {
        return super.isValid((Number) field.get(obj), annotation.value(), annotation.message(), obj);
    }

}
