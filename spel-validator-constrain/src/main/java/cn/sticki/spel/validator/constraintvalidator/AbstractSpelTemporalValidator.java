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
        if (temporal instanceof ChronoLocalDateTime || now instanceof ChronoLocalDateTime) {
            return compareChronoLocalDateTime(temporal, now);
        }
        if (temporal instanceof ChronoZonedDateTime || now instanceof ChronoZonedDateTime) {
            return compareChronoZonedDateTime(temporal, now);
        }
        if (temporal instanceof ChronoLocalDate || now instanceof ChronoLocalDate) {
            return compareChronoLocalDate(temporal, now);
        }

        // 检查类型是否一致
        if (temporal.getClass() != now.getClass()) {
            throw differentTypeException(temporal, now);
        }

        if (temporal instanceof Comparable) {
            // 因为前面已经检查了 temporal.getClass() == now.getClass()，
            // 所以这里的 a.compareTo(b) 是类型安全的。
            //noinspection unchecked
            return ((Comparable<Object>) temporal).compareTo(now);
        }

        throw new IllegalArgumentException("Unsupported non-comparable temporal type: " + temporal.getClass().getName());
    }

    private int compareChronoLocalDateTime(Object temporal, Object now) {
        if (!(temporal instanceof ChronoLocalDateTime) || !(now instanceof ChronoLocalDateTime)) {
            throw differentTypeException(temporal, now);
        }
        ChronoLocalDateTime<?> temporalDateTime = (ChronoLocalDateTime<?>) temporal;
        ChronoLocalDateTime<?> nowDateTime = (ChronoLocalDateTime<?>) now;
        return temporalDateTime.compareTo(nowDateTime);
    }

    private int compareChronoZonedDateTime(Object temporal, Object now) {
        if (!(temporal instanceof ChronoZonedDateTime) || !(now instanceof ChronoZonedDateTime)) {
            throw differentTypeException(temporal, now);
        }
        ChronoZonedDateTime<?> temporalDateTime = (ChronoZonedDateTime<?>) temporal;
        ChronoZonedDateTime<?> nowDateTime = (ChronoZonedDateTime<?>) now;
        return temporalDateTime.compareTo(nowDateTime);
    }

    private int compareChronoLocalDate(Object temporal, Object now) {
        if (!(temporal instanceof ChronoLocalDate) || !(now instanceof ChronoLocalDate)) {
            throw differentTypeException(temporal, now);
        }
        ChronoLocalDate temporalDate = (ChronoLocalDate) temporal;
        ChronoLocalDate nowDate = (ChronoLocalDate) now;
        return temporalDate.compareTo(nowDate);
    }

    private IllegalArgumentException differentTypeException(Object temporal, Object now) {
        return new IllegalArgumentException("Cannot compare different types: " +
                temporal.getClass().getName() + " and " + now.getClass().getName());
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

        if (temporal instanceof ChronoLocalDateTime) {
            ChronoLocalDateTime<?> chronoLocalDateTime = (ChronoLocalDateTime<?>) temporal;
            // 必须基于真实当前时刻构造“now”，不能复用被校验值自身日期
            return chronoLocalDateTime.toLocalDate()
                    .getChronology()
                    .zonedDateTime(Instant.now(), ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        if (temporal instanceof ChronoZonedDateTime) {
            ChronoZonedDateTime<?> chronoZonedDateTime = (ChronoZonedDateTime<?>) temporal;
            // 对带时区时间使用同一时区下的真实当前时刻
            return chronoZonedDateTime.getChronology()
                    .zonedDateTime(Instant.now(), chronoZonedDateTime.getZone());
        }

        // 对于 ChronoLocalDate 的实现类，统一使用 ISO 日期作为比较基准
        if (temporal instanceof ChronoLocalDate) {
            return LocalDate.now();
        }

        throw new IllegalArgumentException("Unsupported temporal type: " + temporal.getClass());
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}
