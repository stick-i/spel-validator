package cn.sticki.spel.validator.core.util;

import java.math.BigDecimal;
import java.util.OptionalInt;

/**
 * 数值类比较工具
 * 参考 {@link org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper}
 * 在此基础上拓展比较
 *
 * @author oddfar
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "JavadocReference"})
public class NumberComparatorUtil {

    private NumberComparatorUtil() {
    }

    public static final OptionalInt LESS_THAN = OptionalInt.of(-1);

    public static final OptionalInt FINITE_VALUE = OptionalInt.empty();

    public static final OptionalInt GREATER_THAN = OptionalInt.of(1);

    public static int compare(Number number, Number value, OptionalInt treatNanAs) {
        if (number == null || value == null || treatNanAs == null || !treatNanAs.isPresent()) {
            throw new IllegalArgumentException("[Number], [Value] and [TreatNanAs] must not be null.");
        }
        if (number.equals(value)) {
            return 0;
        }
        boolean numberIsDouble = number instanceof Double || number instanceof Float;
        boolean valueIsDouble = value instanceof Double || value instanceof Float;

        if (numberIsDouble && valueIsDouble) {
            return compare(number.doubleValue(), value.doubleValue());
        }
        if (numberIsDouble) {
            return compare(number.doubleValue(), value, treatNanAs);
        }
        if (valueIsDouble) {
            return compare(number, value.doubleValue(), treatNanAs);
        }

        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    private static int compare(Double number, double value) {
        return number.compareTo(value);
    }

    private static int compare(Number number, Double value, OptionalInt treatNanAs) {
        // 检查的是 value，所以需要反转
        //noinspection OptionalGetWithoutIsPresent
        treatNanAs = OptionalInt.of(-treatNanAs.getAsInt());
        OptionalInt infinity = infinityCheck(value, treatNanAs);
        if (infinity.isPresent()) {
            // 这里也要反转
            return -infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimal.valueOf(value));
    }

    private static int compare(Double number, Number value, OptionalInt treatNanAs) {
        OptionalInt infinity = infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    private static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number == Double.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number == Double.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }

}
