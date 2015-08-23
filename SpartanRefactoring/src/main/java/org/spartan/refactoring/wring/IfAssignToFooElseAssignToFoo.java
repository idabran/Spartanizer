package org.spartan.refactoring.wring;
import static org.spartan.refactoring.utils.Funcs.compatible;
import static org.spartan.refactoring.utils.Funcs.elze;
import static org.spartan.refactoring.utils.Funcs.then;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.spartan.refactoring.utils.Extract;
import org.spartan.refactoring.utils.Subject;

/**
 * A {@link Wring} to convert
 *
 * <pre>
 * if (x)
 *   a += 3;
 * else
 *   a += 9;
 * </pre>
 *
 * into
 *
 * <pre>
 * a += x ? 3 : 9;
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-29
 */
public final class IfAssignToFooElseAssignToFoo extends Wring.OfIfStatement {
  @Override Statement _replacement(final IfStatement s) {
    final Assignment then = Extract.assignment(then(s));
    final Assignment elze = Extract.assignment(elze(s));
    if (!compatible(then, elze))
      return null;
    final ConditionalExpression e = Subject.pair(then.getRightHandSide(), elze.getRightHandSide()).toCondition(s.getExpression());
    return Subject.pair(then.getLeftHandSide(), e).toStatement(then.getOperator());
  }
  @Override boolean scopeIncludes(final IfStatement s) {
    return s != null && compatible(Extract.assignment(then(s)), Extract.assignment(elze(s)));
  }
}