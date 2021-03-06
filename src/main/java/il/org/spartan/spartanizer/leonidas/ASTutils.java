package il.org.spartan.spartanizer.leonidas;

import static org.junit.Assert.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.cmdline.*;

/** @author Ori Marcovitch
 * @since 2016 */
public class ASTutils {
  public static ASTNode extractASTNode(final String s, final CompilationUnit u) {
    switch (GuessedContext.find(s)) {
      case COMPILATION_UNIT_LOOK_ALIKE:
        return u;
      case EXPRESSION_LOOK_ALIKE:
        return findSecond(Expression.class, findFirst.methodDeclaration(u));
      case METHOD_LOOKALIKE:
        return findFirst.instanceOf(MethodDeclaration.class, u);
      case OUTER_TYPE_LOOKALIKE:
        return u;
      case STATEMENTS_LOOK_ALIKE:
        return findFirst.instanceOf(Block.class, u);
      default:
        break;
    }
    return null;
  }

  public static String extractCode(final String s, final Document d) {
    switch (GuessedContext.find(s)) {
      case EXPRESSION_LOOK_ALIKE:
        return d.get().substring(23, d.get().length() - 3);
      case METHOD_LOOKALIKE:
        return d.get().substring(8, d.get().length() - 1);
      case STATEMENTS_LOOK_ALIKE:
        return d.get().substring(16, d.get().length() - 2);
      default:
        return d.get();
    }
  }

  public static String wrapCode(final String ¢) {
    switch (GuessedContext.find(¢)) {
      case COMPILATION_UNIT_LOOK_ALIKE:
        return ¢;
      case EXPRESSION_LOOK_ALIKE:
        return "class X{int f(){return " + ¢ + ";}}";
      case METHOD_LOOKALIKE:
        return "class X{" + ¢ + "}";
      case OUTER_TYPE_LOOKALIKE:
        return ¢;
      case STATEMENTS_LOOK_ALIKE:
        return "class X{int f(){" + ¢ + "}}";
      default:
        fail(¢ + " is not like anything I know...");
    }
    return null;
  }

  static <N extends ASTNode> N findSecond(final Class<?> c, final ASTNode n) {
    if (n == null)
      return null;
    final Wrapper<Boolean> foundFirst = new Wrapper<>();
    foundFirst.set(Boolean.FALSE);
    final Wrapper<ASTNode> $ = new Wrapper<>();
    n.accept(new ASTVisitor() {
      @Override public boolean preVisit2(final ASTNode ¢) {
        if ($.get() != null)
          return false;
        if (¢.getClass() != c && !c.isAssignableFrom(¢.getClass()))
          return true;
        if (foundFirst.get().booleanValue()) {
          $.set(¢);
          assert $.get() == ¢;
          return false;
        }
        foundFirst.set(Boolean.TRUE);
        return true;
      }
    });
    @SuppressWarnings("unchecked") final N $$ = (N) $.get();
    return $$;
  }
}
