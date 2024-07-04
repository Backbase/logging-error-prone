package org.custom;

import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatchBlockLogExceptionTest {

    private CompilationTestHelper compilationHelper;
    private BugCheckerRefactoringTestHelper refactoringHelper;

    @BeforeEach
    public void setUp() {
        compilationHelper = CompilationTestHelper.newInstance(CatchBlockLogException.class, getClass());
        refactoringHelper = BugCheckerRefactoringTestHelper.newInstance(new CatchBlockLogException(), getClass());
    }

    @Test
    void testNoLoggingInCatchBlock() {
        compilationHelper
                .addSourceLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "      log.debug(\"Caught exception\");",
                        "    } catch (Exception e) {",
                        "      // No logging",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testLoggingWithFix() {
        refactoringHelper
                .addInputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.info(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.error(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testMultipleLoggingStatementsInCatchBlock() {
        refactoringHelper
                .addInputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "      // Code that may throw an exception",
                        "    } catch (Exception e) {",
                        "      log.info(\"Info message\", e);",
                        "      log.debug(\"Debug message\", e);",
                        "    }",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "      // Code that may throw an exception",
                        "    } catch (Exception e) {",
                        "      log.error(\"Info message\", e);",
                        "      log.error(\"Debug message\", e);",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testErrorLoggingInCatchBlock() {
        compilationHelper
                .addSourceLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.error(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testWarnLoggingInCatchBlock() {
        compilationHelper
                .addSourceLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.warn(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }

    @Test
    void testDebugLoggingInCatchBlock() {
        refactoringHelper
                .addInputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.debug(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .addOutputLines(
                        "Test.java",
                        "import org.slf4j.Logger;",
                        "import org.slf4j.LoggerFactory;",
                        "class Test {",
                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "  void testMethod() {",
                        "    try {",
                        "    } catch (Exception e) {",
                        "      log.error(\"Caught exception\", e);",
                        "    }",
                        "  }",
                        "}")
                .doTest();
    }
}
