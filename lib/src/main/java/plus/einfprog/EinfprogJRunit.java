package plus.einfprog;

/**
 * A globally accessible class for storing the context of the current environment.
 */
public class EinfprogJRunit {

    private static Context context;

    private EinfprogJRunit() {
    }

    public static void setContext(Context context) {
        EinfprogJRunit.context = context;
    }

    public static Context getContext() {
        if (context == null)
            throw new IllegalStateException("No active test context. Register EinfprogJRunitExtension with @RegisterExtension and create proxies inside a test.");
        return context;
    }

    public static void clearContext() {
        context = null;
    }

}
