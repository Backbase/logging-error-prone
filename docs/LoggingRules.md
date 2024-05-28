# Backbase error-prone logging rules

## Loggers created using enclosing class

Loggers created using getLogger(Class<?>) must reference their enclosing class.
```java

Logger log = Logger.getLogger("MyLogger");  // Incorrect

Logger log = Logger.getLogger(MyClass.class.getName()); // Correct

```

## Logger should not use throwable parameter

Logger calls interpolation markers should not be used for the throwable parameter because they prevent stack traces from being logged in favour of the string value of the Throwable.

```java

log.error("Error occurred: {}", throwable); // Incorrect
log.error("Error occurred", throwable); // Correct

```

## Prefer parameterized logging methods

Prefer parameterized logging methods (info(String format, Object... args)) over string concatenation to improve performance and avoid unnecessary string manipulation.

```java

log.info("User " + username + " logged in"); // Incorrect
log.info("User {} logged in", username); // Correct

```



## Allow only compile-time constant for log 

Allow only compile-time constant slf4j log message strings

```java

String message = getMessage();
log.info(message); // Incorrect
String message = "This is a compile-time constant message";
log.info(message); // Correct

```



## Don't log sensitive data

Sensitive data must not be passed to any logger directly or indirectly

```java

log.warn("User login failed with password: " + password); // Incorrect
log.warn("User login failed with password: {}", maskPassword(password)); // Correct

```



## Loggers require throwables to be the last parameter 

Slf4j loggers require throwables to be the last parameter otherwise a stack trace is not produced. [SLF4J FAQ](https://www.slf4j.org/faq.html#paramException)

```java 

log.error("An error occurred", "Additional info", exception); // Incorrect
log.error("An error occurred", exception, "Additional info"); // Correct

```


## Logging error them before handling them

When catching exceptions, consider logging them before handling them.

```java

catch (IOException e) {  // Incorrect
handleException(e);  
log.error("Error reading file", e);
}
catch (IOException e) {  // Correct
log.error("Error reading file", e);
handleException(e);
}

```


## Only the error log level should be logged in the catch block

Only the error log level should be logged in the catch block

```java

catch (Exception e) {  // Incorrect
log.info("An error occurred", e);
}
catch (Exception e) {  // Correct
log.error("An error occurred", e);
}

```



## Log statement in the catch block does not log the caught exception

Log statement in the catch block does not log the caught exception.

```java

catch (IOException e) {  // Incorrect
log.error("File read error");
}
catch (IOException e) {  // Correct
log.error("Error reading file", e);
}

```



## Log placeholders count

Check that placeholders in log messages ({}) match the number and types of parameters provided.

```java

log.info("User {} logged in", username, time); // Incorrect
log.info("User {} logged in", username); // Correct

```


## Avoid logging sensitive data directly

Avoid logging sensitive data directly; prefer using a structured logging approach like SLF4J's MDC for sensitive context data.

```java

// Incorrect, Directly logging sensitive data
log.info("User {} logged in with password: {}", username, password);
// Correct, Put sensitive data into the MDC context
MDC.put("username", username);
// Log the login event without directly logging the password
logger.info("User logged in");
// Clear the MDC context after logging
MDC.remove("username");

```



## Don't log inside a loop

Logging within loops should be avoided to prevent unnecessary logging overhead.

```java

for (int i = 0; i < 10; i++) {  // Incorrect
log.debug("Processing item {}", i);  
}

```



 