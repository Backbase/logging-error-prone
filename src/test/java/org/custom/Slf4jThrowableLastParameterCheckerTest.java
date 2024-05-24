package org.custom;


import com.google.errorprone.BugCheckerRefactoringTestHelper;
import com.google.errorprone.CompilationTestHelper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
//import org.testng.annotations.Test;

public class Slf4jThrowableLastParameterCheckerTest {

//    private final BugCheckerRefactoringTestHelper refactoringHelper =
//            BugCheckerRefactoringTestHelper.newInstance(new Slf4jThrowableLastParameterChecker(), getClass());
//
//    @Test
//    public void testFix_simple() {
//        refactoringHelper
//                .addInputLines(
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
//    public void testFix_exceptionNotLastArg() {
//        refactoringHelper
//                .addInputLines(
//                        "Test.java",
//                        "import org.slf4j.Logger;",
//                        "import org.slf4j.LoggerFactory;",
//                        "class Test {",
//                        "  private static final Logger log = LoggerFactory.getLogger(Test.class);",
//                        "  void f(String param) {",
//                        "    try {",
//                        "        log.info(\"hello\");",
//                        "    } catch (Throwable t) {",
//                        "        log.error(\"foo\", t, \"additional info\");",
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
//                        "        log.error(\"foo\", t, \"additional info\");",
//                        "    }",
//                        "  }",
//                        "}")
//                .doTest();
//    }
}