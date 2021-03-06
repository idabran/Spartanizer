package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.Utils.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import il.org.spartan.spartanizer.ast.create.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.tipping.*;

/** convert <code> if(a)f();else{g();return;} </code> into
 * <code>if(a)f();else{g();;}</code> provided that this <code><b>if</b> </code>
 * statement is the last statement in a method.
 * @author Yossi Gil
 * @author Daniel Mittelman <tt><mittelmania [at] gmail.com></tt>
 * @since 2015-09-09 */
public final class IfLastInMethodElseEndingWithEmptyReturn extends EagerTipper<IfStatement> implements TipperCategory.EarlyReturn {
  @Override @SuppressWarnings("unused") public String description(final IfStatement ____) {
    return "Remove redundant return statement in 'else' branch of if statement that terminates this method";
  }

  @Override public Tip tip(final IfStatement s) {
    final Block b = az.block(s.getParent());
    if (b == null || !(b.getParent() instanceof MethodDeclaration) || !lastIn(s, statements(b)))
      return null;
    final ReturnStatement deleteMe = az.returnStatement(hop.lastStatement(elze(s)));
    return deleteMe == null || deleteMe.getExpression() != null ? null : new Tip(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        r.replace(deleteMe, make.emptyStatement(s), g);
      }
    };
  }
}
