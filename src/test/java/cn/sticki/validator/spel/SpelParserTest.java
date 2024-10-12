package cn.sticki.validator.spel;

import cn.sticki.validator.spel.exception.SpelParserException;
import cn.sticki.validator.spel.parse.SpelParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SpelParser 测试
 *
 * @author 阿杆
 * @version 1.0
 * @since 2024/10/12
 */
public class SpelParserTest {

    @Test
    void testParse() {
        Integer num = SpelParser.parse("1+1", null, Integer.class);
        Assertions.assertEquals(2, (int) num);

        String str = SpelParser.parse("'hello' + 'world'", null, String.class);
        Assertions.assertEquals("helloworld", str);

        Boolean bool = SpelParser.parse("true && false", null, Boolean.class);
        Assertions.assertFalse(bool);

        Double decimal = SpelParser.parse("1.1 + 2.2", null, Double.class);
        Assertions.assertEquals(3.3, decimal, 0.0001);

        // ----- 异常情况 ------

        // 表达式结果为空
        Integer nullValue = null;
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("#this", nullValue, Integer.class));

        // 表达式计算结果类型不匹配
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("1+1", null, String.class));

        // 表达式计算异常
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("1/0", null));

        // 表达式解析异常
        Assertions.assertThrows(SpelParserException.class, () -> SpelParser.parse("#this.aaa", null));
    }

}
