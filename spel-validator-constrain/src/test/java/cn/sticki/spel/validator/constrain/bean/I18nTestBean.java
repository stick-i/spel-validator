package cn.sticki.spel.validator.constrain.bean;

import cn.sticki.spel.validator.constrain.*;
import cn.sticki.spel.validator.core.SpelValidContext;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 国际化消息测试
 *
 * @author 阿杆
 * @since 2025/4/11
 */
public class I18nTestBean {

    @Data
    public static class Test1 {

        @SpelNotNull
        String field1 = null;

        @SpelAssert(assertTrue = "false")
        String field2;

        @SpelMax(value = "1")
        int field3 = 2;

        @SpelMin(value = "1")
        int field4 = 0;

        @SpelNotBlank
        String field5 = "";

        @SpelNotEmpty
        List<Integer> field6 = new ArrayList<>();

        @SpelNull
        String field7 = "not null";

        @SpelSize(min = "0", max = "1")
        String field8 = "12345678901";

    }

    public static List<VerifyObject> testUs() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        SpelValidContext context = SpelValidContext.builder().locale(Locale.US).build();

        list.add(VerifyObject.of(new Test1(),
                        VerifyFailedField.of(Test1::getField1, "must not be null"),
                        VerifyFailedField.of(Test1::getField2, "must be true"),
                        VerifyFailedField.of(Test1::getField3, "must be less than or equal to 1"),
                        VerifyFailedField.of(Test1::getField4, "must be greater than or equal to 1"),
                        VerifyFailedField.of(Test1::getField5, "must not be blank"),
                        VerifyFailedField.of(Test1::getField6, "must not be empty"),
                        VerifyFailedField.of(Test1::getField7, "must be null"),
                        VerifyFailedField.of(Test1::getField8, "size must be between 0 and 1")
                ).setContext(context)
        );

        return list;
    }

    public static List<VerifyObject> testZh() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        SpelValidContext context = SpelValidContext.builder().locale(Locale.CHINESE).build();

        list.add(VerifyObject.of(new Test1(),
                        VerifyFailedField.of(Test1::getField1, "不得为 null"),
                        VerifyFailedField.of(Test1::getField2, "必须为 true"),
                        VerifyFailedField.of(Test1::getField3, "必须小于或等于 1"),
                        VerifyFailedField.of(Test1::getField4, "必须大于或等于 1"),
                        VerifyFailedField.of(Test1::getField5, "不得为空白"),
                        VerifyFailedField.of(Test1::getField6, "不得为空"),
                        VerifyFailedField.of(Test1::getField7, "必须为 null"),
                        VerifyFailedField.of(Test1::getField8, "大小必须在 0 和 1 之间")
                ).setContext(context)
        );
        return list;
    }

}
