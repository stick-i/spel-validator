package cn.sticki.validator.spel.util;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * Bean工具类
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/6/13
 */
public class BeanUtil {

    public static void main(String[] args) {
        String e = getFieldName(Object::getClass);
        System.out.println(e);
    }

    /**
     * 获取字段名称
     */
    public static <T> String getFieldName(IGetter<T, ?> fn) {
        try {
            // 获取writeReplace方法
            Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            // 调用writeReplace方法并获取SerializedLambda
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(fn);
            // 获取方法名
            String methodName = serializedLambda.getImplMethodName();
            // 去掉方法名前的get或is，并将首字母转为小写
            if (methodName.startsWith("get")) {
                methodName = methodName.substring(3);
            } else if (methodName.startsWith("is")) {
                methodName = methodName.substring(2);
            }
            // 将首字母转为小写
            if (methodName.length() > 1) {
                methodName = Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
            } else {
                methodName = methodName.toLowerCase();
            }
            return methodName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get the field name", e);
        }
    }

}
