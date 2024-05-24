package org.custom;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import java.util.regex.Pattern;

@AutoService(BugChecker.class)
@BugPattern(
        name = "Slf4jThrowableLastParameterChecker",
        link = "http://www.slf4j.org/faq.html#paramException",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "Slf4j loggers require throwables to be the last parameter otherwise a stack trace is not produced."
)
public final class Slf4jThrowableLastParameterChecker extends BugChecker implements MethodInvocationTreeMatcher {

    private static final long serialVersionUID = 1L;

    private static final Matcher<ExpressionTree> LOG_METHOD = MethodMatchers.instanceMethod()
            .onDescendantOf("org.slf4j.Logger")
            .withNameMatching(Pattern.compile("trace|debug|info|warn|error"));

    private static final Matcher<ExpressionTree> THROWABLE = MoreMatchers.isSubtypeOf(Throwable.class);

    @Override
    public Description matchMethodInvocation(MethodInvocationTree tree, VisitorState state) {
        if (!LOG_METHOD.matches(tree, state)) {
            return Description.NO_MATCH;
        }

        List<? extends ExpressionTree> args = tree.getArguments();
        for (int i = 0; i < args.size() - 1; i++) {
            if (THROWABLE.matches(args.get(i), state)) {
                return buildDescription(tree)
                        .setMessage("Slf4j loggers require throwables to be the last parameter otherwise a stack trace is not produced.")
                        .build();
            }
        }

        return Description.NO_MATCH;
    }
}