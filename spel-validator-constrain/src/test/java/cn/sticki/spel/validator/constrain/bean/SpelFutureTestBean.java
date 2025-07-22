package cn.sticki.spel.validator.constrain.bean;

import cn.sticki.spel.validator.constrain.SpelFuture;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Builder;
import lombok.Data;

import java.time.*;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * {@link SpelFuture} 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/12/21
 */
public class SpelFutureTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    public static class ParamTestBean implements ID {

        private int id;

        private boolean condition;

        @SpelFuture
        private LocalDate futureDate;

        @SpelFuture(condition = "#this.condition")
        private LocalDateTime conditionalDateTime;

        @SpelFuture
        private Date utilDate;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        List<VerifyObject> result = new ArrayList<>();

        // 未来时间 - 应该通过验证
        result.add(VerifyObject.of(
                ParamTestBean.builder()
                        .id(1)
                        .condition(true)
                        .futureDate(LocalDate.now().plusDays(1))
                        .conditionalDateTime(LocalDateTime.now().plusHours(1))
                        .utilDate(new Date(System.currentTimeMillis() + 3600000))
                        .build()
        ));

        // 过去时间 - 应该验证失败
        result.add(VerifyObject.of(
                ParamTestBean.builder()
                        .id(2)
                        .condition(true)
                        .futureDate(LocalDate.now().minusDays(1))
                        .conditionalDateTime(LocalDateTime.now().minusHours(1))
                        .utilDate(new Date(System.currentTimeMillis() - 3600000))
                        .build(),
                VerifyFailedField.of(ParamTestBean::getFutureDate),
                VerifyFailedField.of(ParamTestBean::getConditionalDateTime),
                VerifyFailedField.of(ParamTestBean::getUtilDate)
        ));

        // null值 - 应该通过验证
        result.add(VerifyObject.of(
                ParamTestBean.builder()
                        .id(3)
                        .condition(true)
                        .futureDate(null)
                        .conditionalDateTime(null)
                        .utilDate(null)
                        .build()
        ));

        // 条件为false - 应该跳过验证
        result.add(VerifyObject.of(
                ParamTestBean.builder()
                        .id(4)
                        .condition(false)
                        .futureDate(LocalDate.now().minusDays(1))
                        .conditionalDateTime(LocalDateTime.now().minusHours(1))
                        .utilDate(new Date(System.currentTimeMillis() - 3600000))
                        .build(),
                VerifyFailedField.of(ParamTestBean::getFutureDate),
                VerifyFailedField.of(ParamTestBean::getUtilDate)
        ));

        return result;
    }

    /**
     * 支持的类型测试
     */
    @Data
    @Builder
    static class TypeTestBean {

        @SpelFuture
        private Date date;

        @SpelFuture
        private Calendar calendar;

        @SpelFuture
        private Instant instant;

        @SpelFuture
        private LocalDate localDate;

        @SpelFuture
        private LocalDateTime localDateTime;

        @SpelFuture
        private LocalTime localTime;

        @SpelFuture
        private MonthDay monthDay;

        @SpelFuture
        private OffsetDateTime offsetDateTime;

        @SpelFuture
        private OffsetTime offsetTime;

        @SpelFuture
        private Year year;

        @SpelFuture
        private YearMonth yearMonth;

        @SpelFuture
        private ZonedDateTime zonedDateTime;

        @SpelFuture
        private HijrahDate hijrahDate;

        @SpelFuture
        private JapaneseDate japaneseDate;

        @SpelFuture
        private MinguoDate minguoDate;

        @SpelFuture
        private ThaiBuddhistDate thaiBuddhistDate;

    }

    /**
     * 类型测试
     */
    public static List<VerifyObject> typeTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 未来时间 - 所有类型都应该通过验证
        Calendar futureCalendar = Calendar.getInstance();
        futureCalendar.add(Calendar.DAY_OF_MONTH, 1);

        result.add(VerifyObject.of(
                TypeTestBean.builder()
                        .localDate(LocalDate.now().plusDays(1))
                        .localDateTime(LocalDateTime.now().plusHours(1))
                        .localTime(LocalTime.now().plusHours(1))
                        .instant(Instant.now().plusSeconds(3600))
                        .offsetDateTime(OffsetDateTime.now().plusHours(1))
                        .offsetTime(OffsetTime.now().plusHours(1))
                        .zonedDateTime(ZonedDateTime.now().plusHours(1))
                        .year(Year.now().plusYears(1))
                        .yearMonth(YearMonth.now().plusMonths(1))
                        .monthDay(MonthDay.of(12, 31))
                        .date(new Date(System.currentTimeMillis() + 3600000))
                        .calendar(futureCalendar)
                        .hijrahDate(HijrahDate.now().plus(1, ChronoUnit.DAYS))
                        .japaneseDate(JapaneseDate.now().plus(1, ChronoUnit.DAYS))
                        .minguoDate(MinguoDate.now().plus(1, ChronoUnit.DAYS))
                        .thaiBuddhistDate(ThaiBuddhistDate.now().plus(1, ChronoUnit.DAYS))
                        .build()
        ));

        // 过去时间 - 所有类型都应该验证失败
        Calendar pastCalendar = Calendar.getInstance();
        pastCalendar.add(Calendar.DAY_OF_MONTH, -1);

        result.add(VerifyObject.of(
                TypeTestBean.builder()
                        .localDate(LocalDate.now().minusDays(1))
                        .localDateTime(LocalDateTime.now().minusHours(1))
                        .localTime(LocalTime.now().minusHours(1))
                        .instant(Instant.now().minusSeconds(3600))
                        .offsetDateTime(OffsetDateTime.now().minusHours(1))
                        .offsetTime(OffsetTime.now().minusHours(1))
                        .zonedDateTime(ZonedDateTime.now().minusHours(1))
                        .year(Year.now().minusYears(1))
                        .yearMonth(YearMonth.now().minusMonths(1))
                        .monthDay(MonthDay.of(1, 1))
                        .date(new Date(System.currentTimeMillis() - 3600000))
                        .calendar(pastCalendar)
                        .hijrahDate(HijrahDate.now().minus(1, ChronoUnit.DAYS))
                        .japaneseDate(JapaneseDate.now().minus(1, ChronoUnit.DAYS))
                        .minguoDate(MinguoDate.now().minus(1, ChronoUnit.DAYS))
                        .thaiBuddhistDate(ThaiBuddhistDate.now().minus(1, ChronoUnit.DAYS))
                        .build(),
                VerifyFailedField.of(TypeTestBean::getLocalDate),
                VerifyFailedField.of(TypeTestBean::getLocalDateTime),
                VerifyFailedField.of(TypeTestBean::getLocalTime),
                VerifyFailedField.of(TypeTestBean::getInstant),
                VerifyFailedField.of(TypeTestBean::getOffsetDateTime),
                VerifyFailedField.of(TypeTestBean::getOffsetTime),
                VerifyFailedField.of(TypeTestBean::getZonedDateTime),
                VerifyFailedField.of(TypeTestBean::getYear),
                VerifyFailedField.of(TypeTestBean::getYearMonth),
                VerifyFailedField.of(TypeTestBean::getMonthDay),
                VerifyFailedField.of(TypeTestBean::getDate),
                VerifyFailedField.of(TypeTestBean::getCalendar),
                VerifyFailedField.of(TypeTestBean::getHijrahDate),
                VerifyFailedField.of(TypeTestBean::getJapaneseDate),
                VerifyFailedField.of(TypeTestBean::getMinguoDate),
                VerifyFailedField.of(TypeTestBean::getThaiBuddhistDate)
        ));

        return result;
    }

    /**
     * 重复注解测试
     */
    @Data
    @Builder
    static class RepeatableTestBean {

        boolean condition1;

        boolean condition2;

        @SpelFuture(condition = "#this.condition1", message = "condition1")
        @SpelFuture(condition = "#this.condition2", message = "condition2")
        private LocalDate testDate;

    }

    public static List<VerifyObject> repeatableTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 两个条件都为true，过去时间应该产生两个错误
        result.add(VerifyObject.of(
                RepeatableTestBean.builder()
                        .condition1(true)
                        .condition2(true)
                        .testDate(LocalDate.now().minusDays(1))
                        .build(),
                VerifyFailedField.of(RepeatableTestBean::getTestDate, "condition1"),
                VerifyFailedField.of(RepeatableTestBean::getTestDate, "condition2")
        ));

        // 只有condition1为true
        result.add(VerifyObject.of(
                RepeatableTestBean.builder()
                        .condition1(true)
                        .condition2(false)
                        .testDate(LocalDate.now().minusDays(1))
                        .build(),
                VerifyFailedField.of(RepeatableTestBean::getTestDate, "condition1")
        ));

        // 两个条件都为false，应该跳过验证
        result.add(VerifyObject.of(
                RepeatableTestBean.builder()
                        .condition1(false)
                        .condition2(false)
                        .testDate(LocalDate.now().minusDays(1))
                        .build()
        ));

        // 未来时间，即使条件为true也应该通过验证
        result.add(VerifyObject.of(
                RepeatableTestBean.builder()
                        .condition1(true)
                        .condition2(true)
                        .testDate(LocalDate.now().plusDays(1))
                        .build()
        ));

        return result;
    }

}
