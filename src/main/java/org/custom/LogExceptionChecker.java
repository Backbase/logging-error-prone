package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.VisitorState;
import com.google.errorprone.fixes.Fix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.fixes.SuggestedFix;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;

@AutoService(BugChecker.class)
@BugPattern(
        name = "LogExceptionInCatch",
        summary = "Log statement in the catch block does not log the caught exception.",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#log-statement-in-the-catch-block-does-not-log-the-caught-exception",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = ERROR
)
public final class LogExceptionChecker extends BugChecker implements BugChecker.CatchTreeMatcher {

    @Override
    public Description matchCatch(CatchTree catchTree, VisitorState state) {
        Name exceptionName = state.getNames().fromString(catchTree.getParameter().getName().toString());
        for (StatementTree statementTree : catchTree.getBlock().getStatements()) {
            if (statementTree.getKind() == Tree.Kind.EXPRESSION_STATEMENT) {
                ExpressionTree expressionTree = ((ExpressionStatementTree) statementTree).getExpression();
                if (expressionTree instanceof MethodInvocationTree) {
                    MethodInvocationTree methodInvocationTree = (MethodInvocationTree) expressionTree;
                    if (methodInvocationTree.getMethodSelect().toString().endsWith(LOG_METHOD_NAME)
                            && methodInvocationTree.getArguments().size() < 2) {
                        String errorMessage = String.format("Error reading %s", exceptionName);
                        Fix fix = SuggestedFix.builder()
                                .prefixWith(methodInvocationTree.getArguments().get(0), "\"")
                                .postfixWith(methodInvocationTree.getArguments().get(0), "\", " + exceptionName)
                                .replace(methodInvocationTree.getMethodSelect(), String.format("log.error(\"%s\", %s)", errorMessage, exceptionName))
                                .build();
                        return describeMatch(statementTree, fix);
                    }
                }
            }
        }
        return Description.NO_MATCH;
    }

    private static final String LOG_METHOD_NAME = ".error";
}