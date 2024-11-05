package cn.sticki.spel.validator.core.parse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ApplicationContext工具类，便于在一些非Spring管理的类中使用ApplicationContext的功能
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
@Slf4j
public class SpelValidatorBeanRegistrar implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpelValidatorBeanRegistrar.applicationContext = applicationContext;
    }

}
