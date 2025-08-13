package cn.sticki.spel.validator.constrain.bean;

import cn.sticki.spel.validator.constrain.SpelMax;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SpelMax 测试用例
 * <p>
 * 由于 SpelMax 和 SpelMax 的实现方式相同，故SpelMax仅进行简单测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/10/12
 */
public class SpelMaxTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    // @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        private int max;

        @SpelMax(value = "#this.max")
        private Long testLong;

        @SpelMax(value = "#this.max")
        private String testString;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).max(1).build(),
                VerifyFailedField.of()
        ));

        // 大于最大值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).max(5).testLong(6L).testString("6").build(),
                VerifyFailedField.of(
                        SpelMaxTestBean.ParamTestBean::getTestLong,
                        SpelMaxTestBean.ParamTestBean::getTestString
                )
        ));

        // 等于最大值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).max(5).testLong(5L).testString("5").build()
        ));

        // 小于最大值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).max(5).testLong(-123L).testString("4.5").build()
        ));

        return result;
    }

    /**
     * inclusive 参数测试
     */
    @Data
    @Builder
    public static class InclusiveTestBean implements ID {

        private int id;

        private int max;

        // inclusive=true (默认)
        @SpelMax(value = "#this.max")
        private Long testInclusiveTrue;

        // inclusive=false
        @SpelMax(value = "#this.max", inclusive = false)
        private Long testInclusiveFalse;

    }

    /**
     * inclusive 参数测试
     */
    public static List<VerifyObject> inclusiveTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // inclusive=true，边界值应该通过
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(1).max(100).testInclusiveTrue(100L).build()
        ));

        // inclusive=true，小于边界值应该通过
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(2).max(100).testInclusiveTrue(99L).build()
        ));

        // inclusive=true，大于边界值应该失败
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(3).max(100).testInclusiveTrue(101L).build(),
                VerifyFailedField.of(InclusiveTestBean::getTestInclusiveTrue)
        ));

        // inclusive=false，边界值应该失败
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(4).max(100).testInclusiveFalse(100L).build(),
                VerifyFailedField.of(InclusiveTestBean::getTestInclusiveFalse)
        ));

        // inclusive=false，小于边界值应该通过
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(5).max(100).testInclusiveFalse(99L).build()
        ));

        // inclusive=false，大于边界值应该失败
        result.add(VerifyObject.of(
                InclusiveTestBean.builder().id(6).max(100).testInclusiveFalse(101L).build(),
                VerifyFailedField.of(InclusiveTestBean::getTestInclusiveFalse)
        ));

        return result;
    }

    /**
     * CharSequence 类型测试
     */
    @Data
    @Builder
    public static class CharSequenceTestBean implements ID {

        private int id;

        private boolean condition;

        private int max;

        @SpelMax(condition = "#this.condition", value = "#this.max")
        private String testString;

        @SpelMax(condition = "#this.condition", value = "#this.max")
        private StringBuilder testStringBuilder;

        @SpelMax(condition = "#this.condition", value = "#this.max")
        private StringBuffer testStringBuffer;

    }

    /**
     * CharSequence 类型测试用例
     */
    public static List<VerifyObject> charSequenceTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null 值测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(1).condition(true).max(100).build(),
                VerifyFailedField.of()
        ));

        // 有效数字字符串 - 小于最大值
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(2).condition(true).max(100)
                        .testString("50")
                        .testStringBuilder(new StringBuilder("75.5"))
                        .testStringBuffer(new StringBuffer("99"))
                        .build()
        ));

        // 有效数字字符串 - 等于最大值
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(3).condition(true).max(100)
                        .testString("100")
                        .testStringBuilder(new StringBuilder("100.0"))
                        .testStringBuffer(new StringBuffer("100"))
                        .build()
        ));

        // 有效数字字符串 - 大于最大值
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(4).condition(true).max(100)
                        .testString("150")
                        .testStringBuilder(new StringBuilder("101.1"))
                        .testStringBuffer(new StringBuffer("200"))
                        .build(),
                VerifyFailedField.of(
                        CharSequenceTestBean::getTestString,
                        CharSequenceTestBean::getTestStringBuilder,
                        CharSequenceTestBean::getTestStringBuffer
                )
        ));

        // 负数测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(5).condition(true).max(-5)
                        .testString("-10")
                        .testStringBuilder(new StringBuilder("-8.5"))
                        .testStringBuffer(new StringBuffer("-6"))
                        .build()
        ));

        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(6).condition(true).max(-10)
                        .testString("-5")
                        .testStringBuilder(new StringBuilder("-8"))
                        .testStringBuffer(new StringBuffer("0"))
                        .build(),
                VerifyFailedField.of(
                        CharSequenceTestBean::getTestString,
                        CharSequenceTestBean::getTestStringBuilder,
                        CharSequenceTestBean::getTestStringBuffer
                )
        ));

        // 小数测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(7).condition(true).max(10)
                        .testString("9.99")
                        .testStringBuilder(new StringBuilder("3.14159"))
                        .testStringBuffer(new StringBuffer("2.718"))
                        .build()
        ));

        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(8).condition(true).max(5)
                        .testString("5.01")
                        .testStringBuilder(new StringBuilder("10.5"))
                        .testStringBuffer(new StringBuffer("6.7"))
                        .build(),
                VerifyFailedField.of(
                        CharSequenceTestBean::getTestString,
                        CharSequenceTestBean::getTestStringBuilder,
                        CharSequenceTestBean::getTestStringBuffer
                )
        ));

        // 无效数字格式测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(9).condition(true).max(100)
                        .testString("xyz")
                        .testStringBuilder(new StringBuilder("12.3.4"))
                        .testStringBuffer(new StringBuffer("not-a-number"))
                        .build(),
                VerifyFailedField.of(
                        CharSequenceTestBean::getTestString,
                        CharSequenceTestBean::getTestStringBuilder,
                        CharSequenceTestBean::getTestStringBuffer
                )
        ));

        // 空字符串测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(10).condition(true).max(100)
                        .testString("")
                        .testStringBuilder(new StringBuilder(""))
                        .testStringBuffer(new StringBuffer(""))
                        .build(),
                VerifyFailedField.of(
                        CharSequenceTestBean::getTestString,
                        CharSequenceTestBean::getTestStringBuilder,
                        CharSequenceTestBean::getTestStringBuffer
                )
        ));

        // 科学计数法测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(11).condition(true).max(100)
                        .testString("9.9E1")  // 99
                        .testStringBuilder(new StringBuilder("1.5E2"))  // 150
                        .testStringBuffer(new StringBuffer("5E1"))  // 50
                        .build(),
                VerifyFailedField.of(CharSequenceTestBean::getTestStringBuilder)
        ));

        // condition 为 false 的测试
        result.add(VerifyObject.of(
                CharSequenceTestBean.builder().id(12).condition(false).max(10)
                        .testString("100")  // 大于最大值，但条件为false，应该通过
                        .testStringBuilder(new StringBuilder("abc"))  // 无效格式，但条件为false，应该通过
                        .testStringBuffer(new StringBuffer(""))  // 空字符串，但条件为false，应该通过
                        .build()
        ));

        return result;
    }

}
