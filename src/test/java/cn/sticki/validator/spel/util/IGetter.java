package cn.sticki.validator.spel.util;

/**
 * getter，用于获取对象的属性值
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/15
 */
@FunctionalInterface
public interface IGetter<T, R> extends java.io.Serializable {

    @SuppressWarnings("unused")
    R apply(T t);

}
