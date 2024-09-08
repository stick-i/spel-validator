package cn.sticki.validator.spel.util;

import java.math.BigDecimal;
import java.util.OptionalInt;

/**
 * 数值类比较工具
 * 参考`org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper`
 * 在此基础上拓展比较
 *
 * @author oddfar
 */
public final class NumberComparatorUtil {

    public static final OptionalInt LESS_THAN = OptionalInt.of(-1);

    public static final OptionalInt FINITE_VALUE = OptionalInt.empty();

    public static final OptionalInt GREATER_THAN = OptionalInt.of(1);

    public static int compare(Double number, double value) {
        return number.compareTo(value);
    }

    public static int compare(Float number, float value) {
        return number.compareTo(value);
    }

    public static int compare(Object number, Float value, OptionalInt treatNanAs) {
        OptionalInt infinity = infinityCheck(value, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimal.valueOf(value));
    }

    public static int compare(Object number, Double value, OptionalInt treatNanAs) {
        OptionalInt infinity = infinityCheck(value, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimal.valueOf(value));
    }

    public static int compare(Double number, Object value, OptionalInt treatNanAs) {
        OptionalInt infinity = infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    public static int compare(Float number, Object value, OptionalInt treatNanAs) {
        OptionalInt infinity = infinityCheck(number, treatNanAs);
        if (infinity.isPresent()) {
            return infinity.getAsInt();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    public static int compare(Object number, Object value, OptionalInt treatNanAs) {
        if (number instanceof Double && value instanceof Double) {
            return compare((Double) number, (Double) value);
        }
        if (number instanceof Float && value instanceof Float) {
            return compare((Float) number, (Float) value);
        }
        if (number instanceof Float && !(value instanceof Float || value instanceof Double)) {
            return compare((Float) number, value, treatNanAs);
        }
        if (number instanceof Double && !(value instanceof Float || value instanceof Double)) {
            return compare((Double) number, value, treatNanAs);
        }
        if (!(number instanceof Float || number instanceof Double) && value instanceof Float) {
            return compare(number, (Float) value, treatNanAs);
        }
        if (!(number instanceof Float || number instanceof Double) && value instanceof Double) {
            return compare(number, (Double) value, treatNanAs);
        }

        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    public static OptionalInt infinityCheck(Double number, OptionalInt treatNanAs) {
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

    public static OptionalInt infinityCheck(Float number, OptionalInt treatNanAs) {
        OptionalInt result = FINITE_VALUE;
        if (number == Float.NEGATIVE_INFINITY) {
            result = LESS_THAN;
        } else if (number.isNaN()) {
            result = treatNanAs;
        } else if (number == Float.POSITIVE_INFINITY) {
            result = GREATER_THAN;
        }
        return result;
    }

}
