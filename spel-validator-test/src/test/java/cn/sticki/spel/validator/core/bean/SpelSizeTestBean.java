package cn.sticki.spel.validator.core.bean;

import cn.sticki.spel.validator.core.constrain.SpelSize;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * 功能：SpelSize 测试用例
 * 详细：
 *
 * @author 阿杆
 * @since 2024/7/26
 */
public class SpelSizeTestBean {

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

        private int max;

        // 默认参数
        @SpelSize
        private String test;

        // 变量参数
        @SpelSize(condition = "#this.condition", min = "#this.min", max = "#this.max", message = "testString")
        private String testString;

        @SpelSize(condition = "#this.condition", min = "#this.min", max = "#this.max")
        private CharSequence testCharSequence;

        @SpelSize(condition = "#this.condition", min = "#this.min", max = "#this.max")
        private List<String> testList;

        @SpelSize(condition = "#this.condition", min = "#this.min", max = "#this.max")
        private Map<Object, Object> testMap;

        @SpelSize(condition = "#this.condition", min = "#this.min", max = "#this.max")
        private Object[] testArray;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).condition(true).min(1).max(2).build(),
                VerifyFailedField.of()
        ));

        // 小于最小值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).min(1).max(2)
                        .test("")
                        .testString("")
                        .testArray(new Object[0])
                        .testList(Collections.emptyList())
                        .testMap(Collections.emptyMap())
                        .testCharSequence("")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestCharSequence,
                        ParamTestBean::getTestList,
                        ParamTestBean::getTestMap,
                        ParamTestBean::getTestArray
                ),
                VerifyFailedField.of(ParamTestBean::getTestString, "testString")
        ));

        // 大于最大值
        HashMap<Object, Object> map = new HashMap<>();
        map.put("1", "1");
        map.put("2", "2");
        map.put("3", "3");
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(true).min(1).max(2)
                        .test("123")
                        .testString("123")
                        .testArray(new Object[3])
                        .testList(Arrays.asList("1", "2", "3"))
                        .testMap(map)
                        .testCharSequence("123")
                        .build(),
                VerifyFailedField.of(
                        ParamTestBean::getTestCharSequence,
                        ParamTestBean::getTestList,
                        ParamTestBean::getTestMap,
                        ParamTestBean::getTestArray
                ),
                VerifyFailedField.of(ParamTestBean::getTestString, "testString")
        ));

        // 正常情况
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).condition(true).min(1).max(3)
                        .test("123")
                        .testString("123")
                        .testArray(new Object[3])
                        .testList(Arrays.asList("1", "2", "3"))
                        .testMap(map)
                        .testCharSequence("123")
                        .build()
        ));

        // message
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(5).condition(true).min(1).max(2).testString("").build(),
                VerifyFailedField.of(ParamTestBean::getTestString, "testString")
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(6).condition(false).min(1).max(2)
                        .test("123")
                        .testString("123")
                        .testArray(new Object[3])
                        .testList(Arrays.asList("1", "2", "3"))
                        .testMap(map)
                        .testCharSequence("123")
                        .build()
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
        @SpelSize(condition = "#this.condition1", min = "1", max = "2", message = "condition1")
        @SpelSize(condition = "#this.condition2", min = "2", max = "3", message = "condition2")
        private String test;

    }

    /**
     * Repeatable 测试
     */
    public static List<VerifyObject> repeatableTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // condition1
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(1).condition1(true).condition2(false).test("").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(2).condition1(true).condition2(false).test("1").build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(3).condition1(true).condition2(false).test("123").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));

        // condition2
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(4).condition1(false).condition2(true).test("1").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(5).condition1(false).condition2(true).test("12").build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(6).condition1(false).condition2(true).test("1234").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        // condition1 & condition2
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(7).condition1(true).condition2(true).test("1").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(8).condition1(true).condition2(true).test("12").build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(9).condition1(true).condition2(true).test("123").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(10).condition1(true).condition2(true).test("1234").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2"),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().id(11).condition1(true).condition2(true).test("").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition1"),
                VerifyFailedField.of(RepeatableTestBean::getTest, "condition2")
        ));

        return result;
    }

}