package org.custom;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Matchers;
import com.google.errorprone.matchers.method.MethodMatchers;
import com.sun.source.tree.*;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.source.util.TreeScanner;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@AutoService(BugChecker.class)
@BugPattern(
        name = "CatchBlockLogException",
        link = "https://backbase.atlassian.net/wiki/spaces/GUIL/pages/922386858/Logging#",
        linkType = BugPattern.LinkType.CUSTOM,
        severity = BugPattern.SeverityLevel.ERROR,
        summary = "You use debug or info log method in catch block.")
public final class CatchBlockLogException extends BugChecker implements BugChecker.CatchTreeMatcher {

    private static final long serialVersionUID = 1L;

    private static final Matcher<ExpressionTree> logMethod = MethodMatchers.instanceMethod()
            .onDescendantOfAny("org.slf4j.Logger", "com.palantir.logsafe.logger.SafeLogger")
            .withNameMatching(Pattern.compile("debug|info"));

    private static final Matcher<Tree> containslogMethod =
            Matchers.contains(Matchers.toType(ExpressionTree.class, logMethod));

    @Override
    public Description matchCatch(CatchTree tree, VisitorState state) {
        if (containslogMethod.matches(tree, state)) {
            return buildDescription(tree)
                    .addFix(attemptFix(tree, state))
                    .setMessage("Catch block contains log method debug or info. You can't use this methods in catch block.")
                    .build();
        }
        return Description.NO_MATCH;
    }

    private static SuggestedFix attemptFix(CatchTree tree, VisitorState state) {
        List<MethodInvocationTree> matchingLoggingStatements =
                tree.getBlock().accept(LogStatementScanner.INSTANCE, state);
        if (matchingLoggingStatements == null || matchingLoggingStatements.size() != 1) {
            return SuggestedFix.emptyFix();
        }
        MethodInvocationTree loggingInvocation = matchingLoggingStatements.get(0);
        List<? extends ExpressionTree> loggingArguments = loggingInvocation.getArguments();
        // There are no valid log invocations without at least a single argument.
        ExpressionTree lastArgument = loggingArguments.get(loggingArguments.size() - 1);
        return SuggestedFix.builder()
                .replace(
                        lastArgument,
                        lastArgument
                                .accept(ThrowableFromArgVisitor.INSTANCE, state)
                                .orElseGet(() -> state.getSourceForNode(lastArgument)
                                        + ", "
                                        + tree.getParameter().getName()) + ", test string" + ", " + loggingInvocation.getMethodSelect())
                .build();
//        return SuggestedFix.builder()
//              .replace(
//                      loggingInvocation.getMethodSelect(),
//                      "log.error").build();
    }

    private static final class ThrowableFromArgVisitor extends SimpleTreeVisitor<Optional<String>, VisitorState> {
        private static final ThrowableFromArgVisitor INSTANCE = new ThrowableFromArgVisitor();

        private static final Matcher<ExpressionTree> throwableMessageInvocation = Matchers.instanceMethod()
                .onDescendantOf(Throwable.class.getName())
                .named("getMessage");

        ThrowableFromArgVisitor() {
            super(Optional.empty());
        }

        @Override
        public Optional<String> visitMethodInvocation(MethodInvocationTree node, VisitorState state) {
            if (throwableMessageInvocation.matches(node, state)) {
                return node.getMethodSelect().accept(ThrowableFromInvocationVisitor.INSTANCE, state);
            }
            return Optional.empty();
        }
    }

    private static final class ThrowableFromInvocationVisitor
            extends SimpleTreeVisitor<Optional<String>, VisitorState> {
        private static final ThrowableFromInvocationVisitor INSTANCE = new ThrowableFromInvocationVisitor();

        ThrowableFromInvocationVisitor() {
            super(Optional.empty());
        }

        @Override
        public Optional<String> visitMemberSelect(MemberSelectTree node, VisitorState state) {
            if (node.getIdentifier().contentEquals("getMessage")) {
                return Optional.ofNullable(state.getSourceForNode(node.getExpression()));
            }
            return Optional.empty();
        }
    }

    private static final class LogStatementScanner extends TreeScanner<List<MethodInvocationTree>, VisitorState> {
        private static final LogStatementScanner INSTANCE = new LogStatementScanner();

        @Override
        public List<MethodInvocationTree> visitMethodInvocation(MethodInvocationTree node, VisitorState state) {
            if (logMethod.matches(node, state)) {
                return ImmutableList.of(node);
            }
            return super.visitMethodInvocation(node, state);
        }

        @Override
        public List<MethodInvocationTree> visitCatch(CatchTree node, VisitorState state) {
            // Do not flag logging from a nested catch, it's handled separately
            return ImmutableList.of();
        }

        @Override
        public List<MethodInvocationTree> reduce(
                @Nullable List<MethodInvocationTree> left, @Nullable List<MethodInvocationTree> right) {
            // Unfortunately there's no way to provide default initial values, so we must handle nulls.
            if (left == null) {
                return right;
            }
            if (right == null) {
                return left;
            }
            return ImmutableList.<MethodInvocationTree>builder()
                    .addAll(left)
                    .addAll(right)
                    .build();
        }
    }
}