package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;

/**
 * A BugChecker that ensures logging is not performed within loops.
 * This checker matches for loops that contain logging statements to avoid unnecessary logging overhead.
 */
@AutoService(BugChecker.class)
@BugPattern(
        name = "AvoidLoggingWithinLoops",
        summary = "Logging within loops should be avoided to prevent unnecessary logging overhead.",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#dont-log-inside-a-loop",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = ERROR
)
@SuppressWarnings("AvoidLoggingWithinLoops")
public final class AvoidLoggingWithinLoops extends BugChecker implements BugChecker.ForLoopTreeMatcher {

    /**
     * A TreeScanner to extract the method name from a method invocation tree.
     */
    private static final com.sun.source.util.TreeScanner<Name, Void> GET_METHOD_NAME =
            new com.sun.source.util.TreeScanner<Name, Void>() {
                @Override
                public Name visitMethodInvocation(com.sun.source.tree.MethodInvocationTree node, Void p) {
                    return node.getMethodSelect().accept(this, null);
                }

                @Override
                public Name reduce(Name r1, Name r2) {
                    return (r1 != null) ? r1 : r2;
                }

                @Override
                public Name visitMemberSelect(com.sun.source.tree.MemberSelectTree node, Void p) {
                    return node.getIdentifier();
                }
            };

    /**
     * Matches for loops that contain logging statements to avoid logging overhead within loops.
     *
     * @param forLoopTree The for loop tree to match against.
     * @param state       The current visitor state.
     * @return A description of the match, including suggested fixes.
     */
    @Override
    public Description matchForLoop(ForLoopTree forLoopTree, VisitorState state) {
        StatementTree statement = forLoopTree.getStatement();
        if (statement instanceof BlockTree) {
            BlockTree body = (BlockTree) statement;
            if (body.getStatements().size() == 1) {
                StatementTree loopBodyStatement = body.getStatements().get(0);
                if (loopBodyStatement instanceof ExpressionStatementTree) {
                    ExpressionStatementTree expressionStatement = (ExpressionStatementTree) loopBodyStatement;
                    Name methodName = getMethodName(expressionStatement.getExpression());
                    if (methodName != null && methodName.contentEquals("debug")) {
                        return buildDescription(expressionStatement)
                                .setMessage("Logging within loops should be avoided to prevent unnecessary logging overhead.")
                                .build();
                    }
                }
            }
        }
        return Description.NO_MATCH;
    }

    /**
     * Extracts the method name from the given tree expression.
     *
     * @param expression The tree expression to extract the method name from.
     * @return The method name, or null if the expression is not a method invocation.
     */
    private Name getMethodName(Tree expression) {
        if (expression.getKind() == Tree.Kind.METHOD_INVOCATION) {
            com.sun.source.tree.MethodInvocationTree methodInvocationTree = (com.sun.source.tree.MethodInvocationTree) expression;
            return methodInvocationTree.getMethodSelect().accept(GET_METHOD_NAME, null);
        }
        return null;
    }
}
