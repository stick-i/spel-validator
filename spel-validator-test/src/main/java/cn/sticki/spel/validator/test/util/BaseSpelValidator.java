package cn.sticki.spel.validator.test.util;

import cn.sticki.spel.validator.core.SpelValidExecutor;
import cn.sticki.spel.validator.core.result.ObjectValidResult;

import java.util.List;

/**
 * 测试验证工具
 *
 * @author 阿杆
 * @since 2024/10/31
 */
public class BaseSpelValidator extends AbstractSpelValidator {

    private static final BaseSpelValidator INSTANCE = new BaseSpelValidator();

    public static boolean check(List<VerifyObject> verifyObjectList) {
        return INSTANCE.checkConstraintResult(verifyObjectList);
    }

    /**
     * 参数校验
     * <p>
     * 调用此方法会触发约束校验
     *
     * @param obj        待验证对象
     * @param spelGroups spel 分组参数
     * @return 校验结果
     */
    @Override
    public ObjectValidResult validate(Object obj, String[] spelGroups) {
        return SpelValidExecutor.validateObject(obj, spelGroups);
    }

}
