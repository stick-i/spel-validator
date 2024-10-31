package cn.sticki.spel.validator.core.bean;

import cn.sticki.spel.validator.core.constrain.SpelMin;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SpelMin 测试用例
 *
 * @author oddfar、阿杆
 * @since 2024/8/25
 */
public class SpelMinTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    // @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        private boolean condition;

        private int min;

        // 默认参数
        @SpelMin
        private Integer test;

        // 变量参数
        @SpelMin(condition = "#this.condition", value = "#this.min", message = "testString")
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
        private Long testLong;

        @SpelMin(condition = "#this.condition", value = "#this.min")
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
                ParamTestBean.builder().id(1).condition(true).min(1).build(),
                VerifyFailedField.of()
        ));

        // 小于最小值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).min(5)
                        .test(-1)
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
                        ParamTestBean::getTest,
                        ParamTestBean::getTestBigInteger,
                        ParamTestBean::getTestByte,
                        ParamTestBean::getTestInt,
                        ParamTestBean::getTestShort,
                        ParamTestBean::getTestLong,
                        ParamTestBean::getTestDouble,
                        ParamTestBean::getTestFloat
                ),
                VerifyFailedField.of(ParamTestBean::getTestBigDecimal, "testString")
        ));

        // 等于最小值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(true).min(5)
                        .test(0)
                        .testBigDecimal(new BigDecimal("5"))
                        .testBigInteger(new BigInteger("5"))
                        .testByte((byte) 5)
                        .testInt(5)
                        .testShort((short) 5)
                        .testLong(5L)
                        .testDouble(5d)
                        .testFloat(5f)
                        .build()
        ));

        // 大于最小值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).condition(true).min(0)
                        .test(123)
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
                ParamTestBean.builder().id(5).condition(true).min(2).testBigDecimal(BigDecimal.valueOf(1)).build(),
                VerifyFailedField.of(ParamTestBean::getTestBigDecimal, "testString")
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(6).condition(false).min(0)
                        .test(-123)
                        .testBigDecimal(new BigDecimal("-123.12"))
                        .testBigInteger(new BigInteger("-123123"))
                        .testByte((byte) -12)
                        .testInt(-12)
                        .testShort((short) -12)
                        .testLong(-123L)
                        .testDouble((double) (-100 / 3))
                        .testFloat((float) (-100 / 3))
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTest)
        ));
        return result;
    }

    /**
     * Repeatable 测试
     */
    @Data
    @Builder
    // @SpelValid
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
                RepeatableTestBean.builder().id(1).condition1(true).condition2(false).test(2L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(2).condition1(true).condition2(false).test(1L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(3).condition1(true).condition2(false).test(0L).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));

        // condition2
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(4).condition1(false).condition2(true).test(3L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(5).condition1(false).condition2(true).test(2L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(6).condition1(false).condition2(true).test(1L).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        // condition1 & condition2
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(7).condition1(true).condition2(true).test(1L).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(8).condition1(true).condition2(true).test(2L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(9).condition1(true).condition2(true).test(3L).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(10).condition1(true).condition2(true).test(-1L).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2"),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(11).condition1(true).condition2(true).test(0L).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1"),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        return result;
    }

    /**
     * 基本数据类型支持性测试
     */
    @Data
    @Builder
    // @SpelValid
    public static class ParamTestBean2 implements ID {

        private int id;

        @SpelMin
        private int testInt;

        @SpelMin
        private long testLong;

        @SpelMin
        private double testDouble;

        @SpelMin
        private float testFloat;

        @SpelMin
        private byte testByte;

        @SpelMin
        private short testShort;

    }

    public static List<VerifyObject> paramTest2Case() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 大于最小值
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(1).testInt(1).testLong(1).testDouble(1).testFloat(1).testByte((byte) 1).testShort((short) 1).build()
        ));

        // 等于最小值
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(2).testInt(0).testLong(0).testDouble(0).testFloat(0).testByte((byte) 0).testShort((short) 0).build()
        ));

        // 小于最小值
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(3).testInt(-1).testLong(-1).testDouble(-1).testFloat(-1).testByte((byte) -1).testShort((short) -1).build(),
                VerifyFailedField.of(
                        ParamTestBean2::getTestInt,
                        ParamTestBean2::getTestLong,
                        ParamTestBean2::getTestDouble,
                        ParamTestBean2::getTestFloat,
                        ParamTestBean2::getTestByte,
                        ParamTestBean2::getTestShort
                )
        ));

        // 小数
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(4).testDouble(0.1).testFloat(0.1f).build()
        ));
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(5).testDouble(-0.1).testFloat(-0.1f).build(),
                VerifyFailedField.of(
                        ParamTestBean2::getTestDouble,
                        ParamTestBean2::getTestFloat
                )
        ));

        return result;
    }

    /**
     * 不同类型的value值测试
     */
    @Data
    @Builder
    // @SpelValid
    public static class ValueTypeTestBean implements ID {

        private int id;

        private int condition;

        @SpelMin(condition = "#this.condition == 1", value = "#this.testInt")
        @SpelMin(condition = "#this.condition == 2", value = "#this.testDou")
        @SpelMin(condition = "#this.condition == 3", value = "#this.testInteger")
        @SpelMin(condition = "#this.condition == 4", value = "#this.testDouble")
        @SpelMin(condition = "#this.condition == 6", value = "#this.testBigDecimal")
        @SpelMin(condition = "#this.condition == 7", value = "#this.testBigInteger")
        private Double testValueDouble;

        @SpelMin(condition = "#this.condition == 1", value = "#this.testInt")
        @SpelMin(condition = "#this.condition == 2", value = "#this.testDou")
        @SpelMin(condition = "#this.condition == 3", value = "#this.testInteger")
        @SpelMin(condition = "#this.condition == 4", value = "#this.testDouble")
        @SpelMin(condition = "#this.condition == 6", value = "#this.testBigDecimal")
        @SpelMin(condition = "#this.condition == 7", value = "#this.testBigInteger")
        private Float testValueFloat;

        @SpelMin(condition = "#this.condition == 1", value = "#this.testInt")
        @SpelMin(condition = "#this.condition == 2", value = "#this.testDou")
        @SpelMin(condition = "#this.condition == 3", value = "#this.testInteger")
        @SpelMin(condition = "#this.condition == 4", value = "#this.testDouble")
        @SpelMin(condition = "#this.condition == 6", value = "#this.testBigDecimal")
        @SpelMin(condition = "#this.condition == 7", value = "#this.testBigInteger")
        @SpelMin(condition = "#this.condition == 8", value = "#this.testFloat")
        private Integer testValueInt;

        private int testInt;

        private double testDou;

        private Integer testInteger;

        private Double testDouble;

        private BigDecimal testBigDecimal;

        private BigInteger testBigInteger;

        private Float testFloat;

    }

    public static List<VerifyObject> valueTypeTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 大于最小值
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(1).condition(1).testValueDouble(2D).testInt(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(2).condition(2).testValueDouble(2D).testDou(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(3).condition(3).testValueDouble(2D).testInteger(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(4).condition(4).testValueDouble(2D).testDouble(1d).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(6).condition(6).testValueDouble(2D).testBigDecimal(new BigDecimal(1)).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(7).condition(7).testValueDouble(2D).testBigInteger(new BigInteger("1")).build()
        ));

        // 等于最小值
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(8).condition(1).testValueDouble(1D).testInt(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(9).condition(2).testValueDouble(1D).testDou(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(10).condition(3).testValueDouble(1D).testInteger(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(11).condition(4).testValueDouble(1D).testDouble(1d).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(13).condition(6).testValueDouble(1D).testBigDecimal(new BigDecimal(1)).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(14).condition(7).testValueDouble(1D).testBigInteger(new BigInteger("1")).build()
        ));

        // 小于最小值
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(15).condition(1).testValueDouble(0D).testInt(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(16).condition(2).testValueDouble(0D).testDou(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(17).condition(3).testValueDouble(0D).testInteger(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(18).condition(4).testValueDouble(0D).testDouble(1d).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(20).condition(6).testValueDouble(0D).testBigDecimal(new BigDecimal(1)).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(21).condition(7).testValueDouble(0D).testBigInteger(new BigInteger("1")).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));

        // --------- 边界值：nan、infinity
        // double类型
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(22).condition(1).testValueDouble(Double.NaN).testInt(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(23).condition(1).testValueDouble(Double.POSITIVE_INFINITY).testInt(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(24).condition(1).testValueDouble(Double.NEGATIVE_INFINITY).testInt(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(25).condition(2).testValueDouble(1D).testDou(Double.NaN).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(26).condition(2).testValueDouble(1D).testDou(Double.POSITIVE_INFINITY).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueDouble)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(27).condition(2).testValueDouble(1D).testDou(Double.NEGATIVE_INFINITY).build()
        ));
        // float 类型
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(28).condition(1).testValueFloat(Float.NaN).testInt(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueFloat)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(29).condition(1).testValueFloat(Float.POSITIVE_INFINITY).testInt(1).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(30).condition(1).testValueFloat(Float.NEGATIVE_INFINITY).testInt(1).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueFloat)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(31).condition(2).testValueFloat(1F).testDou(Double.NaN).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueFloat)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(32).condition(2).testValueFloat(1F).testDou(Double.POSITIVE_INFINITY).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueFloat)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(33).condition(2).testValueFloat(1F).testDou(Double.NEGATIVE_INFINITY).build()
        ));
        // int 类型，但是表达式为double或float
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(34).condition(4).testValueInt(1).testDouble(1D).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(35).condition(8).testValueInt(1).testFloat(1F).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(36).condition(4).testValueInt(0).testDouble(Double.NaN).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueInt)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(37).condition(8).testValueInt(0).testFloat(Float.NaN).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueInt)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(38).condition(4).testValueInt(0).testDouble(Double.POSITIVE_INFINITY).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueInt)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(39).condition(8).testValueInt(0).testFloat(Float.POSITIVE_INFINITY).build(),
                VerifyFailedField.of(ValueTypeTestBean::getTestValueInt)
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(40).condition(4).testValueInt(0).testDouble(Double.NEGATIVE_INFINITY).build()
        ));
        result.add(VerifyObject.of(
                ValueTypeTestBean.builder().id(41).condition(8).testValueInt(0).testFloat(Float.NEGATIVE_INFINITY).build()
        ));

        return result;
    }

    /**
     * 不支持的value值类型测试
     */
    @Data
    @Builder
    // @SpelValid
    public static class NotSupportValueTypeTestBean implements ID {

        private int id;

        @SpelMin(value = "#this.testList")
        private Integer test;

        @SpelMin
        private List<String> testList;

    }

    public static List<VerifyObject> notSupportTypeTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // List类型
        result.add(VerifyObject.of(
                NotSupportValueTypeTestBean.builder().id(1).test(1).testList(Collections.emptyList()).build(),
                true
        ));

        return result;
    }

}
