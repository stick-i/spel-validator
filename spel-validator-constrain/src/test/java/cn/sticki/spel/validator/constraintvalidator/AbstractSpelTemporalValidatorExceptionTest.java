package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelPast;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AbstractSpelTemporalValidator 异常情况测试
 * 测试 AbstractSpelTemporalValidator 中的异常抛出语句覆盖
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/8/12
 */
public class AbstractSpelTemporalValidatorExceptionTest {

    /**
     * 创建一个测试用的 AbstractSpelTemporalValidator 实现
     */
    private static class TestTemporalValidator extends AbstractSpelTemporalValidator<SpelPast> {

        @Override
        protected boolean isValidTemporal(Object temporal) {
            return true;
        }

        // 暴露 protected 方法用于测试
        public int testCompareTemporal(Object temporal, Object now) {
            return super.compareTemporal(temporal, now);
        }

        public Object testGetNow(Object temporal) {
            return super.getNow(temporal);
        }

        @Override
        public FieldValidResult isValid(SpelPast annotation, Object obj, Field field) throws IllegalAccessException {
            return null;
        }

    }

    private final TestTemporalValidator validator = new TestTemporalValidator();

    /**
     * 测试 compareTemporal 方法：不同类型比较抛出异常
     */
    @Test
    public void testCompareTemporalDifferentTypes() {
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDate date = LocalDate.now();

        // 测试不同类型比较时抛出 IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.testCompareTemporal(dateTime, date);
        });

        assertTrue(exception.getMessage().contains("Cannot compare different types"));
        assertTrue(exception.getMessage().contains("LocalDateTime"));
        assertTrue(exception.getMessage().contains("LocalDate"));
    }

    /**
     * 测试 compareTemporal 方法：非 Comparable 类型抛出异常
     */
    @Test
    public void testCompareTemporalNonComparable() {
        // 创建一个非 Comparable 的对象
        Object nonComparable1 = new Object();
        Object nonComparable2 = new Object();

        // 测试非 Comparable 类型比较时抛出 IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.testCompareTemporal(nonComparable1, nonComparable2);
        });

        assertTrue(exception.getMessage().contains("Unsupported non-comparable temporal type"));
        assertTrue(exception.getMessage().contains("java.lang.Object"));
    }

    /**
     * 测试 getNow 方法：不支持的时间类型抛出异常
     */
    @Test
    public void testGetNowUnsupportedType() {
        // 创建一个不支持的时间类型对象
        Object unsupportedTemporal = new Object();

        // 测试不支持的时间类型时抛出 IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.testGetNow(unsupportedTemporal);
        });

        assertTrue(exception.getMessage().contains("Unsupported temporal type"));
        assertTrue(exception.getMessage().contains("java.lang.Object"));
    }

    /**
     * 测试正常情况：相同类型的时间比较
     */
    @Test
    public void testCompareTemporalSameType() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(1);
        LocalDateTime past = now.minusDays(1);

        // 测试相同类型的正常比较
        assertTrue(validator.testCompareTemporal(future, now) > 0);
        assertTrue(validator.testCompareTemporal(past, now) < 0);
        assertEquals(0, validator.testCompareTemporal(now, now));
    }

    /**
     * 测试正常情况：获取当前时间
     */
    @Test
    public void testGetNowSupportedTypes() {
        // 测试支持的时间类型
        assertNotNull(validator.testGetNow(LocalDateTime.now()));
        assertNotNull(validator.testGetNow(LocalDate.now()));
        assertNotNull(validator.testGetNow(new Date()));

        // 验证返回的类型正确
        assertInstanceOf(LocalDateTime.class, validator.testGetNow(LocalDateTime.now()));
        assertInstanceOf(LocalDate.class, validator.testGetNow(LocalDate.now()));
        assertInstanceOf(Date.class, validator.testGetNow(new Date()));
    }

    /**
     * 测试 Date 和 LocalDateTime 比较（不同类型）
     */
    @Test
    public void testCompareTemporalDateAndLocalDateTime() {
        Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.now();

        // 测试 Date 和 LocalDateTime 比较时抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            validator.testCompareTemporal(date, localDateTime);
        });

        assertTrue(exception.getMessage().contains("Cannot compare different types"));
        assertTrue(exception.getMessage().contains("Date"));
        assertTrue(exception.getMessage().contains("LocalDateTime"));
    }

}
