package cn.sticki.validator.spel;

import lombok.Data;

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
     * 校验不通过字段
     */

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
     * @param object             待验证对象
     * @param verifyFailedFields 待验证的异常字段信息
     */
    public static VerifyObject of(Object object, VerifyFailedField[] verifyFailedFields, boolean expectException) {
        return VerifyObject.of(object, Arrays.asList(verifyFailedFields), expectException);
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

}
