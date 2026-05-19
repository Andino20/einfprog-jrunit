package plus.einfprog.pipeline.intercepter;

@FunctionalInterface
public interface ExceptionInterceptor {

    Throwable intercept(Throwable throwable);

}
