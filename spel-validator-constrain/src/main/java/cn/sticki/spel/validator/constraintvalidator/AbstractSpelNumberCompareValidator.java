package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.exception.SpelParserException;
import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.result.FieldValidResult;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 约束注解 Max、Min 的抽象校验器。
 *
 * @author oddfar、阿杆
 * @version 1.0
 * @since 2024/8/25
 */
public abstract class AbstractSpelNumberCompareValidator<T extends Annotation> implements SpelConstraintValidator<T> {

    public FieldValidResult isValid(Number fieldValue, String spel, String errorMessage, Object obj) {
        // 元素为null是被允许的
        if (fieldValue == null) {
            return FieldValidResult.success();
        }
        // 计算表达式的值，基本数据类型会自动装箱
        Object numberValue = SpelParser.parse(spel, obj);
        if (!(numberValue instanceof Number)) {
            throw new SpelParserException("Expression [" + spel + "] calculate result must be Number.");
        }
        // 比较大小，其中一个是Not-a-Number (NaN）默认失败
        if (!this.compare(fieldValue, (Number) numberValue)) {
            // todo 目前对Double的边界值处理不太友好，message的展示类似为：不能小于等于 NaN。后续考虑去掉对Double Float类型的支持，或者对边界值抛出异常。
            // 构建错误信息
            // String replacedMessage = errorMessage.replace("{value}", String.valueOf(numberValue));
            return new FieldValidResult(false, numberValue);
        }

        return FieldValidResult.success();
    }

    /**
     * 比较两个数值的大小，返回比较结果。
     *
     * @param fieldValue   当前元素的值
     * @param compareValue 比较的值，最大值或最小值
     * @return 成功时返回true，失败时返回false
     */
    protected abstract boolean compare(Number fieldValue, Number compareValue);

    static final Set<Class<?>> SUPPORT_TYPE;

    static {
        HashSet<Class<?>> hashSet = new HashSet<>();
        hashSet.add(Number.class);
        hashSet.add(int.class);
        hashSet.add(long.class);
        hashSet.add(float.class);
        hashSet.add(double.class);
        hashSet.add(short.class);
        hashSet.add(byte.class);
        SUPPORT_TYPE = Collections.unmodifiableSet(hashSet);
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}
