package cn.sticki.spel.validator.jakarta.bean

import cn.sticki.spel.validator.constrain.SpelNotBlank
import cn.sticki.spel.validator.jakarta.SpelValid
import cn.sticki.spel.validator.test.util.VerifyFailedField
import cn.sticki.spel.validator.test.util.VerifyObject

/**
 * Kotlin类测试Bean
 *
 * @author 阿杆
 * @since 2025/11/21
 */
class KotlinClassTestBean {

    class ClassTest {
        @SpelNotBlank
        var name: String = ""
    }

    @SpelValid
    data class DataClassTest(
        @SpelNotBlank
        var name: String? = ""
    )

    companion object {
        @JvmStatic
        fun testClass(): List<VerifyObject> {
            return listOf(
                VerifyObject.of(
                    ClassTest(),
                    VerifyFailedField.of(ClassTest::name.name)
                )
            )
        }

        @JvmStatic
        fun testDataClass(): List<VerifyObject> {
            return listOf(
                VerifyObject.of(
                    DataClassTest(),
                    VerifyFailedField.of(DataClassTest::name.name)
                )
            )
        }

    }
}
