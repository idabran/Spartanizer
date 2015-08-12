package org.spartan.refactoring.utils;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.spartan.hamcrest.MatcherAssert.assertThat;
import static org.spartan.hamcrest.MatcherAssert.iz;
import static org.spartan.refactoring.spartanizations.Into.e;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.junit.Test;
import org.spartan.refactoring.utils.Subject.Pair;

@SuppressWarnings({ "javadoc", "static-method" }) public class PlantTest {
  @Test public void vanilla() {
    Subject.operand(e("a")).to(PrefixExpression.Operator.NOT);
  }
  @Test public void vanillaCorrectResult() {
    assertThat(Subject.operand(e("a")).to(PrefixExpression.Operator.NOT), iz("!a"));
  }
  @Test public void notPeels() {
    assertThat(Subject.operand(e("((a))")).to(PrefixExpression.Operator.NOT), iz("!a"));
  }
  @Test public void multiplicationOfAddition() {
    assertThat(Subject.pair(e("a+B"), e("c+d")).to(InfixExpression.Operator.TIMES), iz("(a + B) * (c + d)"));
  }
  @Test public void multiplicationOfMultiplicatoin() {
    assertThat(Subject.pair(e("a*B"), e("c*d")).to(InfixExpression.Operator.TIMES), iz("a * B * c * d"));
  }
  @Test public void assignment() {
    assertThat(Subject.pair(e("a"), e("b")).to(Assignment.Operator.ASSIGN), iz("a=b"));
    assertThat(Subject.pair(e("a"), e("b")).to(Assignment.Operator.PLUS_ASSIGN), iz("a+=b"));
    assertThat(Subject.pair(e("a"), e("b")).to(Assignment.Operator. RIGHT_SHIFT_UNSIGNED_ASSIGN), iz("a>>>=b"));
  }
  @Test public void substractionsDoesntAssociate() {
    assertThat(Subject.pair(e("a-B"), e("c-d")).to(InfixExpression.Operator.MINUS), iz("(a - B) - (c - d)"));
  }
  @Test public void divisionDoesntAssociate() {
    assertThat(Subject.pair(e("a*B"), e("c*d")).to(InfixExpression.Operator.DIVIDE), iz("(a * B) / (c * d)"));
  }
  @Test public void remainderDoesntAssociate() {
    assertThat(Subject.pair(e("a*B"), e("c*d")).to(InfixExpression.Operator.REMAINDER), iz("(a * B) % (c * d)"));
  }
  @Test public void extractcoreLeft() {
    assertThat(Subject.pair(e("((a-B))"), e("c-d")).to(InfixExpression.Operator.PLUS), iz("a - B + c - d"));
  }
  @Test public void extractcoreRight() {
    assertThat(Subject.pair(e("a-B"), e("(c-d)")).to(InfixExpression.Operator.PLUS), iz("a - B + c - d"));
  }
  @Test public void conditionalSimple() {
    final Pair pair = Subject.pair(e("a-B"), e("(c-d)"));
    assertThat(pair, notNullValue());
    assertThat(pair.toCondition(e("x")), iz("x ? a-B : c-d"));
  }
  @Test public void conditionalExtract() {
    final Pair pair = Subject.pair(e("a-B"), e("(c-d)"));
    assertThat(pair, notNullValue());
    assertThat(pair.toCondition(e("(x)")), iz("x ? a-B : c-d"));
  }
  @Test public void postfix() {
    assertThat(Subject.operand(e("a")).to(PostfixExpression.Operator.INCREMENT), iz("a++"));
    assertThat(Subject.operand(e("a")).to(PostfixExpression.Operator.DECREMENT), iz("a--"));
  }
  @Test public void postfixPeels() {
    assertThat(Subject.operand(e("((a))")).to(PostfixExpression.Operator.INCREMENT), iz("a++"));
    assertThat(Subject.operand(e("((a))")).to(PostfixExpression.Operator.DECREMENT), iz("a--"));
  }
  @Test public void operandsVanilla() {
    assertThat(Subject.operands(e("((a))"),e("b"), e("c")).to(InfixExpression.Operator.PLUS), iz("a+b+c"));
  }
  @Test public void operandsParenthesisLeft() {
    assertThat(Subject.operands(e("((a+b))"),e("b"), e("c")).to(InfixExpression.Operator.TIMES), iz("(a+b)*b*c"));
  }
  @Test public void operandsParenthesisRight() {
    assertThat(Subject.operands(e("((a))"),e("b+c"), e("c")).to(InfixExpression.Operator.TIMES), iz("a*(b+c)*c"));
  }
  @Test public void operandsParenthesisRest() {
    assertThat(Subject.operands(e("((a))"),e("b+c"), e("c+d")).to(InfixExpression.Operator.TIMES), iz("a*(b+c)*(c+d)"));
  }
  @Test public void operandsNoParenthesisRest() {
    assertThat(Subject.operands(e("((a))"),e("b+c"), e("c+d")).to(InfixExpression.Operator.PLUS), iz("a+b+c+c+d"));
  }
}