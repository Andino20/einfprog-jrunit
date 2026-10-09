package plus.einfprog.pipeline.hook;

@FunctionalInterface
public interface ExceptionHook {

    Throwable apply(Throwable throwable);

}
