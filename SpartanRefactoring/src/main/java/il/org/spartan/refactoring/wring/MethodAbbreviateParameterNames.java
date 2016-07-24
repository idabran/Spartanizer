package il.org.spartan.refactoring.wring;

import static il.org.spartan.idiomatic.*;
import static il.org.spartan.refactoring.utils.Funcs.*;
import static il.org.spartan.refactoring.wring.Wrings.*;
import il.org.spartan.refactoring.preferences.*;
import il.org.spartan.refactoring.utils.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

/** A {@link Wring} that abbreviates the names of variables that have a generic
 * variation. The abbreviated name is the first character in the last word of
 * the variable's name.
 *
 * @author Daniel Mittelman <code><mittelmania [at] gmail.com></code>
 * @since 2015/08/24 */
/* TODO This is a previous version of the MethodParameterAbbreviate wring that
 * replaces all parameter names in a method at once. If it is found to be
 * useless in the near future, delete this class. Otherwise, remove the
 * 
 * @Deprecated annotation */
@Deprecated public class MethodAbbreviateParameterNames extends Wring<MethodDeclaration> implements Kind.RENAME_PARAMETERS {
  private static List<SingleVariableDeclaration> find(final List<SingleVariableDeclaration> ds) {
    final List<SingleVariableDeclaration> $ = new ArrayList<>();
    for (final SingleVariableDeclaration d : ds)
      if (suitable(d))
        $.add(d);
    return unless($.isEmpty()).eval($);
  }
  private static boolean isShort(final SingleVariableDeclaration d) {
    final String n = Funcs.shortName(d.getType());
    return n != null && (n + pluralVariadic(d)).equals(d.getName().getIdentifier());
  }
  private static boolean legal(final SingleVariableDeclaration d, final MethodDeclaration m, final Collection<SimpleName> newNames) {
    if (Funcs.shortName(d.getType()) == null)
      return false;
    final MethodExplorer e = new MethodExplorer(m);
    for (final SimpleName n : e.localVariables())
      if (n.getIdentifier().equals(Funcs.shortName(d.getType())))
        return false;
    for (final SimpleName n : newNames)
      if (n.getIdentifier().equals(Funcs.shortName(d.getType())))
        return false;
    for (final SingleVariableDeclaration n : expose.parameters(m))
      if (n.getName().getIdentifier().equals(Funcs.shortName(d.getType())))
        return false;
    return !m.getName().getIdentifier().equalsIgnoreCase(Funcs.shortName(d.getType()));
  }
  static private String pluralVariadic(final SingleVariableDeclaration d) {
    return d.isVarargs() ? "s" : "";
  }
  private static boolean suitable(final SingleVariableDeclaration d) {
    return new JavaTypeNameParser(d.getType().toString()).isGenericVariation(d.getName().getIdentifier()) && !isShort(d);
  }
  @Override String description(final MethodDeclaration d) {
    return d.getName().toString();
  }
  @Override Rewrite make(final MethodDeclaration d, final ExclusionManager exclude) {
    if (d.isConstructor())
      return null;
    final List<SingleVariableDeclaration> vd = find(expose.parameters(d));
    final Map<SimpleName, SimpleName> renameMap = new HashMap<>();
    if (vd == null)
      return null;
    for (final SingleVariableDeclaration v : vd)
      if (legal(v, d, renameMap.values()))
        renameMap.put(v.getName(), newSimpleName(d, Funcs.shortName(v.getType()) + pluralVariadic(v)));
    if (renameMap.isEmpty())
      return null;
    if (exclude != null)
      exclude.exclude(d);
    return new Rewrite("Abbreviate parameters in method " + d.getName().toString(), d) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        for (final SimpleName key : renameMap.keySet())
          rename(key, renameMap.get(key), d, r, g);
      }
    };
  }
}
