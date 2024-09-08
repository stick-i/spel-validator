package cn.sticki.validator.spel.util;

import cn.sticki.validator.spel.exception.SpelArgumentException;

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
            if (val instanceof BigDecimal) {
                return (BigDecimal) val;
            }else if (val instanceof Double) {
                return BigDecimal.valueOf((Double) val);
            } else if (val instanceof Float) {
                return BigDecimal.valueOf((Float) val);
            } else {
                return new BigDecimal(String.valueOf(val));
            }
        } catch (NumberFormatException e) {
            // 如果转换失败
            throw new SpelArgumentException("Value [" + val + "] can not convert to BigDecimal.");
        }
    }

}
