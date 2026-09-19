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
        return context;
    }

    public static void clearContext() {
        context = null;
    }

}
