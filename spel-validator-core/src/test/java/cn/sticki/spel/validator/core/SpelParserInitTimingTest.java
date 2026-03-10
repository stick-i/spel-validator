package cn.sticki.spel.validator.core;

import cn.sticki.spel.validator.core.parse.SpelParser;
import cn.sticki.spel.validator.core.parse.SpelValidatorBeanRegistrar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.expression.BeanResolver;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;

/**
 * SpelParser 初始化时机相关测试
 *
 * @author 阿杆
 * @since 2026/2/9
 */
@Isolated
public class SpelParserInitTimingTest {

    public static class MyService {

        public String test() {
            return "spring bean";
        }

    }

    /**
     * 验证当前行为：
     * SpelParser 初始化后，再注入 ApplicationContext 也能在后续解析阶段自动完成 BeanResolver 绑定。
     */
    @Test
    void testApplicationContextInjectedAfterInitCanAutoBindBeanResolver() throws Exception {
        Field beanResolverField = SpelParser.class.getDeclaredField("beanResolver");
        beanResolverField.setAccessible(true);
        BeanResolver originalResolver = (BeanResolver) beanResolverField.get(null);

        GenericApplicationContext testContext = new GenericApplicationContext();
        try {
            // 清空现场，模拟首次初始化时 ApplicationContext 尚不可用
            beanResolverField.set(null, null);

            Integer parseResult = SpelParser.parse("1+1", null, Integer.class);
            Assertions.assertEquals(2, parseResult.intValue());

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MyService.class);
            testContext.registerBeanDefinition("myService", builder.getBeanDefinition());
            testContext.refresh();
            new SpelValidatorBeanRegistrar().setApplicationContext(testContext);

            String value = SpelParser.parse("@myService.test()", null, String.class);
            Assertions.assertEquals("spring bean", value);
        } finally {
            if (testContext.isActive()) {
                testContext.close();
            }
            beanResolverField.set(null, originalResolver);
        }
    }
}
