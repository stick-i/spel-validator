package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelMin;
import cn.sticki.validator.spel.exception.SpelParserException;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.util.BigDecimalUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link SpelMin} 注解校验器。
 *
 * @author oddfar
 * @version 1.0
 * @since 2024/8/25
 */
public class SpelMinValidator implements SpelConstraintValidator<SpelMin> {

    @Override
    public FieldValidResult isValid(SpelMin spelMin, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);
        // 元素为null是被允许的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        BigDecimal fieldValueBigDecimal = BigDecimalUtil.valueOf(fieldValue);

        // 计算表达式的值
        Object minValue = SpelParser.parse(spelMin.value(), obj);
        if (!(minValue instanceof Number) && !(minValue instanceof CharSequence)) {
            throw new SpelParserException("Expression [" + spelMin.value() + "] calculate result must be Number or CharSequence.");
        }
        BigDecimal minValueBigDecimal = BigDecimalUtil.valueOf(minValue);

        // 比较大小
        if (fieldValueBigDecimal.compareTo(minValueBigDecimal) < 0) {
            // 构建错误信息
            String message = spelMin.message();
            message = message.replace("{value}", String.valueOf(minValue));
            return new FieldValidResult(false, message);
        }

        return FieldValidResult.success();
    }

    static final Set<Class<?>> SUPPORT_TYPE;

    static {
        HashSet<Class<?>> hashSet = new HashSet<>();
        hashSet.add(CharSequence.class);
        hashSet.add(Number.class);
        hashSet.add(int.class);
        hashSet.add(long.class);
        hashSet.add(float.class);
        hashSet.add(double.class);
        hashSet.add(short.class);
        hashSet.add(byte.class);
        SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}
