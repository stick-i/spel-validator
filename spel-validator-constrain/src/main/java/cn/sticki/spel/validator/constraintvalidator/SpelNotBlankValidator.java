package cn.sticki.spel.validator.constraintvalidator;

import cn.sticki.spel.validator.constrain.SpelNotBlank;
import cn.sticki.spel.validator.core.SpelConstraintValidator;
import cn.sticki.spel.validator.core.result.FieldValidResult;
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

    private static final Set<Class<?>> SUPPORT_TYPE = Collections.singleton(CharSequence.class);

    @Override
    public FieldValidResult isValid(SpelNotBlank annotation, Object obj, Field field) throws IllegalAccessException {
        CharSequence fieldValue = (CharSequence) field.get(obj);
        return new FieldValidResult(StringUtils.hasText(fieldValue));
    }

    @Override
    public Set<Class<?>> supportType() {
        return SUPPORT_TYPE;
    }

}
