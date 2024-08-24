package cn.sticki.validator.spel.bean;

import cn.sticki.validator.spel.SpelValid;
import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import cn.sticki.validator.spel.constrain.SpelAssert;
import cn.sticki.validator.spel.constrain.SpelNotNull;
import cn.sticki.validator.spel.util.ID;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 作为示例的测试 bean
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/13
 */
public class ExampleTestBean {

    @Data
    @SpelValid
    public static class ParamTestBean implements ID {

        private int id;

        @NotNull
        private Boolean switchAudio;

        /**
         * 当 switchAudio 为 true 时，校验 audioContent，audioContent 不能为null
         */
        @SpelNotNull(condition = "#this.switchAudio == true", message = "语音内容不能为空")
        private String audioContent;

        /**
         * 枚举值校验
         * <p>
         * 通过静态方法调用，校验枚举值是否存在
         */
        @SpelAssert(assertTrue = " T(cn.sticki.validator.spel.enums.ExampleEnum).getByCode(#this.testEnum) != null ", message = "枚举值不合法")
        private Integer testEnum;

    }


    public static List<VerifyObject> testCase() {
        ArrayList<VerifyObject> result = new ArrayList<>();

        ParamTestBean bean = new ParamTestBean();
        bean.setId(1);
        bean.setSwitchAudio(true);
        bean.setAudioContent("hello");
        bean.setTestEnum(1);
        result.add(VerifyObject.of(bean));

        ParamTestBean bean2 = new ParamTestBean();
        bean2.setId(2);
        bean2.setSwitchAudio(null);
        bean2.setAudioContent(null);
        bean2.setTestEnum(0);
        result.add(VerifyObject.of(
                bean2,
                VerifyFailedField.of(ParamTestBean::getSwitchAudio),
                VerifyFailedField.of(ParamTestBean::getTestEnum, "枚举值不合法")
        ));

        ParamTestBean bean3 = new ParamTestBean();
        bean3.setId(3);
        bean3.setSwitchAudio(null);
        bean3.setAudioContent(null);
        bean3.setTestEnum(null);
        result.add(VerifyObject.of(bean3, true));

        return result;
    }

}
