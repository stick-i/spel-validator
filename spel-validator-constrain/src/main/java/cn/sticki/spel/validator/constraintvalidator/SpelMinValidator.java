package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelMin;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.util.NumberComparatorUtil;

import java.lang.reflect.Field;

/**
 * {@link SpelMin} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/9/29
 */
public class SpelMinValidator extends AbstractSpelNumberCompareValidator<SpelMin> {

    @Override
    protected boolean compare(Number fieldValue, Number compareValue) {
        return NumberComparatorUtil.compare(fieldValue, compareValue, NumberComparatorUtil.LESS_THAN) >= 0;
    }

    @Override
    public FieldValidResult isValid(SpelMin annotation, Object obj, Field field) throws IllegalAccessException {
        return super.isValid((Number) field.get(obj), annotation.value(), annotation.message(), obj);
    }

}
