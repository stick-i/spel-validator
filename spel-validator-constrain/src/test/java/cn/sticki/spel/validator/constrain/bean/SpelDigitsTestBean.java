package cn.sticki.spel.validator.constrain.bean;

import cn.sticki.spel.validator.constrain.SpelDigits;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * SpelDigits 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2025/8/10
 */
public class SpelDigitsTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    public static class ParamTestBean implements ID {

        private int id;

        private boolean condition;

        private int maxInteger;

        private int maxFraction;

        // 变量参数
        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction", message = "testString")
        private BigDecimal testBigDecimal;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private BigInteger testBigInteger;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private Double testDouble;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private Float testFloat;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private Integer testInteger;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private Long testLong;

        @SpelDigits(condition = "#this.condition", integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private String testString;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).condition(true).maxInteger(3).maxFraction(2).build(),
                VerifyFailedField.of()
        ));

        // 超出整数位数限制
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).maxInteger(2).maxFraction(2)
                        .testBigDecimal(new BigDecimal("123.45"))
                        .testBigInteger(new BigInteger("123"))
                        .testDouble(123.45)
                        .testFloat(123.45f)
                        .testInteger(123)
                        .testLong(123L)
                        .testString("123.45")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestBigInteger,
                        ParamTestBean::getTestDouble,
                        ParamTestBean::getTestFloat,
                        ParamTestBean::getTestInteger,
                        ParamTestBean::getTestLong,
                        ParamTestBean::getTestString
                ),
                VerifyFailedField.of(ParamTestBean::getTestBigDecimal, "testString")
        ));

        // 超出小数位数限制
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(true).maxInteger(3).maxFraction(1)
                        .testBigDecimal(new BigDecimal("12.345"))
                        .testDouble(12.345)
                        .testFloat(12.345f)
                        .testString("12.345")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestDouble,
                        ParamTestBean::getTestFloat,
                        ParamTestBean::getTestString
                ),
                VerifyFailedField.of(ParamTestBean::getTestBigDecimal, "testString")
        ));

        // 符合位数要求
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).condition(true).maxInteger(3).maxFraction(2)
                        .testBigDecimal(new BigDecimal("99.9"))
                        .testBigInteger(new BigInteger("999"))
                        .testDouble(1.23)
                        .testFloat(12.3f)
                        .testInteger(123)
                        .testLong(1L)
                        .testString("99.9")
                        .build()
        ));

        // 边界值测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(5).condition(true).maxInteger(1).maxFraction(0)
                        .testBigDecimal(new BigDecimal("0"))
                        .testBigInteger(new BigInteger("1"))
                        .testDouble(5.0)
                        .testFloat(7.0f)
                        .testInteger(8)
                        .testLong(9L)
                        .testString("0")
                        .build()
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(6).condition(false).maxInteger(1).maxFraction(0)
                        .testBigDecimal(new BigDecimal("12345.6789"))
                        .testBigInteger(new BigInteger("12345"))
                        .testDouble(12345.6789)
                        .testFloat(12345.6789f)
                        .testInteger(12345)
                        .testLong(12345L)
                        .testString("12345.6789")
                        .build()
        ));

        // 无效字符串格式测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(7).condition(true).maxInteger(3).maxFraction(2)
                        .testString("abc")
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 空字符串测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(8).condition(true).maxInteger(3).maxFraction(2)
                        .testString("")
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 负数测试 - 符合位数要求
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(9).condition(true).maxInteger(3).maxFraction(2)
                        .testBigDecimal(new BigDecimal("-123.34"))
                        .testBigInteger(new BigInteger("-123"))
                        .testDouble(-12.34)
                        .testFloat(-12.34f)
                        .testInteger(-123)
                        .testLong(-123L)
                        .testString("-12.34")
                        .build()
        ));

        // 负数测试 - 超出整数位数限制
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(10).condition(true).maxInteger(2).maxFraction(2)
                        .testBigDecimal(new BigDecimal("-123.45"))
                        .testBigInteger(new BigInteger("-123"))
                        .testDouble(-123.45)
                        .testFloat(-123.45f)
                        .testInteger(-123)
                        .testLong(-123L)
                        .testString("-123.45")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestBigDecimal,
                        ParamTestBean::getTestBigInteger,
                        ParamTestBean::getTestDouble,
                        ParamTestBean::getTestFloat,
                        ParamTestBean::getTestInteger,
                        ParamTestBean::getTestLong,
                        ParamTestBean::getTestString
                )
        ));

        // 负数测试 - 超出小数位数限制
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(11).condition(true).maxInteger(3).maxFraction(1)
                        .testBigDecimal(new BigDecimal("-12.345"))
                        .testDouble(-12.345)
                        .testFloat(-12.345f)
                        .testString("-12.345")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestBigDecimal,
                        ParamTestBean::getTestDouble,
                        ParamTestBean::getTestFloat,
                        ParamTestBean::getTestString
                )
        ));

        // 特殊字符串格式测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(12).condition(true).maxInteger(3).maxFraction(2)
                        .testString("12.34.56")  // 多个小数点
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 字符串包含非数字字符
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(13).condition(true).maxInteger(3).maxFraction(2)
                        .testString("12.3a")
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 只有小数点的字符串
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(14).condition(true).maxInteger(3).maxFraction(2)
                        .testString(".")
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 只有负号的字符串
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(15).condition(true).maxInteger(3).maxFraction(2)
                        .testString("-")
                        .build(),
                VerifyFailedField.of(ParamTestBean::getTestString)
        ));

        // 科学计数法字符串
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(16).condition(true).maxInteger(3).maxFraction(0)
                        .testString("1.23E2")  // 123.0
                        .build()
        ));

        // 前导零测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(17).condition(true).maxInteger(1).maxFraction(2)
                        .testString("001.23")
                        .build()
        ));

        // 尾随零测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(18).condition(true).maxInteger(3).maxFraction(1)
                        .testString("12.300")
                        .build()
        ));

        return result;
    }

    /**
     * 基本数据类型支持性测试
     */
    @Data
    @Builder
    public static class ParamTestBean2 implements ID {

        private int id;

        @SpelDigits(integer = "3", fraction = "2")
        private int testInt;

        @SpelDigits(integer = "5", fraction = "0")
        private long testLong;

        @SpelDigits(integer = "2", fraction = "3")
        private double testDouble;

        @SpelDigits(integer = "2", fraction = "2")
        private float testFloat;

        @SpelDigits(integer = "2", fraction = "0")
        private byte testByte;

        @SpelDigits(integer = "3", fraction = "0")
        private short testShort;

    }

    public static List<VerifyObject> paramTest2Case() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 符合位数要求
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(1)
                        .testInt(123)
                        .testLong(12345)
                        .testDouble(12.345)
                        .testFloat(12.34f)
                        .testByte((byte) 99)
                        .testShort((short) 999)
                        .build()
        ));

        // 超出位数限制
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(2)
                        .testInt(1234)  // 超出3位整数
                        .testLong(123456)  // 超出5位整数
                        .testDouble(123.345)  // 超出2位整数
                        .testFloat(123.34f)  // 超出2位整数
                        .testByte((byte) 127)  // 超出2位整数
                        .testShort((short) 1234)  // 超出3位整数
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean2::getTestInt,
                        ParamTestBean2::getTestLong,
                        ParamTestBean2::getTestDouble,
                        ParamTestBean2::getTestFloat,
                        ParamTestBean2::getTestByte,
                        ParamTestBean2::getTestShort
                )
        ));

        // 小数位数测试
        result.add(VerifyObject.of(
                ParamTestBean2.builder().id(3)
                        .testDouble(1.2345)  // 超出3位小数
                        .testFloat(1.234f)   // 超出2位小数
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean2::getTestDouble,
                        ParamTestBean2::getTestFloat
                )
        ));

        return result;
    }

    /**
     * Repeatable 测试
     */
    @Data
    @Builder
    public static class RepeatableTestBean implements ID {

        private int id;

        private boolean condition1;

        private boolean condition2;

        @SpelDigits(condition = "#this.condition1", integer = "2", fraction = "1", message = "condition1")
        @SpelDigits(condition = "#this.condition2", integer = "3", fraction = "2", message = "condition2")
        private BigDecimal test;

    }

    /**
     * Repeatable 测试
     */
    public static List<VerifyObject> repeatableTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // condition1 通过
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(1).condition1(true).condition2(false).test(new BigDecimal("12.3")).build()
        ));

        // condition1 失败
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(2).condition1(true).condition2(false).test(new BigDecimal("123.45")).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));

        // condition2 通过
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(3).condition1(false).condition2(true).test(new BigDecimal("123.45")).build()
        ));

        // condition2 失败
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(4).condition1(false).condition2(true).test(new BigDecimal("1234.567")).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        // 两个条件都开启，都通过
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(5).condition1(true).condition2(true).test(new BigDecimal("12.3")).build()
        ));

        // 两个条件都开启，都失败
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(6).condition1(true).condition2(true).test(new BigDecimal("1234.567")).build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1"),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        return result;
    }

    /**
     * 边界值和异常情况测试
     */
    @Data
    @Builder
    public static class EdgeCaseTestBean implements ID {

        private int id;

        private int maxInteger;

        private int maxFraction;

        @SpelDigits(integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private String testString;

        @SpelDigits(integer = "#this.maxInteger", fraction = "#this.maxFraction")
        private BigDecimal testBigDecimal;

    }

    /**
     * 边界值和异常情况测试用例
     */
    public static List<VerifyObject> edgeCaseTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // integer 为负数的情况 - 应该抛出异常
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(1).maxInteger(-1).maxFraction(2)
                        .testString("12.34")
                        .build(),
                true
        ));

        // fraction 为负数的情况 - 应该抛出异常
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(2).maxInteger(3).maxFraction(-1)
                        .testString("12.34")
                        .build(),
                true
        ));

        // integer 和 fraction 都为负数的情况
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(3).maxInteger(-2).maxFraction(-1)
                        .testString("12.34")
                        .build(),
                true
        ));

        // integer 为 0 的情况
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(4).maxInteger(0).maxFraction(2)
                        .testString("0.12")
                        .build(),
                VerifyFailedField.of(EdgeCaseTestBean::getTestString)
        ));

        // fraction 为 0 的情况 - 只允许整数
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(5).maxInteger(3).maxFraction(0)
                        .testString("123")
                        .testBigDecimal(new BigDecimal("123"))
                        .build()
        ));

        // fraction 为 0 但有小数的情况
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(6).maxInteger(3).maxFraction(0)
                        .testString("12.3")
                        .testBigDecimal(new BigDecimal("12.3"))
                        .build(),
                VerifyFailedField.of(
                        EdgeCaseTestBean::getTestString,
                        EdgeCaseTestBean::getTestBigDecimal
                )
        ));

        // 极大的 integer 和 fraction 值
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(7).maxInteger(Integer.MAX_VALUE).maxFraction(Integer.MAX_VALUE)
                        .testString("123.456")
                        .testBigDecimal(new BigDecimal("123.456"))
                        .build()
        ));

        // 空白字符串测试
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(8).maxInteger(3).maxFraction(2)
                        .testString("   ")  // 只有空格
                        .build(),
                VerifyFailedField.of(EdgeCaseTestBean::getTestString)
        ));

        // 包含空格的数字字符串
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(9).maxInteger(3).maxFraction(2)
                        .testString(" 12.34 ")  // 前后有空格
                        .build(),
                VerifyFailedField.of(EdgeCaseTestBean::getTestString)
        ));

        // 数字中间有空格
        result.add(VerifyObject.of(
                EdgeCaseTestBean.builder().id(10).maxInteger(3).maxFraction(2)
                        .testString("12 .34")  // 中间有空格
                        .build(),
                VerifyFailedField.of(EdgeCaseTestBean::getTestString)
        ));

        return result;
    }

}
