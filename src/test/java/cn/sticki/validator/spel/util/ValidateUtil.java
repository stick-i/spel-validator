package cn.sticki.validator.spel.util;

import cn.sticki.validator.spel.VerifyFailedField;
import cn.sticki.validator.spel.VerifyObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;
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
     * 调用此方法会触发 javax.validation.constraints.* 的校验，类似于使用 @Valid 注解
     *
     * @return 校验结果，如果校验通过则返回空列表
     */
    public static <T> Set<ConstraintViolation<T>> validate(T obj) {
        return validator.validate(obj);
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

        String className = object.getClass().getSimpleName();

        MDC.put("className", className);
        if (object instanceof ID) {
            MDC.put("id", String.valueOf(((ID) object).getId()));
        }
        log.info("Start checking object: {}", object);

        int failCount = 0;
        try {
            // 执行约束校验
            Set<ConstraintViolation<Object>> validate = ValidateUtil.validate(object);
            failCount += calcFailCount(verifyFailedFields, ViolationSet.of(validate));
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

        log.info("Verification end, number of failures: {}", failCount);
        log.info("------------------------------------------------------------------------");
        MDC.clear();

        return failCount == 0;
    }

    /**
     * 计算验证字段约束的失败次数
     *
     * @param verifyFailedFields 预期失败字段
     * @param violationSet       验证结果
     */
    private static int calcFailCount(Collection<VerifyFailedField> verifyFailedFields, ViolationSet violationSet) {
        int failCount = 0;
        // 检查结果是否符合预期
        for (VerifyFailedField verifyFailedField : verifyFailedFields) {
            String fieldName = verifyFailedField.getName();
            MDC.put("fieldName", fieldName);
            String message = verifyFailedField.getMessage();

            log.info("Expected exception information: {}", message == null ? "ignore" : message);

            boolean fieldMatch = false, find = false;

            ConstraintViolation<Object> violation = violationSet.getAndRemove(fieldName, message);
            if (violation != null) {
                find = true;
                log.info("Real exception information: {}", violation.getMessage());

                // 异常信息不同时验证失败（没填写异常信息则不校验异常信息）
                if (message != null && !message.equals(violation.getMessage())) {
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

        MDC.remove("fieldName");
        // 被忽略的字段
        for (ConstraintViolation<Object> violation : violationSet.getAll()) {
            log.error("Field [{}] is ignored", violation.getPropertyPath().toString());
            failCount++;
        }
        return failCount;
    }

    static class ViolationSet {

        private final Map<String, List<ConstraintViolation<Object>>> violationMap;

        public ViolationSet(Set<ConstraintViolation<Object>> validate) {
            if (validate == null || validate.isEmpty()) {
                violationMap = Collections.emptyMap();
                return;
            }
            violationMap = validate.stream().collect(
                    Collectors.groupingBy(violation -> violation.getPropertyPath().toString())
            );
        }

        public static ViolationSet of(Set<ConstraintViolation<Object>> validate) {
            return new ViolationSet(validate);
        }

        /**
         * 根据字段和期望的错误信息来获取字段约束结果
         *
         * @param fieldName     字段名
         * @param expectMessage 期望的错误信息
         * @return 字段约束结果，当 expectMessage 不为null时，会优先匹配具有相同message的数据
         */
        public ConstraintViolation<Object> getAndRemove(String fieldName, String expectMessage) {
            List<ConstraintViolation<Object>> violationList = violationMap.get(fieldName);
            if (violationList == null || violationList.isEmpty()) {
                return null;
            }
            if (violationList.size() == 1 || expectMessage == null) {
                ConstraintViolation<Object> violation = violationList.get(0);
                violationMap.remove(fieldName);
                return violation;
            }
            // 当存在多个约束时，优先匹配具有相同message的数据
            for (ConstraintViolation<Object> violation : violationList) {
                if (expectMessage.equals(violation.getMessage())) {
                    violationList.remove(violation);
                    return violation;
                }
            }

            return violationList.remove(0);
        }

        public Set<ConstraintViolation<Object>> getAll() {
            return violationMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
        }

    }

}
