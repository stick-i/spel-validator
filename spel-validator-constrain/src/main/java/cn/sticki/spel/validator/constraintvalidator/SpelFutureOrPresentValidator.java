package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelFutureOrPresent;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * {@link SpelFutureOrPresent} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
public class SpelFutureOrPresentValidator extends AbstractSpelTemporalValidator<SpelFutureOrPresent> {

    @Override
    public FieldValidResult isValid(SpelFutureOrPresent annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        return super.isValid(fieldValue);
    }

    @Override
    protected boolean isValidTemporal(Object temporal) {
        Object now = getNow(temporal);
        return compareTemporal(temporal, now) >= 0;
    }
}
