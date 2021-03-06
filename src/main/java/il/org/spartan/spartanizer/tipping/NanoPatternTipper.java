package il.org.spartan.spartanizer.tipping;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.utils.*;

/** A {@link Tipper} in which {@link #tip(ASTNode)} is throwing because it is
 * not implemented.
 * @author Ori Marcovitch
 * @year 2016 */
public abstract class NanoPatternTipper<N extends ASTNode> extends Tipper<N> {
  @Override public final boolean canTip(final N ¢) {
    if (prerequisite(¢))
      Counter.count(this.getClass());
    return prerequisite(¢);
  }

  protected abstract boolean prerequisite(final N ¢);
}
