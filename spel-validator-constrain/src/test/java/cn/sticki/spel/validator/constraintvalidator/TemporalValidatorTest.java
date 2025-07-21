package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelFuture;
import cn.sticki.spel.validator.constrain.SpelFutureOrPresent;
import cn.sticki.spel.validator.constrain.SpelPast;
import cn.sticki.spel.validator.constrain.SpelPastOrPresent;
import cn.sticki.spel.validator.core.result.FieldValidResult;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 时间约束校验器测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
public class TemporalValidatorTest {

    @Data
    static class TestBean {

        @SpelFuture
        private LocalDate futureDate;

        @SpelFutureOrPresent
        private LocalDateTime futureOrPresentDateTime;

        @SpelPast
        private Date pastDate;

        @SpelPastOrPresent
        private LocalDate pastOrPresentDate;

    }

    @Test
    void testSpelFutureValidator() throws Exception {
        SpelFutureValidator validator = new SpelFutureValidator();
        TestBean testBean = new TestBean();
        Field field = TestBean.class.getDeclaredField("futureDate");
        field.setAccessible(true);

        // Test null value (should be valid)
        testBean.setFutureDate(null);
        FieldValidResult result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test future date (should be valid)
        testBean.setFutureDate(LocalDate.now().plusDays(1));
        result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test past date (should be invalid)
        testBean.setFutureDate(LocalDate.now().minusDays(1));
        result = validator.isValid(null, testBean, field);
        assertFalse(result.isSuccess());

        // Test today (should be invalid for Future)
        testBean.setFutureDate(LocalDate.now());
        result = validator.isValid(null, testBean, field);
        assertFalse(result.isSuccess());
    }

    @Test
    void testSpelFutureOrPresentValidator() throws Exception {
        SpelFutureOrPresentValidator validator = new SpelFutureOrPresentValidator();
        TestBean testBean = new TestBean();
        Field field = TestBean.class.getDeclaredField("futureOrPresentDateTime");
        field.setAccessible(true);

        // Test null value (should be valid)
        testBean.setFutureOrPresentDateTime(null);
        FieldValidResult result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test future date (should be valid)
        testBean.setFutureOrPresentDateTime(LocalDateTime.now().plusDays(1));
        result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test past date (should be invalid)
        testBean.setFutureOrPresentDateTime(LocalDateTime.now().minusDays(1));
        result = validator.isValid(null, testBean, field);
        assertFalse(result.isSuccess());
    }

    @Test
    void testSpelPastValidator() throws Exception {
        SpelPastValidator validator = new SpelPastValidator();
        TestBean testBean = new TestBean();
        Field field = TestBean.class.getDeclaredField("pastDate");
        field.setAccessible(true);

        // Test null value (should be valid)
        testBean.setPastDate(null);
        FieldValidResult result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test past date (should be valid)
        testBean.setPastDate(new Date(System.currentTimeMillis() - 86400000)); // 1 day ago
        result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test future date (should be invalid)
        testBean.setPastDate(new Date(System.currentTimeMillis() + 86400000)); // 1 day later
        result = validator.isValid(null, testBean, field);
        assertFalse(result.isSuccess());
    }

    @Test
    void testSpelPastOrPresentValidator() throws Exception {
        SpelPastOrPresentValidator validator = new SpelPastOrPresentValidator();
        TestBean testBean = new TestBean();
        Field field = TestBean.class.getDeclaredField("pastOrPresentDate");
        field.setAccessible(true);

        // Test null value (should be valid)
        testBean.setPastOrPresentDate(null);
        FieldValidResult result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test past date (should be valid)
        testBean.setPastOrPresentDate(LocalDate.now().minusDays(1));
        result = validator.isValid(null, testBean, field);
        assertTrue(result.isSuccess());

        // Test future date (should be invalid)
        testBean.setPastOrPresentDate(LocalDate.now().plusDays(1));
        result = validator.isValid(null, testBean, field);
        assertFalse(result.isSuccess());
    }

}
