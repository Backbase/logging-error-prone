package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.tools.javac.code.Symbol;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoService(BugChecker.class)
@BugPattern(
        name = "LogPlaceholderChecker",
        summary = "Check that placeholders in log messages ({}) match the number of parameters provided.",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.NONE
)
public final class LogPlaceholderChecker extends BugChecker implements MethodInvocationTreeMatcher {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{}");

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        Symbol.MethodSymbol methodSymbol = ASTHelpers.getSymbol(tree);
        if (methodSymbol == null || !isLoggingMethod(methodSymbol)) {
            return Description.NO_MATCH;
        }

        // Ensure there is at least a format string
        if (tree.getArguments().isEmpty()) {
            return Description.NO_MATCH;
        }

        // Get the format string and count placeholders
        String formatString = getFormatString(tree.getArguments().get(0));
        if (formatString == null) {
            return Description.NO_MATCH;
        }
        int placeholderCount = countPlaceholders(formatString);

        // Count the number of arguments excluding the format string
        int argumentCount = tree.getArguments().size() - 1;

        // Check if the last argument is a Throwable
        boolean lastArgumentIsThrowable = isThrowable(tree.getArguments().get(tree.getArguments().size() - 1), state);

        // Adjust the argument count if the last argument is a Throwable and it's the only argument
        if (lastArgumentIsThrowable && argumentCount == 0) {
            argumentCount++;
        }

        // Check if the number of placeholders matches the number of arguments
        if ((placeholderCount != 0) && (placeholderCount != argumentCount)) {
            return buildDescription(tree)
                    .setMessage("Mismatch between the number of placeholders (" + placeholderCount +
                            ") and the number of arguments (" + argumentCount + ").")
                    .build();
        }

        return Description.NO_MATCH;
    }

    private boolean isLoggingMethod(Symbol.MethodSymbol methodSymbol) {
        String methodName = methodSymbol.getSimpleName().toString();
        return methodName.equals("info") || methodName.equals("warn") ||
                methodName.equals("error") || methodName.equals("debug") ||
                methodName.equals("trace");
    }

    private String getFormatString(ExpressionTree firstArgument) {
        if (firstArgument instanceof com.sun.source.tree.LiteralTree) {
            Object value = ((com.sun.source.tree.LiteralTree) firstArgument).getValue();
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    private int countPlaceholders(String formatString) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(formatString);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private boolean isThrowable(ExpressionTree argument, VisitorState state) {
        return ASTHelpers.isSubtype(ASTHelpers.getType(argument), state.getTypeFromString("java.lang.Throwable"), state);
    }
}