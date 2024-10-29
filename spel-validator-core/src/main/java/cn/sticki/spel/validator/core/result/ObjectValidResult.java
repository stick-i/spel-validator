package cn.sticki.spel.validator.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 对象校验结果
 *
 * @author 阿杆
 * @version 1.1
 * @since 2024/4/29
 */
public class ObjectValidResult {

    private final List<FieldError> errors = new ArrayList<>();

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public boolean noneError() {
        return errors.isEmpty();
    }

    public List<FieldError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public int getErrorSize() {
        return errors.size();
    }

    /**
     * 添加校验结果
     * <p>
     * 当校验结果为false时，会将错误信息添加到结果中
     *
     * @param results 字段校验结果列表
     */
    public void addFieldResults(List<FieldValidResult> results) {
        for (FieldValidResult result : results) {
            this.addFieldResult(result);
        }
    }

    /**
     * 添加校验结果
     * <p>
     * 当校验结果为false时，会将错误信息添加到结果中
     *
     * @param result 字段校验结果
     */
    public void addFieldResult(FieldValidResult result) {
        if (!result.isSuccess()) {
            errors.add(new FieldError(result.getFieldName(), result.getMessage()));
        }
    }
}
