package plus.einfprog.pipeline;

public record InvocationPipeline(Pipeline<MethodCall> before,
                                 Pipeline<MethodCallResult> after,
                                 Pipeline<Throwable> exception) {
}
