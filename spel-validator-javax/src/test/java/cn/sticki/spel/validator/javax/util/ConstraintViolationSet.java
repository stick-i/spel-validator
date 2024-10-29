package cn.sticki.spel.validator.javax.util;

import javax.validation.ConstraintViolation;
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

    private static final ConstraintViolationSet EMPTY = new ConstraintViolationSet(Collections.emptyList());

    private final Map<String, List<VerifyFailedField>> verifyMap;

    public ConstraintViolationSet(Collection<VerifyFailedField> failedFields) {
        if (failedFields == null || failedFields.isEmpty()) {
            verifyMap = Collections.emptyMap();
            return;
        }

        this.verifyMap = failedFields.stream().collect(Collectors.groupingBy(VerifyFailedField::getName));
    }

    public static ConstraintViolationSet of(Set<ConstraintViolation<Object>> validate) {
        if (validate == null || validate.isEmpty()) {
            return EMPTY;
        }
        List<VerifyFailedField> list = validate.stream().map(ConstraintViolationSet::convert).collect(Collectors.toList());
        return new ConstraintViolationSet(list);
    }

    public static ConstraintViolationSet of(List<VerifyFailedField> validate) {
        return new ConstraintViolationSet(validate);
    }

    private static VerifyFailedField convert(ConstraintViolation<Object> violation) {
        return VerifyFailedField.of(violation.getPropertyPath().toString(), violation.getMessage());
    }

    /**
     * 根据字段和期望的错误信息来获取字段约束结果
     *
     * @param fieldName     字段名
     * @param expectMessage 期望的错误信息
     * @return 字段约束结果，当 expectMessage 不为null时，会优先匹配具有相同message的数据
     */
    public VerifyFailedField getAndRemove(String fieldName, String expectMessage) {
        List<VerifyFailedField> violationList = verifyMap.get(fieldName);
        if (violationList == null || violationList.isEmpty()) {
            return null;
        }
        if (violationList.size() == 1 || expectMessage == null) {
            VerifyFailedField violation = violationList.get(0);
            verifyMap.remove(fieldName);
            return violation;
        }
        // 当存在多个约束时，优先匹配具有相同message的数据。
        // 否则当一个字段有多个约束条件时，无法匹配到期望的约束。
        for (VerifyFailedField violation : violationList) {
            if (expectMessage.equals(violation.getMessage())) {
                violationList.remove(violation);
                return violation;
            }
        }

        return violationList.remove(0);
    }

    /**
     * 获取所有的约束违反字段
     */
    public Set<VerifyFailedField> getAll() {
        return verifyMap.values().stream().flatMap(List::stream).collect(Collectors.toSet());
    }

}