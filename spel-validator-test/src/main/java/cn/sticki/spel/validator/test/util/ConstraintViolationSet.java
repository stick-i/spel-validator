package cn.sticki.spel.validator.test.util;

import cn.sticki.spel.validator.core.result.FieldError;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 约束违反集合
 * <p>
 * 用于存储校验结果，根据字段名和期望的错误信息来获取字段约束结果
 *
 * @author 阿杆
 * @since 2024/10/29
 */
public class ConstraintViolationSet {

    private final Map<String, List<FieldError>> verifyMap;

    public ConstraintViolationSet(Collection<FieldError> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            verifyMap = Collections.emptyMap();
            return;
        }

        this.verifyMap = fieldErrors.stream().collect(Collectors.groupingBy(FieldError::getFieldName));
    }

    public static ConstraintViolationSet of(List<FieldError> fieldErrors) {
        return new ConstraintViolationSet(fieldErrors);
    }

    /**
     * 根据字段和期望的错误信息来获取字段约束结果
     *
     * @param fieldName     字段名
     * @param expectMessage 期望的错误信息
     * @return 字段约束结果，当 expectMessage 不为null时，会优先匹配具有相同message的数据
     */
    public FieldError getAndRemove(String fieldName, String expectMessage) {
        List<FieldError> violationList = verifyMap.get(fieldName);
        if (violationList == null || violationList.isEmpty()) {
            return null;
        }
        if (violationList.size() == 1 || expectMessage == null) {
            FieldError violation = violationList.get(0);
            verifyMap.remove(fieldName);
            return violation;
        }
        // 当存在多个约束时，优先匹配具有相同message的数据。
        // 否则当一个字段有多个约束条件时，无法匹配到期望的约束。
        for (FieldError violation : violationList) {
            if (expectMessage.equals(violation.getErrorMessage())) {
                violationList.remove(violation);
                return violation;
            }
        }

        return violationList.remove(0);
    }

    /**
     * 获取所有的约束违反字段
     */
    public Set<FieldError> getAll() {
        return verifyMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }

}