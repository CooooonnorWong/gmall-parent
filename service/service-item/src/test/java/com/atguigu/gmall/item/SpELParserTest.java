package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author Connor
 * @date 2022/9/1
 */
public class SpELParserTest {

    @Test
    public void test() {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext context = new TemplateParserContext();
//        Expression expression = parser.parseExpression("hello #{1 + 1}", context);
        Expression expression = parser.parseExpression("2 + 2");
        Object value = expression.getValue();
        System.out.println("value = " + value);
    }
}
