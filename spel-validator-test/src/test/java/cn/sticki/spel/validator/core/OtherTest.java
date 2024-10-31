package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.constrain.SpelAssert;
import cn.sticki.spel.validator.core.exception.SpelArgumentException;
import cn.sticki.spel.validator.core.exception.SpelParserException;
import cn.sticki.spel.validator.core.manager.AnnotationMethodManager;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import cn.sticki.spel.validator.core.result.ObjectValidResult;
import cn.sticki.spel.validator.core.util.BigDecimalUtil;
import cn.sticki.spel.validator.core.util.CalcLengthUtil;
import cn.sticki.spel.validator.core.util.NumberComparatorUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.OptionalInt;

/**
 * 一些边边角角的测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/10/13
 */
public class OtherTest {

    @Test
    void testException() {
        SpelParserException exception = new SpelParserException(new RuntimeException("test"));
        Assertions.assertNotNull(exception);
    }

    @Test
    void testAnnotationMethodManager() {
        Method abc = AnnotationMethodManager.get(SpelAssert.class, "abc");
        Assertions.assertNull(abc);

        Method assertTrue = AnnotationMethodManager.get(SpelAssert.class, "assertTrue");
        Assertions.assertNotNull(assertTrue);
    }

    @Test
    void testObjectValidResult() {
        ArrayList<FieldValidResult> list = new ArrayList<>();
        list.add(new FieldValidResult(false, "test"));

        ObjectValidResult result = new ObjectValidResult();
        Assertions.assertFalse(result.hasError());
        result.addFieldResults(list);
        Assertions.assertTrue(result.hasError());
        Assertions.assertEquals(1, result.getErrorSize());
    }

    @Test
    void testCalcLengthUtil() {
        Assertions.assertEquals(0, CalcLengthUtil.calcFieldSize(null));
        Assertions.assertEquals(0, CalcLengthUtil.calcFieldSize(""));
        Assertions.assertEquals(2, CalcLengthUtil.calcFieldSize("ab"));
        Object[] objects = {new Object(), new Object()};
        Assertions.assertEquals(2, CalcLengthUtil.calcFieldSize(objects));
    }

    @Test
    void testBigDecimalUtil() {
        Assertions.assertEquals(BigDecimal.ONE, BigDecimalUtil.valueOf(BigDecimal.ONE));
        Assertions.assertEquals(BigDecimal.ONE, BigDecimalUtil.valueOf(1));
        Assertions.assertEquals(BigDecimal.ONE, BigDecimalUtil.valueOf("1"));
        //noinspection BigDecimalMethodWithoutRoundingCalled
        Assertions.assertEquals(BigDecimal.ONE.setScale(1), BigDecimalUtil.valueOf(1D));
        //noinspection BigDecimalMethodWithoutRoundingCalled
        Assertions.assertEquals(BigDecimal.ONE.setScale(1), BigDecimalUtil.valueOf(1F));
        Assertions.assertThrows(SpelArgumentException.class, () -> BigDecimalUtil.valueOf("a"));
    }

    @Test
    void testNumberComparatorUtil() {
        int compare = NumberComparatorUtil.compare(1, 2, NumberComparatorUtil.GREATER_THAN);
        Assertions.assertTrue(compare < 0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberComparatorUtil.compare(null, 1, NumberComparatorUtil.GREATER_THAN));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberComparatorUtil.compare(1, null, NumberComparatorUtil.GREATER_THAN));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberComparatorUtil.compare(1, 1, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> NumberComparatorUtil.compare(1, 1, OptionalInt.empty()));
    }

}
