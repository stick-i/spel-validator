package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelMin;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.util.BigDecimalUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * {@link SpelMin} 注解校验器。
 *
 * @author oddfar
 * @version 1.0
 * @since 2024/8/25
 */
public class SpelMinValidator implements SpelConstraintValidator<SpelMin> {

    @Override
    public FieldValidResult isValid(SpelMin annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        // 元素为null是被允许的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        BigDecimal bigFieldValue = BigDecimalUtil.valueOf(fieldValue);

        // 计算表达式的值
        Object value = SpelParser.parse(annotation.value(), obj, Object.class);
        BigDecimal valueBigDecimal = BigDecimalUtil.valueOf(value);

        if (bigFieldValue.compareTo(valueBigDecimal) < 0) {
            // 构建错误信息
            String message = annotation.message();
            message = message.replace("{value}", String.valueOf(value));
            return new FieldValidResult(false, message);
        }

        return FieldValidResult.success();
    }

}
