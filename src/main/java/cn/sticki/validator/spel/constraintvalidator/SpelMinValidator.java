package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelMin;
import cn.sticki.validator.spel.exception.SpelParserException;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.util.NumberComparatorUtil;

import java.lang.reflect.Field;
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
        // 计算表达式的值，基本数据类型会自动装箱
        Object minValue = SpelParser.parse(spelMin.value(), obj);
        if (!(minValue instanceof Number)) {
            throw new SpelParserException("Expression [" + spelMin.value() + "] calculate result must be Number.");
        }
        // 比较大小，其中一个是Not-a-Number (NaN）默认失败
        if (NumberComparatorUtil.compare(fieldValue, minValue, NumberComparatorUtil.LESS_THAN) < 0) {
            // todo 目前对Double的边界值处理不太友好，message的展示类似为：不能小于等于 NaN。后续考虑去掉对Double Float类型的支持，或者对边界值抛出异常。
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
