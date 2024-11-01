# 模块说明

此模块为校验器核心模块，提供了校验器的基本功能，包括校验器的注册、执行、结果汇总等。

该模块可以被单独使用，只需要遵循校验器的规范 [SpelConstraint](src/main/java/cn/sticki/spel/validator/core/SpelConstraint.java)，
并实现自己需要的约束注解即可。

但一般情况下，建议直接使用 `-javax` 或 `-jakarta` 模块，因为这两个模块已经提供了一些常用的约束注解和启动注解。
