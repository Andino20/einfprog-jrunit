package plus.einfprog.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface as a proxy.
 * The {@code value} attribute specifies the fully qualified class path of the
 * target class that the proxy should delegate to.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Proxy {
    String value();
}
