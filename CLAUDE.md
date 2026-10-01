# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

einfprog-jrunit is a Java unit-testing library built around JUnit 6. It lets course staff test student code **without compiling against the student's classes**: tests talk to the student code through `@Proxy` interfaces (dynamic proxies) or the fluent `Reflected` API. It was written for a local university course ("einfprog"), so keep solutions simple. Avoid new dependencies and abstraction layers unless they're clearly needed.

## Commands

Requires JDK 25 (Gradle toolchain; foojay downloads it automatically if missing).

```sh
./gradlew build                                   # compile + test all modules (what CI runs)
./gradlew :lib:test                               # library tests only
./gradlew :lib:test --tests 'plus.einfprog.proxy.SimpleProxyTests'              # one class
./gradlew :lib:test --tests 'plus.einfprog.proxy.SimpleProxyTests.invokeProxyMethodTest'  # one method
./gradlew :examples:j25-demo:testFailing          # run intentionally failing demo tests (@Tag("failing")) to inspect the failure/trace output
```

There is no linter configured.

## Modules

- `lib/`: the published library (`plus.einfprog`, artifact `einfprog-jrunit`). JUnit and Lombok are `compileOnly`, so users supply their own JUnit.
- `examples/proxy`: a realistic course exercise (`src/main` stands in for student code, `src/test` holds `@Proxy` interfaces and tests).
- `examples/j25-demo`: Java 25 compact source files (an implicit `Program` class) and console I/O tests. Tests tagged `failing` are excluded from `test` and only run via `testFailing`.

## Architecture

Every call into student code, whether a proxy method call, `Reflected.call` or `Reflected.create` (constructors), becomes an `Invocation` record and runs through `InvocationPipeline`:

1. **before** hooks, in this order: `ProxyAutoWrapper` (unwraps proxy args and types to the target types) → `InvocationResolver` (finds the `Method` or `Constructor`; the name `"<init>"` means a constructor; the `Any` type is a wildcard) → `InvocationTracer` (logs a call event).
2. The resolved `Executable` is invoked on a **separate thread with a timeout** (`Settings`, 5 seconds by default) → `RuntimeTimeoutException`. Exceptions from student code run through the **exception** hooks and are rethrown wrapped in `TargetInvocationException` (the original is `getCause()`).
3. **after** hooks: `InvocationTracer` (logs a return event) → `ProxyAutoWrapper` (wraps return values back into proxies, including arrays).

The default hooks are registered in the `Context` record's constructor. `EinfprogJRunitExtension` (registered via `@RegisterExtension`) creates a new `Context` before each test and stores it in the **global static** `EinfprogJRunit` (so tests can't run in parallel). It also redirects `System.out` into `Context.out()` (read by `IOAssertions.assertOutput`). When a test fails, it prints the collected trace as JSON (`JsonTraceFormatter`) to stderr; this is the feedback students see.

`Invocation`/`InvocationResult` are immutable records (Lombok `@With`/`@Builder`). Hooks return modified copies rather than mutating them. `Invocation.withExecutable` also rewrites the signature to match the resolved executable.

A proxy is an interface annotated with `@Proxy("fully.qualified.TargetClass")` (nested classes use `$`, e.g. `"pkg.Outer$Inner"`). `ProxyUtil.create(ProxyClass.class, ctorArgs...)` instantiates the target via `Reflected` and wraps it. `ProxyUtil` handles wrap/unwrap for objects, classes and (multi-dimensional) arrays.

`Reflected` maps boxed argument types to primitives (`Integer` → `int`) when inferring parameter types. To call methods with wrapper-typed parameters, pass explicit `Class<?>[]` types.

## Conventions

- Library tests define the classes under test as nested static classes inside the test class and point `@Proxy` at them by their binary name.
- Every test class that uses proxies or `Reflected` must register `EinfprogJRunitExtension.getDefault()`, since the pipeline reads its settings from the global context.
