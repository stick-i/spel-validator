package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelSize;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.util.CalcLengthUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * {@link SpelSize} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelSizeValidator implements SpelConstraintValidator<SpelSize> {

    @Override
    public FieldValidResult isValid(SpelSize annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        // 元素为null是被允许的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }

        // 计算字段内容的长度
        int size = CalcLengthUtil.calcFieldSize(fieldValue);

        // 计算表达式的值
        Integer min = SpelParser.parse(annotation.min(), obj, Integer.class);
        Integer max = SpelParser.parse(annotation.max(), obj, Integer.class);

        if (size < min || size > max) {
            return FieldValidResult.of(false,  min, max);
        }

        return FieldValidResult.success();
    }

    @Override
    public Set<Class<?>> supportType() {
        return CalcLengthUtil.SUPPORT_TYPE;
    }

}
