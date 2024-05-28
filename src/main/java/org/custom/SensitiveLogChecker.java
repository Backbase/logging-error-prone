package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;

import java.util.regex.Pattern;

@AutoService(BugChecker.class)
@BugPattern(
        name = "SensitiveLogChecker",
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#dont-log-sensitive-data",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "You use debug or info log method in catch block.")
public final class SensitiveLogChecker extends BugChecker implements BugChecker.MethodInvocationTreeMatcher {

    private static final Pattern SENSITIVE_PATTERN = Pattern.compile("(?i)password|apikey|token|secret");

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState context) {
        TreePath path = context.getPath();
        SensitiveArgumentScanner scanner = new SensitiveArgumentScanner();
        scanner.scan(path, null); // Scan the tree for sensitive arguments
        if (scanner.isSensitiveFound()) {
            // If sensitive information is found in the log statement, report an error
            return buildDescription(tree)
                    .setMessage("Sensitive information found in log statement")
                    .build();
        }
        return Description.NO_MATCH;
    }

    private static class SensitiveArgumentScanner extends TreePathScanner<Void, Void> {
        private boolean sensitiveFound = false;

        boolean isSensitiveFound() {
            return sensitiveFound;
        }

        @Override
        public Void visitMethodInvocation(MethodInvocationTree tree, Void aVoid) {
            // Check if the method invocation is a log statement
            if (isLogMethod(tree)) {
                // Check each argument for sensitive information
                for (ExpressionTree arg : tree.getArguments()) {
                    if (arg != null && arg.toString().trim().length() > 0 && containsSensitiveInfo(arg)) {
                        sensitiveFound = true;
                        return null;
                    }
                }
            }
            return super.visitMethodInvocation(tree, aVoid);
        }

        private boolean isLogMethod(MethodInvocationTree tree) {
            String methodName = tree.getMethodSelect().toString();
            // Add additional log method checks here if needed
            return methodName.endsWith(".info") || methodName.endsWith(".debug") || methodName.endsWith(".error");
        }

        private boolean containsSensitiveInfo(ExpressionTree arg) {
            return SENSITIVE_PATTERN.matcher(arg.toString()).find();
        }
    }
}