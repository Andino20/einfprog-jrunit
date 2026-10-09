# einfprog-jrunit

[![Build](https://github.com/Andino20/einfprog-jrunit/actions/workflows/build.yml/badge.svg)](https://github.com/Andino20/einfprog-jrunit/actions/workflows/build.yml)
[![codecov](https://codecov.io/github/Andino20/einfprog-jrunit/graph/badge.svg?token=XLJXE4C4OV)](https://codecov.io/github/Andino20/einfprog-jrunit)
![Maven Central Version](https://img.shields.io/maven-central/v/io.github.andino20/einfprog-jrunit)
[![javadoc](https://javadoc.io/badge2/io.github.andino20/einfprog-jrunit/javadoc.svg)](https://javadoc.io/doc/io.github.andino20/einfprog-jrunit)

A Java unit-testing framework utilizing reflection and dynamic proxies, built around Junit 6.

It lets you test code without compiling against it: tests talk to the code under test through
`@Proxy` interfaces or the fluent `Reflected` API. If a test fails, a trace of all calls into
the tested code is printed as JSON to stderr.

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
    testImplementation("io.github.andino20:einfprog-jrunit:0.1.1")
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
To call a method with wrapper-typed parameters, pass the types explicitly:

```java
Reflected.on("einfprog.Calculator").call("add", new Class[]{Integer.class, Integer.class}, 2, 3);
```

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

### Errors and timeouts

Exceptions thrown by the tested code are rethrown as `TargetInvocationException` (the original
exception is available via `getCause()`). Each call runs with a timeout (5 seconds by default; 
can be changed via global settings) and throws an `InvocationTimeoutException` when it is exceeded,
so endless loops don't block the test run.

Tests cannot run in parallel, as the library keeps its state in a global context.

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
Apache-2.0
