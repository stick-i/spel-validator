package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelNull;
import cn.sticki.validator.spel.result.FieldValidResult;

import java.lang.reflect.Field;

/**
 * {@link SpelNull} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNullValidator implements SpelConstraintValidator<SpelNull> {

	@Override
	public FieldValidResult isValid(SpelNull annotation, Object obj, Field field) throws IllegalAccessException {
		return new FieldValidResult(field.get(obj) == null);
	}

}
