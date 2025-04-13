package cn.sticki.spel.validator.jakarta.bean;

import cn.sticki.spel.validator.constrain.SpelNotNull;
import cn.sticki.spel.validator.constrain.SpelSize;
import cn.sticki.spel.validator.jakarta.SpelValid;
import cn.sticki.spel.validator.test.util.VerifyFailedField;
import cn.sticki.spel.validator.test.util.VerifyObject;
import lombok.Data;
import org.springframework.context.i18n.LocaleContextHolder;

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
    @SpelValid
    public static class Test1 {

        @SpelNotNull
        String field1 = null;

        @SpelSize(min = "0", max = "1")
        String field8 = "12345678901";

    }

    public static List<VerifyObject> testUs() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        LocaleContextHolder.setLocale(Locale.US);

        list.add(VerifyObject.of(new Test1(),
                        VerifyFailedField.of(Test1::getField1, "must not be null"),
                        VerifyFailedField.of(Test1::getField8, "size must be between 0 and 1")
                )
        );

        return list;
    }

    public static List<VerifyObject> testZh() {
        ArrayList<VerifyObject> list = new ArrayList<>();

        LocaleContextHolder.setLocale(Locale.CHINESE);

        list.add(VerifyObject.of(new Test1(),
                        VerifyFailedField.of(Test1::getField1, "不得为 null"),
                        VerifyFailedField.of(Test1::getField8, "大小必须在 0 和 1 之间")
                )
        );
        return list;
    }

}
