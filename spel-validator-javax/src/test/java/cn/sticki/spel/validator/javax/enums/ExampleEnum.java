package cn.sticki.spel.validator.javax.enums;

import lombok.Getter;

/**
 * 示例枚举
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/1
 */
@Getter
public enum ExampleEnum {

    ONE(1),

    TWO(2),

    THREE(3),

    FOUR(4),

    FIVE(5);

    private final Integer code;

    ExampleEnum(Integer code) {
        this.code = code;
    }

    public static ExampleEnum getByCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("code can not be null");
        }
        for (ExampleEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
