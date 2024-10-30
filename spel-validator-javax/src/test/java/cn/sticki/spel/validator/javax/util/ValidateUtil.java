package cn.sticki.spel.validator.javax.util;

import cn.sticki.spel.validator.core.SpelValidExecutor;
import cn.sticki.spel.validator.core.result.FieldError;
import cn.sticki.spel.validator.core.result.ObjectValidResult;
import cn.sticki.spel.validator.javax.SpelValid;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测试验证工具类
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/13
 */
@Slf4j
public class ValidateUtil {

    @SuppressWarnings("resource")
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 参数校验
     * <p>
     * 调用此方法会触发约束校验
     *
     * @return 校验结果
     */
    public static ObjectValidResult validate(Object obj) {
        // 如果对象没有使用 SpelValid 注解，则直接调用验证执行器进行验证
        // 这种情况下，只会验证本框架提供的约束注解
        if (!obj.getClass().isAnnotationPresent(SpelValid.class)) {
            return SpelValidExecutor.validateObject(obj);
        }

        // 通过 @Valid 的方式进行验证
        Set<ConstraintViolation<Object>> validate = validator.validate(obj);
        if (validate == null || validate.isEmpty()) {
            return ObjectValidResult.EMPTY;
        }
        ObjectValidResult validResult = new ObjectValidResult();
        List<FieldError> list = validate.stream().map(ValidateUtil::convert).collect(Collectors.toList());
        validResult.addFieldError(list);
        return validResult;
    }

    private static FieldError convert(ConstraintViolation<Object> violation) {
        return FieldError.of(violation.getPropertyPath().toString(), violation.getMessage());
    }

    /**
     * 验证约束结果是否符合预期
     */
    public static boolean checkConstraintResult(List<VerifyObject> verifyObjectList) {
        int failCount = 0;
        for (VerifyObject verifyObject : verifyObjectList) {
            if (!checkConstraintResult(verifyObject)) {
                failCount++;
            }
        }
        return failCount == 0;
    }

    /**
     * 验证约束结果是否符合预期
     */
    public static boolean checkConstraintResult(VerifyObject verifyObject) {
        Object object = verifyObject.getObject();
        Collection<VerifyFailedField> verifyFailedFields = verifyObject.getVerifyFailedFields();
        boolean expectException = verifyObject.isExpectException();

        // 设置日志上下文
        LogContext.setValidateObject(object);
        log.info("Start checking object: {}", object);

        int failCount = 0;
        try {
            // 执行约束校验
            ObjectValidResult validResult = ValidateUtil.validate(object);
            failCount += processVerifyResult(verifyFailedFields, ConstraintViolationSet.of(validResult.getErrors()));
        } catch (Exception e) {
            if (expectException) {
                log.info("Passed, Capture exception {}, message: {}", e.getClass(), e.getMessage());
                expectException = false;
            } else {
                log.error("Failed, Capture exception {}, message: {}", e.getClass(), e.getMessage());
                failCount++;
            }
        }

        if (expectException) {
            log.error("Failed, No exception captured");
            failCount++;
        }

        if (failCount == 0) {
            log.info("Verification end, all passed");
        } else {
            log.error("Verification end, number of failures: {}", failCount);
        }
        log.info("------------------------------------------------------------------------");
        LogContext.clearValidateObject();

        return failCount == 0;
    }

    /**
     * 处理验证结果
     *
     * @param verifyFailedFields 预期失败字段
     * @param violationSet       验证结果
     * @return 验证失败的次数
     */
    private static int processVerifyResult(Collection<VerifyFailedField> verifyFailedFields, ConstraintViolationSet violationSet) {
        final String fieldNameLogKey = "fieldName";
        int failCount = 0;
        // 检查结果是否符合预期
        for (VerifyFailedField verifyFailedField : verifyFailedFields) {
            String fieldName = verifyFailedField.getName();
            LogContext.set(fieldNameLogKey, fieldName);
            String message = verifyFailedField.getMessage();

            log.info("Expected exception information: {}", message == null ? "ignore" : message);

            boolean fieldMatch = false, find = false;

            FieldError fieldError = violationSet.getAndRemove(fieldName, message);
            if (fieldError != null) {
                find = true;
                log.info("Real exception information: {}", fieldError.getErrorMessage());

                // 异常信息不同时验证失败（没填写异常信息则不校验异常信息）
                if (message != null && !message.equals(fieldError.getErrorMessage())) {
                    log.error("Failed");
                } else {
                    fieldMatch = true;
                    log.info("Passed");
                }
            }

            if (!find) {
                // 多余的字段
                log.error("Excess field");
            }
            if (!fieldMatch) {
                failCount++;
            }
        }

        LogContext.remove(fieldNameLogKey);
        // 被忽略的字段
        for (FieldError violation : violationSet.getAll()) {
            log.error("Field [{}] is ignored", violation.getFieldName());
            failCount++;
        }
        return failCount;
    }


}
