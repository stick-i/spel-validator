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
    protected boolean compare(SpelMin anno, Number fieldValue, Number compareValue) {
        int compareResult = NumberComparatorUtil.compare(fieldValue, compareValue, NumberComparatorUtil.LESS_THAN);
        return anno.inclusive() ? compareResult >= 0 : compareResult > 0;
    }

    @Override
    public FieldValidResult isValid(SpelMin annotation, Object obj, Field field) throws IllegalAccessException {
        Number fieldValue = (Number) field.get(obj);
        return super.isValid(annotation, fieldValue, annotation.value(), obj);
    }

}
