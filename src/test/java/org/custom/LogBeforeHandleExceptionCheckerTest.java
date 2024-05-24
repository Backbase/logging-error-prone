package org.custom;

import com.google.errorprone.CompilationTestHelper;
import org.junit.Before;
import org.junit.Test;

public class LogBeforeHandleExceptionCheckerTest {

    private CompilationTestHelper compilationTestHelper;

    @Before
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
                        "import org.springframework.web.client.HttpClientErrorException;",
                        "public class Test {",
                        "    private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "    public void testMethod() {",
                        "        try {",
                        "            // some code",
                        "        } catch (HttpClientErrorException ex) {",
                        "            handleException(ex);",  // Incorrect
                        "            log.error(\"Error reading file\", ex);",
                        "        }",
                        "    }",
                        "    private void handleException(HttpClientErrorException e) {}",
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
                        "import org.springframework.web.client.HttpClientErrorException;",
                        "public class Test {",
                        "    private static final Logger log = LoggerFactory.getLogger(Test.class);",
                        "    public void testMethod() {",
                        "        try {",
                        "            // some code",
                        "        } catch (HttpClientErrorException ex) {",
                        "            log.error(\"Error reading file\", ex);", // Correct
                        "            handleException(ex);",
                        "        }",
                        "    }",
                        "    private void handleException(HttpClientErrorException e) {}",
                        "}")
                .doTest();
    }
}
