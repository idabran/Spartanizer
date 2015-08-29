package org.spartan.refactoring.wring;

import static org.spartan.refactoring.wring.Wrings.emptyElse;
import static org.spartan.refactoring.wring.Wrings.emptyThen;

import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;
import org.spartan.refactoring.utils.Rewrite;

/**
 * A {@link Wring} to eliminate degenerate if statements such as
 * <code>if (x) ; else ;</code>
 *
 * @author Yossi Gil
 * @since 2015-08-26
 */
public final class IfEmptyThenEmptyElse extends Wring<IfStatement> {
  @Override final boolean eligible(final IfStatement s) {
    assert scopeIncludes(s);
    return true;
  }
  @Override final Rewrite make(final IfStatement s) {
    return new Rewrite(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        s.setElseStatement(null);
        r.remove(s, g);
      }
    };
  }
  @Override boolean scopeIncludes(final IfStatement s) {
    return s != null && emptyThen(s) && emptyElse(s);
  }
  @Override String description(@SuppressWarnings("unused") final IfStatement _) {
    return "Remove 'if' statement with vacous 'then' and 'else' parts";
  }
}