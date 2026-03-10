package cn.sticki.spel.validator.core.parse;

import cn.sticki.spel.validator.core.exception.SpelParserException;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spel表达式解析工具
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
@SuppressWarnings("AlibabaConstantFieldShouldBeUpperCase")
@Slf4j
public class SpelParser {

    private SpelParser() {
    }

    private static final SpelExpressionParser parser = new SpelExpressionParser();

    private static volatile BeanResolver beanResolver;

    private static final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    // 静态初始化仅输出一次启动日志，BeanResolver 绑定由 SpelValidatorBeanRegistrar 主动触发。
    static {
        logInitInfo();
    }

    private static void logInitInfo() {
        if (beanResolver == null) {
            log.info("SpelParser initialized without BeanResolver, spring bean reference is temporarily unavailable");
            log.info("If you want to use spring bean reference in SpelParser, please use @EnableSpelValidatorBeanRegistrar to enable ApplicationContext support");
        } else {
            log.debug("SpelParser initialized with BeanResolver");
        }
        log.debug("SpelParser init log complete");
    }

    /**
     * 绑定 Spring BeanResolver。
     * 该方法由 {@link SpelValidatorBeanRegistrar} 在 ApplicationContext 注入后主动调用。
     */
    static void bindBeanResolver(@NotNull ApplicationContext applicationContext) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        beanResolver = new BeanFactoryResolver(beanFactory);
        log.debug("SpelParser bind bean resolver success");
    }

    private static StandardEvaluationContext createEvaluationContext() {
        StandardEvaluationContext context = new StandardEvaluationContext();
        BeanResolver resolver = beanResolver;
        if (resolver != null) {
            context.setBeanResolver(resolver);
        }
        return context;
    }

    /**
     * 解析表达式
     *
     * @param expression 表达式
     * @param rootObject 用于计算表达式的根对象
     * @return 表达式计算结果。若为基本数据类型，则会自动转为包装类型。
     */
    @Nullable
    public static Object parse(@Language("spel") String expression, Object rootObject) {
        try {
            log.debug("======> Parse expression [{}]", expression);
            Expression parsed = expressionCache.computeIfAbsent(expression, parser::parseExpression);
            Object value = parsed.getValue(createEvaluationContext(), rootObject, Object.class);
            log.debug("======> Parse result [{}]", value);
            return value;
        } catch (RuntimeException e) {
            throw new SpelParserException("Parse expression error, expression [" + expression + "], message [" + e.getMessage() + "]", e);
        }
    }

    /**
     * 解析表达式
     *
     * @param <T>          返回值类型
     * @param expression   表达式
     * @param rootObject   用于计算表达式的根对象
     * @param requiredType 指定返回值的类型
     * @return 表达式计算结果。若为基本数据类型，则会自动转为包装类型。
     * @throws SpelParserException 当表达式计算结果为null或者不是指定类型时抛出
     */
    @NotNull
    public static <T> T parse(@Language("spel") String expression, Object rootObject, Class<T> requiredType) {
        Object any = parse(expression, rootObject);
        if (any == null) {
            throw new SpelParserException("Expression [" + expression + "] evaluated to null, expected type [" + requiredType.getName() + "]. Please check if the SpEL expression is correct.");
        }
        if (!requiredType.isInstance(any)) {
            throw new SpelParserException("Expression [" + expression + "] evaluated to type [" + any.getClass().getName() + "], expected type [" + requiredType.getName() + "]. Please check if the SpEL expression is correct.");
        }
        //noinspection unchecked
        return (T) any;
    }

}
