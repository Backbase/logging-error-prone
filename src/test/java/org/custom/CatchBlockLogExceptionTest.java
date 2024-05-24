package org.custom;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchBlockLogExceptionTest {

//    private static final String errorMsg =
//            "BUG: Diagnostic contains: " + "Catch block contains log statements but thrown exception is never logged";
//
//    private CompilationTestHelper compilationHelper;
//
//    @BeforeEach
//    public void before() {
//        compilationHelper = CompilationTestHelper.newInstance(CatchBlockLogException.class, getClass());
//    }
//
//    @Test
//    public void testLogIllegalArgumentException() {
//        test(IllegalArgumentException.class, "log.info(\"hello\", e);", Optional.empty());
//    }
//
//    @Test
//    public void testLogRuntimeException() {
//        test(RuntimeException.class, "log.info(\"hello\", e);", Optional.empty());
//    }
//
//    @Test
//    public void testLogException() {
//        test(Exception.class, "log.info(\"hello\", e);", Optional.empty());
//    }
//
//    @Test
//    public void testLogThrowable() {
//        test(Throwable.class, "log.info(\"hello\", e);", Optional.empty());
//    }
//
//    @Test
//    public void testLogExceptionNotLastArg() {
//        test(RuntimeException.class, "log.info(\"hello\", e, \"world\");", Optional.of(errorMsg));
//    }
//
//    @Test
//    public void testNoLogException() {
//        test(RuntimeException.class, "log.info(\"hello\");", Optional.of(errorMsg));
//    }
//
//    @Test
//    public void testNoLogStatement() {
//        test(RuntimeException.class, "// Do nothing", Optional.empty());
//    }
//
//    @Test
//    public void testFix_simple_slf4j() {
//        fix().addInputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\");",
//                        "    }",
//                        "  }",
//                        "}")
//                .addOutputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\", t);",
//                        "    }",
//                        "  }",
//                        "}")
//                .doTest();
//    }
//
//    @Test
//    public void testFix_simple_safelog() {
//        fix().addInputLines(
//                        "Test.java",
//                        "import com.palantir.logsafe.logger.SafeLogger;",
//                        "import com.palantir.logsafe.logger.SafeLoggerFactory;",
//                        "class Test {",
//                        "  private static final SafeLogger log = SafeLoggerFactory.get(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\");",
//                        "    }",
//                        "  }",
//                        "}")
//                .addOutputLines(
//                        "Test.java",
//                        "import com.palantir.logsafe.logger.SafeLogger;",
//                        "import com.palantir.logsafe.logger.SafeLoggerFactory;",
//                        "class Test {",
//                        "  private static final SafeLogger log = SafeLoggerFactory.get(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\", t);",
//                        "    }",
//                        "  }",
//                        "}")
//                .doTest();
//    }
//
//    @Test
//    public void testFix_ambiguous() {
//        // In this case there are multiple options, no fixes should be suggested.
//        fix().addInputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\");",
//                        "        log.warn(\"bar\");",
//                        "    }",
//                        "  }",
//                        "}")
//                .expectUnchanged()
//                .doTestExpectingFailure(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
//    }
//
//    @Test
//    public void testFix_getMessage() {
//        // In this case there are multiple options, no fixes should be suggested.
//        fix().addInputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\", t.getMessage());",
//                        "    }",
//                        "  }",
//                        "}")
//                .addOutputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\", t);",
//                        "    }",
//                        "  }",
//                        "}")
//                .doTest(BugCheckerRefactoringTestHelper.TestMode.TEXT_MATCH);
//    }
//
//    private void test(Class<? extends Throwable> exceptionClass, String catchStatement, Optional<String> error) {
//        compilationHelper
//                .addSourceLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "// " + error.orElse(""),
//                        "    } catch (" + exceptionClass.getSimpleName() + " e) {",
//                        "        " + catchStatement,
//                        "    }",
//                        "  }",
//                        "}")
//                .doTest();
//    }
//
//    private RefactoringValidator fix() {
//        return RefactoringValidator.of(CatchBlockLogException.class, getClass());
//    }
}