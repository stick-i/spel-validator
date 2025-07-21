package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.annotation.Annotation;
import java.time.*;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;

/**
 * 时间约束注解的抽象校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/07/20
 */
public abstract class AbstractSpelTemporalValidator<T extends Annotation> implements SpelConstraintValidator<T> {

    private static final Set<Class<?>> SUPPORT_TYPE;

    static {
        Set<Class<?>> supportTypes = new HashSet<>();
        // Java 8 time API
        supportTypes.add(Instant.class);
        supportTypes.add(LocalDate.class);
        supportTypes.add(LocalDateTime.class);
        supportTypes.add(LocalTime.class);
        supportTypes.add(MonthDay.class);
        supportTypes.add(OffsetDateTime.class);
        supportTypes.add(OffsetTime.class);
        supportTypes.add(Year.class);
        supportTypes.add(YearMonth.class);
        supportTypes.add(ZonedDateTime.class);

        // Chronos API
        supportTypes.add(ChronoLocalDate.class);
        supportTypes.add(ChronoLocalDateTime.class);
        supportTypes.add(ChronoZonedDateTime.class);

        // Legacy Date API
        supportTypes.add(Date.class);
        supportTypes.add(Calendar.class);

        SUPPORT_TYPE = Collections.unmodifiableSet(supportTypes);
    }

    /**
     * 校验时间字段
     *
     * @param fieldValue 字段值
     * @return 校验结果
     */
    protected FieldValidResult isValid(Object fieldValue) {
        // null值被认为是有效的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }

        return FieldValidResult.of(isValidTemporal(fieldValue));
    }

    /**
     * 校验时间对象是否满足条件
     *
     * @param temporal 时间对象
     * @return 是否有效
     */
    protected abstract boolean isValidTemporal(Object temporal);

    /**
     * 比较两个时间对象
     *
     * @param temporal 要比较的时间对象
     * @param now      当前时间
     * @return 比较结果：负数表示temporal在now之前，0表示相等，正数表示temporal在now之后
     */
    protected int compareTemporal(Object temporal, Object now) {
        if (temporal instanceof Instant && now instanceof Instant) {
            return ((Instant) temporal).compareTo((Instant) now);
        }

        if (temporal instanceof LocalDate && now instanceof LocalDate) {
            return ((LocalDate) temporal).compareTo((LocalDate) now);
        }

        if (temporal instanceof LocalDateTime && now instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).compareTo((LocalDateTime) now);
        }

        if (temporal instanceof LocalTime && now instanceof LocalTime) {
            return ((LocalTime) temporal).compareTo((LocalTime) now);
        }

        if (temporal instanceof OffsetDateTime && now instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).compareTo((OffsetDateTime) now);
        }

        if (temporal instanceof OffsetTime && now instanceof OffsetTime) {
            return ((OffsetTime) temporal).compareTo((OffsetTime) now);
        }

        if (temporal instanceof ZonedDateTime && now instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).compareTo((ZonedDateTime) now);
        }

        if (temporal instanceof Year && now instanceof Year) {
            return ((Year) temporal).compareTo((Year) now);
        }

        if (temporal instanceof YearMonth && now instanceof YearMonth) {
            return ((YearMonth) temporal).compareTo((YearMonth) now);
        }

        if (temporal instanceof MonthDay && now instanceof MonthDay) {
            return ((MonthDay) temporal).compareTo((MonthDay) now);
        }

        if (temporal instanceof Date && now instanceof Date) {
            return ((Date) temporal).compareTo((Date) now);
        }

        if (temporal instanceof Calendar && now instanceof Calendar) {
            return ((Calendar) temporal).compareTo((Calendar) now);
        }

        // 对于 ChronoLocalDate, ChronoLocalDateTime, ChronoZonedDateTime
        if (temporal instanceof ChronoLocalDate && now instanceof ChronoLocalDate) {
            return ((ChronoLocalDate) temporal).compareTo((ChronoLocalDate) now);
        }

        if (temporal instanceof ChronoLocalDateTime && now instanceof ChronoLocalDateTime) {
            return ((ChronoLocalDateTime<?>) temporal).compareTo((ChronoLocalDateTime<?>) now);
        }

        if (temporal instanceof ChronoZonedDateTime && now instanceof ChronoZonedDateTime) {
            return ((ChronoZonedDateTime<?>) temporal).compareTo((ChronoZonedDateTime<?>) now);
        }

        // 如果类型不匹配，尝试转换为Instant进行比较
        return compareAsInstant(temporal, now);
    }

    /**
     * 将时间对象转换为Instant进行比较
     */
    private int compareAsInstant(Object temporal, Object now) {
        Instant temporalInstant = toInstant(temporal);
        Instant nowInstant = toInstant(now);
        return temporalInstant.compareTo(nowInstant);
    }

    /**
     * 将时间对象转换为Instant
     */
    private Instant toInstant(Object temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atZone(ZoneId.systemDefault()).toInstant();
        }
        if (temporal instanceof LocalDate) {
            return ((LocalDate) temporal).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant();
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant();
        }
        if (temporal instanceof Date) {
            return ((Date) temporal).toInstant();
        }
        if (temporal instanceof Calendar) {
            return ((Calendar) temporal).toInstant();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + temporal.getClass());
    }

    /**
     * 获取当前时间，类型与传入的时间对象相同
     */
    protected Object getNow(Object temporal) {
        if (temporal instanceof Instant) {
            return Instant.now();
        }
        if (temporal instanceof LocalDate) {
            return LocalDate.now();
        }
        if (temporal instanceof LocalDateTime) {
            return LocalDateTime.now();
        }
        if (temporal instanceof LocalTime) {
            return LocalTime.now();
        }
        if (temporal instanceof OffsetDateTime) {
            return OffsetDateTime.now();
        }
        if (temporal instanceof OffsetTime) {
            return OffsetTime.now();
        }
        if (temporal instanceof ZonedDateTime) {
            return ZonedDateTime.now();
        }
        if (temporal instanceof Year) {
            return Year.now();
        }
        if (temporal instanceof YearMonth) {
            return YearMonth.now();
        }
        if (temporal instanceof MonthDay) {
            return MonthDay.now();
        }
        if (temporal instanceof Date) {
            return new Date();
        }
        if (temporal instanceof Calendar) {
            return Calendar.getInstance();
        }

        // 对于 ChronoLocalDate, ChronoLocalDateTime, ChronoZonedDateTime
        if (temporal instanceof ChronoLocalDate) {
            return LocalDate.now();
        }
        if (temporal instanceof ChronoLocalDateTime) {
            return LocalDateTime.now();
        }
        if (temporal instanceof ChronoZonedDateTime) {
            return ZonedDateTime.now();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + temporal.getClass());
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}
