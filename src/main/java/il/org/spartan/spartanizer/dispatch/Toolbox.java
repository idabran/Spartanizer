package il.org.spartan.spartanizer.dispatch;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.plugin.*;
import il.org.spartan.spartanizer.tippers.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.spartanizer.utils.*;

/** Singleton containing all {@link Tipper}s which are active, allowing
 * selecting and applying the most appropriate such object for a given
 * {@link ASTNode}.
 * @author Yossi Gil
 * @since 2015-08-22 */
public class Toolbox {
  @SuppressWarnings({ "serial", "unchecked" }) //
  static final Map<Class<? extends ASTNode>, Integer> //
  classToNodeType //
      = new LinkedHashMap<Class<? extends ASTNode>, Integer>() {
        {
          for (int nodeType = 1;; ++nodeType)
            try {
              monitor.debug("Searching for " + nodeType);
              final Class<? extends ASTNode> nodeClassForType = ASTNode.nodeClassForType(nodeType);
              monitor.debug("Found for " + nodeClassForType);
              put(nodeClassForType, Integer.valueOf(nodeType));
            } catch (final IllegalArgumentException x) {
              monitor.debug(this, x);
              break;
            } catch (final Exception x) {
              monitor.logEvaluationError(this, x);
              break;
            }
        }
      };
  /** The default Instance of this class */
  static Toolbox defaultInstance;

  public static Toolbox defaultInstance() {
    // Lazy evaluation pattern.
    return defaultInstance = defaultInstance != null ? defaultInstance : freshCopyOfAllTippers();
  }

  public static Toolbox emptyToolboox() {
    return new Toolbox();
  }

  public static <N extends ASTNode> Tipper<N> findTipper(final N n, @SuppressWarnings("unchecked") final Tipper<N>... ns) {
    for (final Tipper<N> $ : ns)
      if ($.canTip(n))
        return $;
    return null;
  }

  public static Toolbox freshCopyOfAllTippers() {
    return new Toolbox()//
        .add(EnhancedForStatement.class, new EnhancedForParameterRenameToCent())//
        .add(VariableDeclarationExpression.class, new ForRenameInitializerToCent()) //
        .add(ThrowStatement.class, new ThrowNotLastInBlock()) //
        .add(ClassInstanceCreation.class, new ClassInstanceCreationValueTypes()) //
        .add(SuperConstructorInvocation.class, new SuperConstructorInvocationRemover()) //
        .add(ReturnStatement.class, new ReturnLastInMethod()) //
        .add(SingleVariableDeclaration.class, //
            new SingleVariableDeclarationAbbreviation(), //
            new SingelVariableDeclarationUnderscoreDoubled(), //
            new VariableDeclarationRenameUnderscoreToDoubleUnderscore<SingleVariableDeclaration>(), //
            new SingleVariableDeclarationEnhancedForRenameParameterToCent(), //
            null)//
        .add(ForStatement.class, //
            new BlockBreakToReturnInfiniteFor(), //
            new ReturnToBreakFiniteFor(), //
            new RemoveRedundentFor(), //
            new ForToForUpdaters(), //
            null)//
        .add(WhileStatement.class, //
            new BlockBreakToReturnInfiniteWhile(), //
            new ReturnToBreakFiniteWhile(), //
            new RemoveRedundantWhile(), //
            new WhileToForUpdaters(), //
            null) //
        .add(Assignment.class, //
            new AssignmentAndAssignment(), //
            new AssignmentAndReturn(), //
            new AssignmentToFromInfixIncludingTo(), //
            new AssignmentToPostfixIncrement(), //
            null) //
        .add(Block.class, //
            new BlockSimplify(), //
            new BlockSingleton(), //
            null) //
        .add(PostfixExpression.class, //
            new PostfixToPrefix(), //
            null) //
        .add(InfixExpression.class, //
            new InfixMultiplicationEvaluate(), //
            new InfixDivisionEvaluate(), //
            new InfixRemainderEvaluate(), //
            new InfixComparisonSizeToZero(), //
            new InfixSubtractionZero(), //
            new InfixAdditionSubtractionExpand(), //
            new InfixEmptyStringAdditionToString(), //
            new InfixConcatenationEmptyStringLeft(), //
            new InfixFactorNegatives(), //
            new InfixAdditionEvaluate(), //
            new InfixSubtractionEvaluate(), //
            new InfixTermsZero(), //
            new InfixPlusRemoveParenthesis(), //
            new InfixAdditionSort(), //
            new InfixComparisonBooleanLiteral(), //
            new InfixConditionalAndTrue(), //
            new InfixConditionalOrFalse(), //
            new InfixComparisonSpecific(), //
            new InfixMultiplicationByOne(), //
            new InfixMultiplicationByZero(), //
            new InfixMultiplicationSort(), //
            new InfixPseudoAdditionSort(), //
            new InfixSubtractionSort(), //
            new InfixDivisonSortRest(), //
            new InfixConditionalCommon(), //
            new InfixIndexOfToStringContains(), //
            null)
        .add(MethodDeclaration.class, //
            new MethodDeclarationRenameReturnToDollar(), //
            new MethodDeclarationModifiersRedundant(), //
            new BodyDeclarationModifiersSort.ofMethod(), //
            new MethodDeclarationRenameSingleParameterToCent(), //
            null)
        .add(MethodInvocation.class, //
            new MethodInvocationEqualsWithLiteralString(), //
            new MethodInvocationValueOfBooleanConstant(), //
            new MethodInvocationToStringToEmptyStringAddition(), //
            null)//
        .add(IfStatement.class, //
            new IfTrueOrFalse(), //
            new RemoveRedundantIf(), //
            new IfLastInMethodThenEndingWithEmptyReturn(), //
            new IfLastInMethodElseEndingWithEmptyReturn(), //
            new IfLastInMethod(), //
            new IfReturnFooElseReturnBar(), //
            new IfReturnNoElseReturn(), //
            new IfAssignToFooElseAssignToFoo(), //
            new IfThenFooBarElseFooBaz(), //
            new IfBarFooElseBazFoo(), //
            new IfThrowFooElseThrowBar(), //
            new IfThrowNoElseThrow(), //
            new IfExpressionStatementElseSimilarExpressionStatement(), //
            new IfThenOrElseIsCommandsFollowedBySequencer(), //
            new IfFooSequencerIfFooSameSequencer(), //
            new IfCommandsSequencerNoElseSingletonSequencer(), //
            new IfPenultimateInMethodFollowedBySingleStatement(), //
            new IfThenIfThenNoElseNoElse(), //
            new IfEmptyThenEmptyElse(), //
            new IfDegenerateElse(), //
            new IfEmptyThen(), //
            new IfShortestFirst(), //
            null)//
        .add(PrefixExpression.class, //
            new PrefixIncrementDecrementReturn(), //
            new PrefixNotPushdown(), //
            new PrefixPlusRemove(), //
            null) //
        .add(ConditionalExpression.class, //
            new TernaryBooleanLiteral(), //
            new TernaryCollapse(), //
            new TernaryEliminate(), //
            new TernaryShortestFirst(), //
            new TernaryPushdown(), //
            new TernaryPushdownStrings(), //
            null) //
        .add(TypeDeclaration.class, //
            // new delmeTypeModifierCleanInterface(), //
            new TypeRedundantModifiers(), //
            // Disabled to protect against infinite loop
            new BodyDeclarationModifiersSort.ofType(), //
            // new BodyDeclarationAnnotationsSort.ofType(), //
            null) //
        .add(EnumDeclaration.class, //
            new EnumRedundantModifiers(), new BodyDeclarationModifiersSort.ofEnum(), //
            new EnumRedundantModifiers(), new BodyDeclarationModifiersSort.ofEnum(), //
            null) //
        .add(FieldDeclaration.class, //
            new FieldRedundantModifiers(), //
            new BodyDeclarationModifiersSort.ofField(), //
            null) //
        .add(CastExpression.class, //
            new CastToDouble2Multiply1(), //
            new CastToLong2Multiply1L(), //
            null) //
        .add(EnumConstantDeclaration.class, //
            new EnumConstantRedundantModifiers(), //
            new BodyDeclarationModifiersSort.ofEnumConstant(), //
            // new BodyDeclarationAnnotationsSort.ofEnumConstant(), //
            null) //
        .add(NormalAnnotation.class, //
            new AnnotationDiscardValueName(), //
            new AnnotationRemoveEmptyParentheses(), //
            null) //
        .add(Initializer.class, new BodyDeclarationModifiersSort.ofInitializer(), //
            null) //
        .add(VariableDeclarationFragment.class, new DeclarationRedundantInitializer(), //
            new DeclarationAssignment(), //
            new DeclarationInitialiazelUpdateAssignment(), //
            new DeclarationInitializerIfAssignment(), //
            new DeclarationInitializerIfUpdateAssignment(), //
            new DeclarationInitializerReturnVariable(), //
            new DeclarationInitializerReturnExpression(), //
            new DeclarationInitializerReturnAssignment(), //
            new DeclarationInitializerReturnUpdateAssignment(), //
            new DeclarationInitializerStatementTerminatingScope(), //
            new DeclarationInitialiazerAssignment(), //
            new VariableDeclarationRenameUnderscoreToDoubleUnderscore<VariableDeclarationFragment>(), //
            new ForToForInitializers(), //
            new WhileToForInitializers(), //
            null) //
    //
    ;
  }

  /** Make a {@link Toolbox} for a specific kind of tippers
   * @param clazz JD
   * @param w JS
   * @return a new defaultInstance containing only the tippers passed as
   *         parameter */
  @SafeVarargs public static <N extends ASTNode> Toolbox make(final Class<N> clazz, final Tipper<N>... ns) {
    return emptyToolboox().add(clazz, ns);
  }

  public static void refresh() {
    defaultInstance = freshCopyOfAllTippers();
  }

  private static void disable(final Class<? extends TipperCategory> c, final List<Tipper<? extends ASTNode>> ns) {
    removing: for (;;) {
      for (int ¢ = 0; ¢ < ns.size(); ++¢)
        if (c.isAssignableFrom(ns.get(¢).getClass())) {
          ns.remove(¢);
          continue removing;
        }
      break;
    }
  }

  @SuppressWarnings("unchecked") private static <N extends ASTNode> Tipper<N> firstTipper(final N n, final List<Tipper<?>> ts) {
    for (final Tipper<?> ¢ : ts)
      if (((Tipper<N>) ¢).canTip(n))
        return (Tipper<N>) ¢;
    return null;
  }

  /** Implementation */
  @SuppressWarnings("unchecked") private final List<Tipper<? extends ASTNode>>[] implementation = (List<Tipper<? extends ASTNode>>[]) new List<?>[2
      * ASTNode.TYPE_METHOD_REFERENCE];

  public Toolbox() {
    // Nothing to do
  }

  /** Associate a bunch of{@link Tipper} with a given sub-class of
   * {@link ASTNode}.
   * @param n JD
   * @param ns JD
   * @return <code><b>this</b></code>, for easy chaining. */
  @SafeVarargs public final <N extends ASTNode> Toolbox add(final Class<N> n, final Tipper<N>... ns) {
    final Integer nodeType = classToNodeType.get(n);
    assert nodeType != null : fault.dump() + //
        "\n c = " + n + //
        "\n c.getSimpleName() = " + n.getSimpleName() + //
        "\n classForNodeType.keySet() = " + classToNodeType.keySet() + //
        "\n classForNodeType = " + classToNodeType + //
        fault.done();
    final List<Tipper<? extends ASTNode>> ts = get(nodeType.intValue());
    for (final Tipper<N> ¢ : ns) {
      if (¢ == null)
        break;
      assert ¢.tipperGroup() != null : "Did you forget to use a specific kind for " + ¢.getClass().getSimpleName();
      if (¢.tipperGroup().isEnabled())
        ts.add(¢);
    }
    return this;
  }

  public void disable(final Class<? extends TipperCategory> c) {
    for (final List<Tipper<? extends ASTNode>> ¢ : implementation)
      if (¢ != null)
        disable(c, ¢);
  }

  /** Find the first {@link Tipper} appropriate for an {@link ASTNode}
   * @param pattern JD
   * @return first {@link Tipper} for which the parameter is within scope, or
   *         <code><b>null</b></code> if no such {@link Tipper} is found. @ */
  public <N extends ASTNode> Tipper<N> firstTipper(final N ¢) {
    return firstTipper(¢, get(¢));
  }

  public List<Tipper<? extends ASTNode>> get(final int ¢) {
    if (implementation[¢] == null)
      implementation[¢] = new ArrayList<>();
    return implementation[¢];
  }

  public int hooksCount() {
    int $ = 0;
    for (final List<Tipper<? extends ASTNode>> ¢ : implementation)
      $ += as.bit(¢ != null && !¢.isEmpty());
    return $;
  }

  public int tippersCount() {
    int $ = 0;
    for (final List<?> ¢ : implementation)
      if (¢ != null)
        $ += ¢.size();
    return $;
  }

  <N extends ASTNode> List<Tipper<? extends ASTNode>> get(final N ¢) {
    return get(¢.getNodeType());
  }
}