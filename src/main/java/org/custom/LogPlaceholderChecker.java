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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A BugChecker that checks whether the number of placeholders in log messages
 * matches the number of parameters provided.
 */
@AutoService(BugChecker.class)
@BugPattern(
        name = "LogPlaceholderChecker",
        summary = "Check that placeholders in log messages ({}) match the number of parameters provided.",
        severity = BugPattern.SeverityLevel.ERROR,
        link = "https://github.com/Backbase/logging-error-prone/blob/develop/docs/LoggingRules.md#log-placeholders-count",
        linkType = BugPattern.LinkType.CUSTOM
)
@SuppressWarnings("LogPlaceholderChecker")
public final class LogPlaceholderChecker extends BugChecker implements MethodInvocationTreeMatcher {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{}");

    /**
     * Matches method invocations to ensure log message placeholders match the number of parameters.
     *
     * @param tree  The method invocation tree to match against.
     * @param state The current visitor state.
     * @return A description of the match, including suggested fixes if applicable.
     */
    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        // Check if the method corresponds to a logging method
        if (!isLoggingMethod(tree)) {
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

    /**
     * Checks if the given method invocation corresponds to a logging method.
     *
     * @param tree The method invocation tree to check.
     * @return {@code true} if the method invocation is a logging method, {@code false} otherwise.
     */
    private boolean isLoggingMethod(MethodInvocationTree tree) {
        // Get the method name as a string
        String methodName = tree.getMethodSelect().toString();

        // Check if the method name corresponds to a logging method
        return methodName.endsWith(".info") || methodName.endsWith(".warn") ||
                methodName.endsWith(".error") || methodName.endsWith(".debug") ||
                methodName.endsWith(".trace");
    }

    /**
     * Extracts the format string from the first argument of a method invocation.
     *
     * @param firstArgument The first argument of the method invocation.
     * @return The format string if the first argument is a string literal, null otherwise.
     */
    private String getFormatString(ExpressionTree firstArgument) {
        if (firstArgument instanceof com.sun.source.tree.LiteralTree) {
            Object value = ((com.sun.source.tree.LiteralTree) firstArgument).getValue();
            if (value instanceof String) {
                return (String) value;
            }
        }
        return null;
    }

    /**
     * Counts the number of placeholders in the given format string.
     *
     * @param formatString The format string to count placeholders in.
     * @return The number of placeholders.
     */
    private int countPlaceholders(String formatString) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(formatString);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Checks if the given argument is a subtype of Throwable.
     *
     * @param argument The argument to check.
     * @param state    The current visitor state.
     * @return True if the argument is a subtype of Throwable, false otherwise.
     */
    private boolean isThrowable(ExpressionTree argument, VisitorState state) {
        return ASTHelpers.isSubtype(ASTHelpers.getType(argument), state.getTypeFromString("java.lang.Throwable"), state);
    }
}
