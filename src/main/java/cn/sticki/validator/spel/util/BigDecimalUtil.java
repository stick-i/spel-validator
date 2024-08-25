package cn.sticki.validator.spel.util;

import java.math.BigDecimal;

/**
 * BigDecimal工具
 *
 * @author oddfar
 * @since 2024/8/25
 */
public class BigDecimalUtil {

    public static BigDecimal valueOf(Object val) {
        try {
            if (val instanceof Double) {
                return BigDecimal.valueOf((Double) val);
            } else if (val instanceof Float) {
                return BigDecimal.valueOf((Float) val);
            } else {
                return new BigDecimal(String.valueOf(val));
            }
        } catch (Exception e) {
            // 如果转换失败
            throw new IllegalArgumentException("无法将标记的元素值转换为数值类型");
        }
    }

}
