package cn.sticki.spel.validator.test.util;

import cn.sticki.spel.validator.core.SpelValidContext;
import lombok.Data;
import org.intellij.lang.annotations.Language;

import java.util.Arrays;
import java.util.Collection;

/**
 * 验证对象类
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/14
 */
@Data
public class VerifyObject {

    /**
     * 待验证的对象
     */
    private Object object;

    /**
     * 异常字段列表
     */
    private Collection<VerifyFailedField> verifyFailedFields;

    /**
     * 是否期望抛出异常
     */
    private boolean expectException;

    /**
     * spel 分组参数
     * <p>
     * 当待验证的对象未使用 @SpelValid 注解时，会使用执行器 {@link cn.sticki.spel.validator.core.SpelValidExecutor} 直接进行校验，此时该参数生效
     */
    @Language("spel")
    private String[] spelGroups;

    /**
     * 校验上下文
     * <p>
     * 当待验证的对象未使用 @SpelValid 注解时，会使用执行器 {@link cn.sticki.spel.validator.core.SpelValidExecutor} 直接进行校验，此时该参数生效
     */
    private SpelValidContext context = SpelValidContext.getDefault();

    private VerifyObject() {
    }

    /**
     * 创建待验证对象
     *
     * @param object 待验证对象
     */
    public static VerifyObject of(Object object) {
        return VerifyObject.of(object, new VerifyFailedField[0]);
    }

    /**
     * 创建待验证对象
     *
     * @param object 待验证对象
     */
    public static VerifyObject of(Object object, boolean expectException) {
        return VerifyObject.of(object, new VerifyFailedField[0], expectException);
    }

    /**
     * 创建待验证对象
     *
     * @param object             待验证对象
     * @param verifyFailedFields 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, VerifyFailedField... verifyFailedFields) {
        return VerifyObject.of(object, verifyFailedFields, false);
    }

    /**
     * 创建待验证对象
     *
     * @param object             待验证对象
     * @param verifyFailedFields 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields) {
        return VerifyObject.of(object, verifyFailedFields, false);
    }

    /**
     * 创建待验证对象
     *
     * @param object              待验证对象
     * @param verifyFailedFields1 待验证的异常字段信息
     * @param verifyFailedFields2 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields1, VerifyFailedField... verifyFailedFields2) {
        verifyFailedFields1.addAll(Arrays.asList(verifyFailedFields2));
        return VerifyObject.of(object, verifyFailedFields1, false);
    }

    /**
     * 创建待验证对象
     *
     * @param object             待验证对象
     * @param verifyFailedFields 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, VerifyFailedField[] verifyFailedFields, boolean expectException) {
        return of(object, Arrays.asList(verifyFailedFields), expectException);
    }

    /**
     * 创建待验证对象
     *
     * @param object             待验证对象
     * @param verifyFailedFields 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, Collection<VerifyFailedField> verifyFailedFields, boolean expectException) {
        VerifyObject verifyObject = new VerifyObject();
        verifyObject.setObject(object);
        verifyObject.setVerifyFailedFields(verifyFailedFields);
        verifyObject.setExpectException(expectException);
        return verifyObject;
    }

    public VerifyObject setGroups(@Language("spel") String... spelGroups) {
        this.spelGroups = spelGroups;
        return this;
    }

    public VerifyObject setContext(SpelValidContext context) {
        this.context = context;
        return this;
    }

}
