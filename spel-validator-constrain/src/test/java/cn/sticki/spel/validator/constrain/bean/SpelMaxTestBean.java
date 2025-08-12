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
                ParamTestBean.builder().id(2).max(5).testLong(6L).build(),
                VerifyFailedField.of(SpelMaxTestBean.ParamTestBean::getTestLong)
        ));

        // 等于最大值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(3).max(5).testLong(5L).build()
        ));

        // 小于最大值
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(4).max(5).testLong(-123L).build()
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

}
