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
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.*;
import com.sun.tools.javac.code.Symbol;

@AutoService(BugChecker.class)
@BugPattern(
        name = "LoggerAnnotationCheck",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#loggers-created-using-enclosing-class",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "Do not create Loggers using getLogger(Class<?>), Logger should be created using @Slf4j annotation.")
public final class LoggerAnnotationCheck extends BugChecker implements BugChecker.VariableTreeMatcher {

    private static final Matcher<VariableTree> matcher = Matchers.allOf(
            Matchers.isField(),
            Matchers.isSubtypeOf("org.slf4j.Logger"),
            Matchers.variableInitializer(MethodMatchers.staticMethod()
                    .onClass("org.slf4j.LoggerFactory")
                    .named("getLogger")
                    )
    );

    @Override
    public Description matchVariable(VariableTree tree, VisitorState state) {
        if (!matcher.matches(tree, state)) {
            return Description.NO_MATCH;
        }

        MethodInvocationTree getLoggerInvocation = (MethodInvocationTree) tree.getInitializer();
        Symbol.ClassSymbol enclosingClassSymbol = ASTHelpers.enclosingClass(ASTHelpers.getSymbol(tree));

        SuggestedFix.Builder fix = SuggestedFix.builder();
        String message = "Logger should be created using @Slf4j annotation.";

        // Get the class declaration tree
        ClassTree classTree = ASTHelpers.findEnclosingNode(state.getPath(), ClassTree.class);
        if (classTree != null) {
            Tree.Kind kind = classTree.getKind();
            // Ensure it's a class declaration
            if (kind == Tree.Kind.CLASS || kind == Tree.Kind.ENUM || kind == Tree.Kind.INTERFACE) {
                // Add @Slf4j annotation to the class declaration
                fix.prefixWith(classTree, "@Slf4j\n");
                return buildDescription(tree)
                        .setMessage(message)
                        .addFix(fix.build())
                        .build();
            }
        }

        return Description.NO_MATCH;
    }
}