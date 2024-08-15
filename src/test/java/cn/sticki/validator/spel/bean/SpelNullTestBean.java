package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelNull;
import cn.sticki.validator.spel.util.ID;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：SpelNull 测试用例
 * 详细：
 *
 * @author 黄天文
 * @since 2024/8/14
 */
public class SpelNullTestBean {

    /**
     * 参数测试
     */
    @Data
    @Builder
    @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        // 默认参数
        @SpelNull
        private String test;

        private boolean condition;

        // 变量参数
        @SpelNull(condition = "#this.condition", message = "test2")
        private String test2;

        // 变量参数2
        @SpelNull(condition = "#this.condition", message = "test3")
        private String test3;

    }

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        List<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).condition(true).test(null).test2(null).test3(null).build()
        ));

        // not null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).test("").test2("").test3("").build(),
                VerifyFailedField.of(ParamTestBean::getTest, ParamTestBean::getTest2, ParamTestBean::getTest3)
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(false).build()
        ));
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).condition(false).test("1").test2("2").test3("3").build(),
                VerifyFailedField.of(ParamTestBean::getTest)
        ));

        return result;
    }

    /**
     * 支持的类型测试
     */
    @Data
    @Builder
    @SpelValid
    static class TypeTestBean {

        @SpelNull
        private Object object;

        @SpelNull
        private CharSequence charSequence;

    }

    /**
     * 类型测试
     */
    public static List<VerifyObject> typeTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                TypeTestBean.builder().build()
        ));

        // not null
        result.add(VerifyObject.of(
                TypeTestBean.builder().charSequence("").object(new Object()).build(),
                VerifyFailedField.of(TypeTestBean::getObject, TypeTestBean::getCharSequence)
        ));

        return result;
    }

    /**
     * Repeatable 测试
     */
    @Data
    @Builder
    @SpelValid
    static class RepeatableTestBean {

        boolean condition1;

        boolean condition2;

        @SpelNull(condition = "#this.condition1", message = "condition1")
        @SpelNull(condition = "#this.condition2", message = "condition2")
        private String test1;

    }

    /**
     * Repeatable 测试
     */
    public static List<VerifyObject> repeatableTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // not null
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(true).condition2(true).test1("").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest1, "condition1"),
                VerifyFailedField.of(RepeatableTestBean::getTest1, "condition2")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(false).condition2(true).test1("").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest1, "condition2")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(true).condition2(false).test1("").build(),
                VerifyFailedField.of(RepeatableTestBean::getTest1, "condition1")
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(false).condition2(false).test1("").build()
        ));

        // null
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(true).condition2(true).test1(null).build()
        ));
        result.add(VerifyObject.of(
                RepeatableTestBean.builder().condition1(true).condition2(false).test1(null).build()
        ));

        return result;
    }

}
