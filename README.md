# einfprog-jrunit

[![Build](https://github.com/Andino20/einfprog-jrunit/actions/workflows/build.yml/badge.svg)](https://github.com/Andino20/einfprog-jrunit/actions/workflows/build.yml)
[![codecov](https://codecov.io/github/Andino20/einfprog-jrunit/graph/badge.svg?token=XLJXE4C4OV)](https://codecov.io/github/Andino20/einfprog-jrunit)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.andino20/einfprog-jrunit)
[![javadoc](https://javadoc.io/badge2/io.github.andino20/einfprog-jrunit/javadoc.svg)](https://javadoc.io/doc/io.github.andino20/einfprog-jrunit)

A Java unit-testing framework utilizing reflection and dynamic proxies, built around JUnit 6.

It lets you test code without compiling against it: tests talk to the code under test through
`@Proxy` interfaces or the fluent `Reflected` API. If a test fails, its failure message is
replaced with plain-text feedback that lists all calls into the tested code and explains what
went wrong.

## Getting Started

Requires Java 25 and JUnit 6. The library is published to Maven Central.

```kotlin
// build.gradle.kts
repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.github.andino20:einfprog-jrunit:0.1.2")
}
```

The Maven group is `io.github.andino20`, but the Java package is `plus.einfprog`.

## Usage

Every test class must register the `EinfprogJRunitExtension` extension:

```java
@RegisterExtension
static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();
```

### Proxies

Declare an interface with the methods you want to call and point `@Proxy` at the fully
qualified name of the target class.

```java
// Task.java
public class Task {
    private String title;
    
    public Task(String title) {
        this.title = title;
    }
    
    public String getTitle() {
        return title;
    }
}

// TaskProxy.java
@Proxy("einfprog.Task")
public interface TaskProxy {
    String getTitle();
}

// TaskTest.java
@RegisterExtension
static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

@Test
void testGetTitle() {
    TaskProxy task = ProxyUtil.create(TaskProxy.class, "Try einfprog-jrunit");
    assertEquals("Try einfprog-jrunit", task.getTitle());
}
```

Proxies can be passed as arguments and returned from methods (also works with arrays).
They are unwrapped and wrapped automatically.

### Reflection

Without declaring an interface, use `Reflected`:

```java
int sum = Reflected.on("einfprog.Calculator").call("add", 2, 3).get();          // static method
String title = Reflected.on("einfprog.Task").create("Write tests").call("getTitle").get(); // constructor + instance method
TaskProxy task = Reflected.on("einfprog.Task").create("Write tests").as(TaskProxy.class); // wrap into a proxy
```

Parameter types are inferred from the arguments, and boxed values are treated as primitives
(`2` looks for an `int` parameter, not `Integer`). To call a method with wrapper-typed
parameters, pass the types explicitly:

```java
Reflected.on("einfprog.Calculator").call("add", new Class[]{Integer.class, Integer.class}, 2, 3);
```

Calling a `void` method is fine, but using its result (`get()`, `as(...)` or another `call(...)`)
throws a `TargetNotFoundException`.

### Console I/O

`System.out` is captured during each test, and input can be prepared for `System.in`:

```java
@Test
void testGreeting() {
    IOAssertions.prepareInput(Input.of("Alice"));
    Reflected.on("einfprog.Greeter").call("greet");
    IOAssertions.assertOutput("Hello Alice!" + System.lineSeparator());
}
```

`assertOutput` compares against everything printed since the last check and then clears the
captured output. A mismatch throws an `OutputMismatchException`.

### Errors and timeouts

Exceptions thrown by the tested code are rethrown as `TargetInvocationException` (the original
exception is available via `getCause()`). A missing class, method or constructor throws a
`TargetNotFoundException`.

Each call runs with a timeout (5 seconds by default) and throws an `InvocationTimeoutException`
when it is exceeded, so endless loops don't block the test run. The timeout is configured on
the extension:

```java
@RegisterExtension
static final EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault()
        .withSettings(Settings.getDefault()
                .withTimeout(500)
                .withTimeoutUnit(TimeUnit.MILLISECONDS));
```

Tests cannot run in parallel, as the library keeps its state in a global context.

### Failure feedback

When a test fails, the extension replaces the failure message with feedback aimed at the
author of the tested code:

```
Test failed.

Calls made to your code, in order:
   1. new Program()
        -> created Program@5d465e4b
   2. Program@5d465e4b.add(21, 21)
        -> returned 42
   3. Program@5d465e4b.add(2, 2)
        -> returned 4

What went wrong:
  The test expected something else:
    expected: <5> but was: <4>
```

The original exception is kept as the cause, and tests aborted by a failed assumption are
still reported as skipped. A different output format can be plugged in with
`withFormatter(...)` by implementing `TraceFormatter`.

### Examples

More examples can be found in [`examples/`](examples).

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate. Run all tests with:

```sh
./gradlew build
```

## License
MIT
