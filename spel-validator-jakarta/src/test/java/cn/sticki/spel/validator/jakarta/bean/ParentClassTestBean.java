package cn.sticki.spel.validator.jakarta.bean;

import cn.sticki.spel.validator.constrain.SpelNotNull;
import cn.sticki.spel.validator.jakarta.SpelValid;
import cn.sticki.spel.validator.test.util.ID;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试继承类
 *
 * @author 阿杆
 * @since 2025/1/23
 */
public class ParentClassTestBean {

    @Data
    @SpelValid
    public static class Parent implements ID {

        private int id;

        @SpelNotNull(message = "parentField不能为空")
        private String parentField;

    }

    @Data
    @SpelValid
    public static class Child extends Parent implements ID {

        @SpelNotNull(message = "childField不能为空")
        private String childField;

    }

    public static List<VerifyObject> paramTestCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        // 父类测试
        Parent parent1 = new Parent();
        parent1.setId(1);
        parent1.setParentField(null);
        result.add(VerifyObject.of(parent1, VerifyFailedField.of(Parent::getParentField)));

        Parent parent2 = new Parent();
        parent2.setId(2);
        parent2.setParentField("123");
        result.add(VerifyObject.of(parent2));

        // 子类测试
        Child child1 = new Child();
        child1.setId(1);
        child1.setParentField(null);
        child1.setChildField(null);
        result.add(VerifyObject.of(child1, VerifyFailedField.of(Child::getParentField, Child::getChildField)));

        Child child2 = new Child();
        child2.setId(2);
        child2.setParentField("123");
        child2.setChildField("123");
        result.add(VerifyObject.of(child2));

        return result;
    }

}
