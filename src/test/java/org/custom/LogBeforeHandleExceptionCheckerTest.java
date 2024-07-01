package org.custom;

import autovalue.shaded.com.google.common.base.Predicates;
import com.google.errorprone.CompilationTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class LogBeforeHandleExceptionCheckerTest {

    private CompilationTestHelper compilationTestHelper;

    @BeforeEach
    public void setUp() {
        compilationTestHelper = CompilationTestHelper.newInstance(LogBeforeHandleExceptionChecker.class, getClass());
    }

    @Test
    public void testLogBeforeHandleException() {
        compilationTestHelper
            .addSourceLines(
                "Test.java",
                "import java.io.IOException;",
                "import org.slf4j.Logger;",
                "import org.slf4j.LoggerFactory;",
                "import java.lang.Exception;",
                "public class Test {",
                "    private static final Logger log = LoggerFactory.getLogger(Test.class);",
                "    public void testMethod() {",
                "        try {",
                "            // some code",
                "        } catch (Exception ex) {",
                "            handleException(ex);",  // Incorrect
                "            log.error(\"Error reading file\", ex);",
                "        }",
                "    }",
                "    private void handleException(Exception e) {}",
                "}")
            .doTest();

    }

    @Test
    public void testCorrectLogBeforeHandleException() {
        compilationTestHelper
            .addSourceLines(
                "Test.java",
                "import java.io.IOException;",
                "import org.slf4j.Logger;",
                "import org.slf4j.LoggerFactory;",
                "import java.lang.Exception;",
                "public class Test {",
                "    private static final Logger log = LoggerFactory.getLogger(Test.class);",
                "    public void testMethod() {",
                "        try {",
                "            // some code",
                "        } catch (Exception ex) {",
                "            log.error(\"Error reading file\", ex);", // Correct
                "            handleException(ex);",
                "        }",
                "    }",
                "    private void handleException(Exception e) {}",
                "}")
            .doTest();
    }
}
