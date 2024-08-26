package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelMin;
import cn.sticki.validator.spel.util.ID;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能：SpelMin 测试用例
 *
 * @author oddfar
 * @since 2024/8/25
 */
public class SpelMinTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        private boolean condition;

        private int min;

        // 默认参数
        @SpelMin
        private String test;

        // 变量参数
        @SpelMin(condition = "#this.condition", value = "#this.min", message = "testString")
        private String testString;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private BigDecimal testBigDecimal;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private BigInteger testBigInteger;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private Byte testByte;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private Integer testInt;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private Short testShort;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        @Min(value = 0)
        private Long testLong;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        @Min(value = 0)
        private Double testDouble;

        @SpelMin(condition = "#this.condition", value = "#this.min")
        private Float testFloat;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                SpelMinTestBean.ParamTestBean.builder().id(1).condition(true).min(1).build(),
                VerifyFailedField.of()
        ));

        // 小于最小值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).min(5)
                        .test("1")
                        .testString("2")
                        .testBigDecimal(new BigDecimal(0))
                        .testBigInteger(new BigInteger("0"))
                        .testByte((byte) 0)
                        .testInt(0)
                        .testShort((short) 0)
                        .testLong(0L)
                        .testDouble((double) (1 / 3))
                        .testFloat((float) (1 / 3))
                        .build(),
                VerifyFailedField.of(
                        SpelMinTestBean.ParamTestBean::getTestBigDecimal,
                        SpelMinTestBean.ParamTestBean::getTestBigInteger,
                        SpelMinTestBean.ParamTestBean::getTestByte,
                        SpelMinTestBean.ParamTestBean::getTestInt,
                        SpelMinTestBean.ParamTestBean::getTestShort,
                        SpelMinTestBean.ParamTestBean::getTestLong,
                        SpelMinTestBean.ParamTestBean::getTestDouble,
                        SpelMinTestBean.ParamTestBean::getTestFloat
                ),
                VerifyFailedField.of(SpelMinTestBean.ParamTestBean::getTestString, "testString")
        ));

        // 正常情况
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(true).min(0)
                        .test("123")
                        .testString("123")
                        .testBigDecimal(new BigDecimal("123.12"))
                        .testBigInteger(new BigInteger("123123"))
                        .testByte((byte) 12)
                        .testInt(12)
                        .testShort((short) 12)
                        .testLong(123L)
                        .testDouble((double) (100 / 3))
                        .testFloat((float) (100 / 3))
                        .build()
        ));

        // message
        result.add(VerifyObject.of(
                SpelMinTestBean.ParamTestBean.builder().id(4).condition(true).min(1).testString("2").build(),
                VerifyFailedField.of(SpelMinTestBean.ParamTestBean::getTestString, "testString")
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(5).condition(true).min(0)
                        .test("123")
                        .testString("123")
                        .testBigDecimal(new BigDecimal("123.12"))
                        .testBigInteger(new BigInteger("123123"))
                        .testByte((byte) 12)
                        .testInt(12)
                        .testShort((short) 12)
                        .testLong(123L)
                        .testDouble((double) (100 / 3))
                        .testFloat((float) (100 / 3))
                        .build()
        ));
        return result;
    }

    /**
     * Repeatable 测试
     */
    @Data
    @Builder
    @SpelValid
    public static class RepeatableTestBean implements ID {

        private int id;

        private boolean condition1;

        private boolean condition2;

        // 默认参数
        @SpelMin(condition = "#this.condition1", value = "1", message = "condition1")
        @SpelMin(condition = "#this.condition2", value = "2", message = "condition2")
        private Long test;

    }

    /**
     * Repeatable 测试
     */
    public static List<VerifyObject> repeatableTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // condition1
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(1).condition1(true).condition2(false).test(1L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(2).condition1(true).condition2(false).test(1L).build()
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(3).condition1(true).condition2(false).test(123L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition1")
        ));

        // condition2
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(4).condition1(false).condition2(true).test(1L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition2")
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(5).condition1(false).condition2(true).test(12L).build()
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(6).condition1(false).condition2(true).test(1234L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition2")
        ));

        // condition1 & condition2
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(7).condition1(true).condition2(true).test(1L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition2")
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(8).condition1(true).condition2(true).test(12L).build()
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(9).condition1(true).condition2(true).test(123L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(10).condition1(true).condition2(true).test(1234L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition2"),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                SpelMinTestBean.RepeatableTestBean.builder().id(11).condition1(true).condition2(true).test(0L).build(),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition1"),
                VerifyFailedField.of(SpelMinTestBean.RepeatableTestBean::getTest, "condition2")
        ));

        return result;
    }

}
