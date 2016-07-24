package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;
import il.org.spartan.refactoring.preferences.*;

import org.eclipse.jdt.core.dom.*;

/** A {@link Wring} to remove unnecessary uses of Boolean.valueOf, for example by
 * converting <code>
 * 
 * <pre> Boolean b = Boolean.valueOf(true) </pre>
 * 
 * <code> into <code>
 * 
 * <pre> Boolean b = Boolean.TRUE </pre>
 * 
 * <code>
 *
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 *
 * @since 2016-04-04 */
public class BooleanConstants extends Wring.ReplaceCurrentNode<MethodInvocation> implements Kind.ConsolidateStatements {
  @Override String description(@SuppressWarnings("unused") final MethodInvocation __) {
    return "Use built-in boolean constant instead of valueOf()";
  }
  @Override ASTNode replacement(final MethodInvocation i) {
    return i.getExpression() == null || !"Boolean".equals(i.getExpression().toString()) || !"valueOf".equals(i.getName().getIdentifier()) || i.arguments().size() != 1
        || !"true".equals(i.arguments().get(0).toString()) && !"false".equals(i.arguments().get(0).toString()) ? null : i.getAST().newQualifiedName(i.getAST().newName("Boolean"),
        newSimpleName(i, ((BooleanLiteral) i.arguments().get(0)).booleanValue() ? "TRUE" : "FALSE"));
  }
}