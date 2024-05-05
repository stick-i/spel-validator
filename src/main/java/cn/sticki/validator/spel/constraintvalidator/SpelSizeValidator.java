package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelSize;
import cn.sticki.validator.spel.parse.SpelParser;
import cn.sticki.validator.spel.result.FieldValidResult;
import cn.sticki.validator.spel.util.CalcLengthUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * {@link SpelSize} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelSizeValidator implements SpelConstraintValidator<SpelSize> {

	@Override
	public FieldValidResult isValid(SpelSize annotation, Object obj, Field field) throws IllegalAccessException {
		Object fieldValue = field.get(obj);
		// 元素为null是被允许的
		if (fieldValue == null) {
			return FieldValidResult.success();
		}

		// 计算字段内容的大小或长度
		int size = CalcLengthUtil.calcFieldSize(fieldValue);

		// 计算表达式的值
		Integer min = this.calcExpression(annotation.min(), obj);
		Integer max = this.calcExpression(annotation.max(), obj);

		// 构造错误信息
		String fieldSign = CalcLengthUtil.getFieldSign(fieldValue);
		String message = null;
		if (min != null && max != null && (size < min || size > max)) {
			message = fieldSign + "必须在" + min + "和" + max + "之间";
		} else if (min != null && size < min) {
			message = fieldSign + "必须大于等于" + min;
		} else if (max != null && size > max) {
			message = fieldSign + "必须小于等于" + max;
		}

		if (message != null) {
			return new FieldValidResult(false, message);
		}

		return FieldValidResult.success();
	}

	private Integer calcExpression(String expression, Object obj) {
		if (!StringUtils.hasText(expression)) {
			return null;
		}
		return SpelParser.parse(expression, obj, Integer.class);
	}

	@Override
	public Set<Class<?>> supportType() {
		return CalcLengthUtil.SUPPORT_TYPE;
	}

}
