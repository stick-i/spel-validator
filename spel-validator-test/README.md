# 模块说明

此模板内包含一些测试使用的工具，其他模块引入此模块时都需要以 `<scope>test</scope>` 的方式引入。

刚开始只有一个 `spel-validator`，里面包含了所有的代码。

后来需要兼容 `javax` 和 `jakarta` 的包，所以将代码分成了三个模块，`spel-validator-core`、`spel-validator-jakarta` 和 `spel-validator-javax`。
此时测试工具代码在 `spel-validator-core` 的测试目录中，但 `-javax` 和 `-jakarta` 模块也需要使用这些测试工具，
而测试目录的代码不会被编译到 jar 包中，无法传递给其他模块。

然后我就把测试代码单独提取出来，放到了 `spel-validator-test` 模块中，再让其他三个模块进行引入。
