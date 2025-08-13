package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelDigits;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.exception.SpelParserException;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * {@link SpelDigits} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/8/10
 */
public class SpelDigitsValidator implements SpelConstraintValidator<SpelDigits> {

    @Override
    public FieldValidResult isValid(SpelDigits annotation, Object obj, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(obj);

        // 元素为null是被允许的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }

        // 计算整数位数限制
        Number integerValue = SpelParser.parse(annotation.integer(), obj, Number.class);
        int maxIntegerLength = integerValue.intValue();
        if (maxIntegerLength < 0) {
            throw new SpelParserException("Integer length must be non-negative, but got: " + maxIntegerLength);
        }

        // 计算小数位数限制
        Number fractionValue = SpelParser.parse(annotation.fraction(), obj, Number.class);
        int maxFractionLength = fractionValue.intValue();
        if (maxFractionLength < 0) {
            throw new SpelParserException("Fraction length must be non-negative, but got: " + maxFractionLength);
        }

        // 处理CharSequence类型
        Number numberValue;
        if (fieldValue instanceof CharSequence) {
            String stringValue = fieldValue.toString();
            try {
                // 尝试将字符串转换为BigDecimal
                numberValue = new BigDecimal(stringValue);
            } catch (NumberFormatException e) {
                // 如果不是有效的数字格式，返回验证失败
                return FieldValidResult.of(false, maxIntegerLength, maxFractionLength);
            }
        } else {
            numberValue = (Number) fieldValue;
        }

        // 转换为BigDecimal进行精确的位数计算
        BigDecimal bigDecimalValue;
        if (numberValue instanceof BigDecimal) {
            bigDecimalValue = (BigDecimal) numberValue;
        } else {
            bigDecimalValue = new BigDecimal(numberValue.toString());
        }

        // 获取整数部分和小数部分的位数
        String plainString = bigDecimalValue.stripTrailingZeros().toPlainString();

        // 处理负数
        if (plainString.startsWith("-")) {
            plainString = plainString.substring(1);
        }

        int actualIntegerLength;
        int actualFractionLength;

        int dotIndex = plainString.indexOf('.');
        if (dotIndex == -1) {
            // 没有小数点，全是整数
            actualIntegerLength = plainString.length();
            actualFractionLength = 0;
        } else {
            // 有小数点
            actualIntegerLength = dotIndex;
            actualFractionLength = plainString.length() - dotIndex - 1;
        }

        // 特殊处理：如果整数部分是"0"，则整数位数为1
        if (actualIntegerLength == 0 || (actualIntegerLength == 1 && plainString.startsWith("0"))) {
            actualIntegerLength = 1;
        }

        // 验证位数
        if (actualIntegerLength > maxIntegerLength || actualFractionLength > maxFractionLength) {
            return FieldValidResult.of(false, maxIntegerLength, maxFractionLength);
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
        hashSet.add(CharSequence.class);
        SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}