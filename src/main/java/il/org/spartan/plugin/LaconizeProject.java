package il.org.spartan.plugin;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import org.eclipse.core.commands.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.ui.*;
import org.eclipse.ui.progress.*;

import il.org.spartan.spartanizer.dispatch.*;

/** A handler for {@link Tips}. This handler executes all safe Tips on all Java
 * files in the current project.
 * @author Ofir Elmakias <code><elmakias [at] outlook.com></code>
 * @since 2015/08/01 */
public final class LaconizeProject extends BaseHandler {
  static final int MAX_PASSES = 20;
  private final StringBuilder status = new StringBuilder();
  private ICompilationUnit currentCompilationUnit;
  IJavaProject javaProject;
  final List<ICompilationUnit> todo = new ArrayList<>();
  private int initialCount;
  private final List<ICompilationUnit> dead = new ArrayList<>();

  /** Returns the number of spartanization tips for a compilation unit
   * @param u JD
   * @return number of tips available for the compilation unit */
  public int countTips() {
    if (todo.isEmpty())
      return 0;
    final AtomicInteger $ = new AtomicInteger(0);
    final GUI$Applicator ¢ = new Trimmer();
    try {
      PlatformUI.getWorkbench().getProgressService().run(true, true, pm -> {
        pm.beginTask("Looking for tips in " + javaProject, IProgressMonitor.UNKNOWN);
        ¢.setMarker(null);
        ¢.setICompilationUnit(todo.get(0));
        $.addAndGet(¢.countTips());
        if (pm.isCanceled())
          $.set(0);
        pm.done();
      });
    } catch (InvocationTargetException | InterruptedException e) {
      e.printStackTrace();
    }
    return $.get();
  }

  @Override public Void execute(@SuppressWarnings("unused") final ExecutionEvent __) {
    status.setLength(0);
    todo.clear();
    dead.clear();
    initialCount = 0;
    return go();
  }

  public Void go() {
    start();
    if (initialCount == 0)
      return eclipse.announce(status + "No tips found.");
    eclipse.announce(status);
    final GUI$Applicator a = new Trimmer();
    int i;
    final IWorkbench wb = PlatformUI.getWorkbench();
    for (i = 0; i < MAX_PASSES; ++i) {
      final IProgressService ps = wb.getProgressService();
      final AtomicInteger passNum = new AtomicInteger(i + 1);
      final AtomicBoolean cancelled = new AtomicBoolean(false);
      try {
        ps.run(true, true, pm -> {
          // a.setProgressMonitor(pm);
          pm.beginTask(
              "Spartanizing project '" + javaProject.getElementName() + "' - " + "Pass " + passNum.get() + " out of maximum of " + MAX_PASSES,
              todo.size());
          int n = 0;
          for (final ICompilationUnit ¢ : todo) {
            if (pm.isCanceled()) {
              cancelled.set(true);
              break;
            }
            pm.worked(1);
            pm.subTask("Compilation unit #" + ++n + "/" + todo.size() + " (" + ¢.getElementName() + ")");
            if (!a.apply(¢))
              dead.add(¢);
          }
          if (!dead.isEmpty())
            status.append(dead.size() + " CUs did not change; will not be processed further\n");
          todo.removeAll(dead);
          dead.clear();
          pm.done();
        });
      } catch (final InvocationTargetException x) {
        monitor.logEvaluationError(this, x);
      } catch (final InterruptedException x) {
        monitor.logEvaluationError(this, x);
      }
      if (cancelled.get() || todo.isEmpty())
        break;
    }
    todo.clear();
    todo.addAll(eclipse.facade.compilationUnits(currentCompilationUnit));
    final int finalCount = countTips();
    return eclipse.announce(//
        status + "Laconizing '" + javaProject.getElementName() + "' project \n" + //
            "Completed in " + (1 + i) + " passes. \n" + //
            (i < MAX_PASSES ? "" : "   === too many passes\n") + //
            "Tips followed: " + (initialCount - finalCount) + "\n" + //
            "Tips before: " + initialCount + "\n" + //
            "Tips after: " + finalCount + "\n" //
    );
  }

  public void start() {
    currentCompilationUnit = eclipse.currentCompilationUnit();
    status.append("Starting at compilation unit: " + currentCompilationUnit.getElementName() + "\n");
    javaProject = currentCompilationUnit.getJavaProject();
    status.append("Java project is: " + javaProject.getElementName() + "\n");
    todo.clear();
    todo.addAll(eclipse.facade.compilationUnits(currentCompilationUnit));
    status.append("Found " + todo.size() + " compilation units, ");
    initialCount = countTips();
    status.append("with " + initialCount + " tips.\n");
  }
}