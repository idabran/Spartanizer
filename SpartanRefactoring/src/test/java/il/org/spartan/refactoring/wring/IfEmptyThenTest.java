package il.org.spartan.refactoring.wring;

import il.org.spartan.*;
import il.org.spartan.refactoring.utils.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import static il.org.spartan.azzert.*;

@SuppressWarnings({ "javadoc", "static-method" })//
public class IfEmptyThenTest {
  private static final Statement INPUT = Into.s("{if (b) ; else ff();}");
  private static final IfStatement IF = extract.firstIfStatement(INPUT);
  private static final IfEmptyThen WRING = new IfEmptyThen();

  @Test public void eligible() {
    azzert.aye(WRING.eligible(IF));
  }
  @Test public void emptyThen() {
    azzert.aye(Is.vacuousThen(IF));
  }
  @Test public void extractFirstIf() {
    azzert.that(IF, notNullValue());
  }
  @Test public void inputType() {
    azzert.that(INPUT, instanceOf(Block.class));
  }
  @Test public void scopeIncludes() {
    azzert.aye(WRING.scopeIncludes(IF));
  }
}
