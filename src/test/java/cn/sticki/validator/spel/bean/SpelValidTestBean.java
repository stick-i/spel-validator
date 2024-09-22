package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelNotNull;
import cn.sticki.validator.spel.util.ID;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SpelValid 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/9/22
 */
public class SpelValidTestBean {

    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // On
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(1).condition(true).test(null).build(),
                VerifyFailedField.of(ParamTestBean::getTest)
        ));

        // off
        result.add(VerifyObject.of(
                ParamTestBean.builder().id(2).condition(false).test(null).build()
        ));

        return result;
    }

    /**
     * 参数测试
     */
    @Data
    @Builder
    @SpelValid(condition = "#this.condition == true")
    public static class ParamTestBean implements ID {

        private int id;

        private boolean condition;

        @SpelNotNull
        private String test;

    }

}
