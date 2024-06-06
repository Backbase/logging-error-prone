package org.custom;


import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.StatementTree;

import java.util.List;

@AutoService(BugChecker.class)
@BugPattern(
        name = "LogBeforeHandleException",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#logging-error-them-before-handling-them",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "When catching exceptions, consider logging them before handling them."
)
@SuppressWarnings("LogBeforeHandleException")
public final class LogBeforeHandleExceptionChecker extends BugChecker implements BugChecker.CatchTreeMatcher {

    @Override
    public Description matchCatch(CatchTree catchTree, VisitorState state) {
        BlockTree block = catchTree.getBlock();
        if (block != null) {
            boolean loggingBeforeHandling = loggingBeforeHandling((List<StatementTree>) block.getStatements());
            if (!loggingBeforeHandling) {
                String message = "Logging should precede exception handling";
                return buildDescription(catchTree)
                        .setMessage(message)
                        .build();
            }
        }
        return Description.NO_MATCH;
    }

    private boolean loggingBeforeHandling(List<StatementTree> statements) {
        boolean logFound = false;
        boolean handleFound = false;

        for (StatementTree statement : statements) {
            String statementStr = statement.toString();

            if (statementStr.contains("log.error") || statementStr.contains("log.warn") || statementStr.contains("log.info")) {
                logFound = true;
            }
            if (statementStr.contains("handleException")) {
                handleFound = true;
            }
            if (handleFound && !logFound) {
                return false;
            }
        }
        return true;
    }
}