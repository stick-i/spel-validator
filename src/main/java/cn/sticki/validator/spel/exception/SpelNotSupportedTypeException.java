package cn.sticki.validator.spel.exception;

import lombok.Getter;

import java.util.Set;

/**
 * 不支持的类型异常
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/3
 */
@Getter
public class SpelNotSupportedTypeException extends SpelValidatorException {

    private final Class<?> clazz;

    private final Set<Class<?>> supperType;

    public SpelNotSupportedTypeException(Class<?> clazz, Set<Class<?>> supperType) {
        super("Class type not supported, current type: " + clazz.getName() + ", supper type: " + supperType.toString());
        this.clazz = clazz;
        this.supperType = supperType;
    }

}
