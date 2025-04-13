package cn.sticki.spel.validator.test.util;

import cn.sticki.spel.validator.core.SpelValidContext;
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

    @Override
    public ObjectValidResult validate(Object obj, String[] spelGroups, SpelValidContext context) {
        return SpelValidExecutor.validateObject(obj, spelGroups, context);
    }

}
