package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelAssert;
import cn.sticki.validator.spel.util.ID;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SpelAssert 测试用例
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
public class SpelAssertTestBean {

    @Data
    @SpelValid(spelGroups = "#this.group")
    public static class ParamTestBean implements ID {

        private int id;

        @SpelAssert(condition = "false", assertTrue = "false")
        private Integer test;

        @SpelAssert(condition = "true", assertTrue = "false ")
        private List<Integer> test2;

        @SpelAssert(condition = "true")
        private Double test3;

        @SpelAssert(assertTrue = "#this.test4 == 'test4'", message = "test4")
        private String test4;

        private String group;

        @SpelAssert(assertTrue = "false", message = "group1", group = "'group1'")
        private Boolean test5;

        private String group2 = "group2";

        @SpelAssert(assertTrue = "false", message = "group2", group = "#this.group2")
        private Boolean test6;

    }

    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        ParamTestBean bean = new ParamTestBean();
        bean.setId(1);
        list.add(VerifyObject.of(
                bean,
                VerifyFailedField.of(ParamTestBean::getTest2),
                VerifyFailedField.of(ParamTestBean::getTest4, "test4")
        ));

        ParamTestBean bean2 = new ParamTestBean();
        bean2.setId(2);
        bean2.setTest4("test4");
        bean2.setGroup("group1");
        list.add(VerifyObject.of(
                bean2,
                VerifyFailedField.of(ParamTestBean::getTest2),
                VerifyFailedField.of(ParamTestBean::getTest5, "group1")
        ));

        ParamTestBean bean3 = new ParamTestBean();
        bean3.setId(3);
        bean3.setTest4("test4");
        bean3.setGroup("00");
        list.add(VerifyObject.of(
                bean3,
                VerifyFailedField.of(ParamTestBean::getTest2)
        ));

        ParamTestBean bean4 = new ParamTestBean();
        bean4.setId(4);
        bean4.setGroup("group2");
        // bean4.setGroup2("group2");
        list.add(VerifyObject.of(
                bean4,
                VerifyFailedField.of(ParamTestBean::getTest2),
                VerifyFailedField.of(ParamTestBean::getTest4, "test4"),
                VerifyFailedField.of(ParamTestBean::getTest6, "group2")
        ));

        return list;
    }

    @Data
    @SpelValid
    public static class EmptyTestBean {

        @SpelAssert(condition = "true", assertTrue = "")
        private Integer test;

    }

    public static List<VerifyObject> emptyTestCase() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        EmptyTestBean bean = new EmptyTestBean();
        list.add(VerifyObject.of(bean, true));

        return list;
    }

}
