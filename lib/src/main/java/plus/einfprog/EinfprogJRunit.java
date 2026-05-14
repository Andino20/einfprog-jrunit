package plus.einfprog;

public class EinfprogJRunit {

    private static final ThreadLocal<Context> context = new ThreadLocal<>();

    public static void setContext(Context context) {
        EinfprogJRunit.context.set(context);
    }

    public static void clearContext() {
        EinfprogJRunit.context.remove();
    }

    public static Context getContext() {
        return context.get();
    }
}
