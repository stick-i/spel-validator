package cn.sticki.spel.validator.core.parse;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ApplicationContext 注入回调，用于在容器就绪后主动绑定 SpEL 的 BeanResolver。
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/4/29
 */
public class SpelValidatorBeanRegistrar implements ApplicationContextAware {

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpelParser.bindBeanResolver(applicationContext);
    }

}
