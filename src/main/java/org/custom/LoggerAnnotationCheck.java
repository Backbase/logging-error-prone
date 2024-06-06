package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.*;
import com.sun.source.util.TreePath;

/**
 * A BugChecker that ensures Loggers are created using the @Slf4j annotation.
 * This checker identifies loggers created using LoggerFactory.getLogger(Class<?>)
 * and suggests using the @Slf4j annotation instead.
 */
@AutoService(BugChecker.class)
@BugPattern(
        name = "LoggerAnnotationCheck",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#loggers-created-using-enclosing-class",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "Do not create Loggers using getLogger(Class<?>), Logger should be created using @Slf4j annotation."
)
@SuppressWarnings("LoggerAnnotationCheck")
public final class LoggerAnnotationCheck extends BugChecker implements BugChecker.VariableTreeMatcher {

    private static final Matcher<VariableTree> matcher = Matchers.allOf(
            Matchers.isField(),
            Matchers.isSubtypeOf("org.slf4j.Logger"),
            Matchers.variableInitializer(MethodMatchers.staticMethod()
                    .onClass("org.slf4j.LoggerFactory")
                    .named("getLogger")
            )
    );

    /**
     * Matches variable declarations to ensure loggers are created using @Slf4j annotation.
     *
     * @param tree  The variable tree to match against.
     * @param state The current visitor state.
     * @return A description of the match, including suggested fixes.
     */
    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        if (!matcher.matches(tree, state)) {
            return Description.NO_MATCH;
        }

        MethodInvocationTree getLoggerInvocation = (MethodInvocationTree) tree.getInitializer();

        // Get the enclosing class symbol
        TreePath path = state.getPath();
        while (path != null && !(path.getLeaf() instanceof ClassTree)) {
            path = path.getParentPath();
        }

        if (path == null) {
            return Description.NO_MATCH;
        }

        ClassTree enclosingClass = (ClassTree) path.getLeaf();
        SuggestedFix.Builder fix = SuggestedFix.builder();
        String message = "Logger should be created using @Slf4j annotation.";

        // Add @Slf4j annotation to the class declaration
        fix.prefixWith(enclosingClass, "@Slf4j\n");
        return buildDescription(tree)
                .setMessage(message)
                .addFix(fix.build())
                .build();
    }
}
