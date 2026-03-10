package cn.sticki.spel.validator.core.util;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 数值类比较工具
 * 参考 {@link org.hibernate.validator.internal.constraintvalidators.bv.number.bound.NumberComparatorHelper}
 * 在此基础上拓展比较
 *
 * @author oddfar
 */
@SuppressWarnings("JavadocReference")
public class NumberComparatorUtil {

    private NumberComparatorUtil() {
    }

    @Getter
    public enum CompareResult {
        LESS_THAN(-1),
        FINITE_VALUE(0),
        GREATER_THAN(1);

        private final int value;

        CompareResult(int value) {
            this.value = value;
        }

        public CompareResult negate() {
            if (this == LESS_THAN) {
                return GREATER_THAN;
            }
            if (this == GREATER_THAN) {
                return LESS_THAN;
            }
            return FINITE_VALUE;
        }
    }

    public static final CompareResult LESS_THAN = CompareResult.LESS_THAN;

    public static final CompareResult FINITE_VALUE = CompareResult.FINITE_VALUE;

    public static final CompareResult GREATER_THAN = CompareResult.GREATER_THAN;

    public static int compare(Number number, Number value, CompareResult treatNanAs) {
        if (number == null || value == null || treatNanAs == null) {
            throw new IllegalArgumentException("[Number], [Value] and [TreatNanAs] must not be null.");
        }
        if (treatNanAs == CompareResult.FINITE_VALUE) {
            throw new IllegalArgumentException("[TreatNanAs] must not be FINITE_VALUE.");
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

    private static int compare(Number number, Double value, CompareResult treatNanAs) {
        // 检查的是 value，所以需要反转
        CompareResult negatedTreatNanAs = treatNanAs.negate();
        CompareResult infinity = infinityCheck(value, negatedTreatNanAs);
        if (infinity != FINITE_VALUE) {
            // 这里也要反转
            return -infinity.getValue();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimal.valueOf(value));
    }

    private static int compare(Double number, Number value, CompareResult treatNanAs) {
        CompareResult infinity = infinityCheck(number, treatNanAs);
        if (infinity != FINITE_VALUE) {
            return infinity.getValue();
        }
        return BigDecimalUtil.valueOf(number).compareTo(BigDecimalUtil.valueOf(value));
    }

    private static CompareResult infinityCheck(Double number, CompareResult treatNanAs) {
        CompareResult result = FINITE_VALUE;
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
