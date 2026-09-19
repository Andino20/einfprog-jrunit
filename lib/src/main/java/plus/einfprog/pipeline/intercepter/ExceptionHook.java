package plus.einfprog.pipeline.intercepter;

@FunctionalInterface
public interface ExceptionHook {

    Throwable intercept(Throwable throwable);

}
