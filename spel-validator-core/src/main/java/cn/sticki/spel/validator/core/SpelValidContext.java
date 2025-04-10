package cn.sticki.spel.validator.core;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * Spel校验上下文
 *
 * @author 阿杆
 * @since 2025/4/10
 */
@Builder
@ToString
@EqualsAndHashCode
public class SpelValidContext {

    Locale locale;

    Set<Object> validateGroups;

    private static final SpelValidContext DEFAULT = SpelValidContext.builder().build();

    public static SpelValidContext getDefault() {
        return DEFAULT;
    }

    public Locale getLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }

    public Set<Object> getValidateGroups() {
        return validateGroups == null ? Collections.emptySet() : validateGroups;
    }

}
