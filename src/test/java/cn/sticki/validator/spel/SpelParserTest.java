package cn.sticki.validator.spel;

import cn.sticki.validator.spel.exception.SpelParserException;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.parse.SpelValidatorBeanRegistrar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SpelParser 测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/10/12
 */
public class SpelParserTest {

    @BeforeAll
    static void init() {
        // 创建一个Spring ApplicationContext
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        // 构建Bean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyService.class);
        applicationContext.registerBeanDefinition("myService", builder.getBeanDefinition());
        // 刷新上下文，这会初始化所有单例Bean
        applicationContext.refresh();

        // 注册上下文到SpelValidatorBeanRegistrar
        //noinspection WriteOnlyObject
        SpelValidatorBeanRegistrar registrar = new SpelValidatorBeanRegistrar();
        registrar.setApplicationContext(applicationContext);

        // 重新初始化 SpelParser，确保 ApplicationContext 注入成功
        try {
            Method method = SpelParser.class.getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static class MyService {

        public String test() {
            return "spring bean";
        }

    }

    @Test
    void testParse() {
        Integer num = SpelParser.parse("1+1", null, Integer.class);
        Assertions.assertEquals(2, (int) num);

        String str = SpelParser.parse("'hello' + 'world'", null, String.class);
        Assertions.assertEquals("helloworld", str);

        Boolean bool = SpelParser.parse("true && false", null, Boolean.class);
        Assertions.assertFalse(bool);

        Double decimal = SpelParser.parse("1.1 + 2.2", null, Double.class);
        Assertions.assertEquals(3.3, decimal, 0.0001);

        String springBeanStr = SpelParser.parse("@myService.test()", null, String.class);
        Assertions.assertEquals("spring bean", springBeanStr);

        // ----- 异常情况 ------

        // 表达式结果为空
        Integer nullValue = null;
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("#this", nullValue, Integer.class));

        // 表达式计算结果类型不匹配
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("1+1", null, String.class));

        // 表达式计算异常
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("1/0", null));

        // 表达式解析异常
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("#this.aaa", null));
    }

}
