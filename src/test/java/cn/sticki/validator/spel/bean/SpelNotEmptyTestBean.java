package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelNotEmpty;
import cn.sticki.validator.spel.util.ID;
import lombok.Builder;
import lombok.Data;

import java.util.*;

/**
 * 功能：SpelNotEmpty 测试用例
 * 详细：
 *
 * @author 阿杆
 * @since 2024/7/26
 */
public class SpelNotEmptyTestBean {

    /**
     * 参数测试
     */
    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).condition(true).build(),
                VerifyFailedField.of(ParamTestBean::getTest, ParamTestBean::getTest2, ParamTestBean::getTest3)
        ));

        // 空字符串
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(true).test("").test2("").test3("").build(),
                VerifyFailedField.of(ParamTestBean::getTest, ParamTestBean::getTest2, ParamTestBean::getTest3)
        ));

        // 正常情况
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).condition(true).test("1").test2("2").test3("3").build()
        ));

        // condition测试
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).condition(false).build(),
                VerifyFailedField.of(ParamTestBean::getTest)
        ));
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(5).condition(false).test("1").test2("2").test3("3").build()
        ));

        return result;
    }

    /**
     * 类型测试
     */
    public static List<VerifyObject> typeTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // null
        result.add(VerifyObject.of(
                TypeTestBean.builder().build(),
                VerifyFailedField.of(TypeTestBean::getTestCharSequence, TypeTestBean::getTestCollection, TypeTestBean::getTestMap, TypeTestBean::getTestArray)
        ));

        // empty
        result.add(VerifyObject.of(
                TypeTestBean.builder().testCharSequence("").testArray(new Object[0]).testCollection(Collections.emptyList()).testMap(Collections.emptyMap()).build(),
                VerifyFailedField.of(TypeTestBean::getTestCharSequence, TypeTestBean::getTestCollection, TypeTestBean::getTestMap, TypeTestBean::getTestArray)
        ));

        result.add(VerifyObject.of(
                TypeTestBean.builder()
                        .testCharSequence("11")
                        .testArray(new Object[1])
                        .testCollection(Collections.singleton(new Object()))
                        .testMap(Collections.singletonMap("1", "1"))
                        .build()
        ));

        return result;
    }

    /**
     * 参数测试
     */
    @Data
    @Builder
    @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        // 默认参数
        @SpelNotEmpty
        private String test;

        private boolean condition;

        // 变量参数
        @SpelNotEmpty(condition = "#this.condition", message = "test2")
        private String test2;

        // 变量参数2
        @SpelNotEmpty(condition = "#this.condition", message = "test3")
        private String test3;

    }

    /**
     * 支持的类型测试
     */
    @Data
    @Builder
    @SpelValid
    static class TypeTestBean {

        @SpelNotEmpty
        private CharSequence testCharSequence;

        @SpelNotEmpty
        private Collection<?> testCollection;

        @SpelNotEmpty
        private Map<?, ?> testMap;

        @SpelNotEmpty
        private Object[] testArray;

    }

}
