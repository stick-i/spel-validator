package cn.sticki.validator.spel.parse;

import cn.sticki.validator.spel.exception.SpelParserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
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

	private static final SpelExpressionParser parser = new SpelExpressionParser();

	private static final StandardEvaluationContext context = new StandardEvaluationContext();

	private static final Map<String, Expression> expressionMap = new ConcurrentHashMap<>();

	static {
		ApplicationContext applicationContext = SpelValidatorBeanRegistrar.getApplicationContext();
		if (applicationContext != null) {
			AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
			context.setBeanResolver(new BeanFactoryResolver(beanFactory));
		} else {
			log.warn("ApplicationContext is null, SpelParser will not support spring bean reference");
			log.warn("If you want to use spring bean reference in SpelParser, please use @EnableSpelValidatorBeanRegistrar to enable ApplicationContext support");
		}
	}

	/**
	 * 解析表达式
	 *
	 * @param expression 表达式
	 * @param rootObject 用于计算表达式的根对象
	 * @return 表达式计算结果
	 */
	public static Object parse(String expression, Object rootObject) {
		try {
			log.debug("======> Parse expression [{}]", expression);
			updateExpressionCache(expression);
			Object value = parser.parseExpression(expression).getValue(context, rootObject, Object.class);
			log.debug("======> Parse result [{}]", value);
			return value;
		} catch (Exception e) {
			log.error("Parse expression error, expression [{}], message [{}]", expression, e.getMessage());
			throw new SpelParserException(e);
		}
	}

	private static void updateExpressionCache(String expression) {
		if (expressionMap.containsKey(expression)) {
			return;
		}
		synchronized (expressionMap) {
			if (!expressionMap.containsKey(expression)) {
				expressionMap.put(expression, parser.parseExpression(expression));
			}
		}
	}

	/**
	 * 解析表达式
	 *
	 * @param <T>          返回值类型
	 * @param expression   表达式
	 * @param rootObject   用于计算表达式的根对象
	 * @param requiredType 指定返回值的类型
	 * @return 表达式计算结果
	 * @throws SpelParserException 当表达式计算结果为null或者不是指定类型时抛出
	 */
	public static <T> T parse(String expression, Object rootObject, Class<T> requiredType) {
		Object any = parse(expression, rootObject);
		if (any == null) {
			throw new SpelParserException("Expression [" + expression + "] calculate result can not be null");
		}
		if (!requiredType.isInstance(any)) {
			throw new SpelParserException("Expression [" + expression + "] calculate result must be [" + requiredType.getName() + "]");
		}
		return requiredType.cast(any);
	}

}
