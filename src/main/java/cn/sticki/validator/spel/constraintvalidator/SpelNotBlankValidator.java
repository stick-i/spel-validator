package cn.sticki.validator.spel.constraintvalidator;

import cn.sticki.validator.spel.SpelConstraintValidator;
import cn.sticki.validator.spel.constrain.SpelNotBlank;
import cn.sticki.validator.spel.result.FieldValidResult;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

/**
 * {@link SpelNotBlank} 注解校验器。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/5/5
 */
public class SpelNotBlankValidator implements SpelConstraintValidator<SpelNotBlank> {

	@Override
	public FieldValidResult isValid(SpelNotBlank annotation, Object obj, Field field) throws IllegalAccessException {
		CharSequence fieldValue = (CharSequence) field.get(obj);
		return new FieldValidResult(StringUtils.hasText(fieldValue));
	}

	private static final Set<Class<?>> SUPPORT_TYPE = Collections.singleton(CharSequence.class);

	@Override
	public Set<Class<?>> supportType() {
		return SUPPORT_TYPE;
	}

}
