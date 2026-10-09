package plus.einfprog.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * An {@link InvocationHandler} that also returns the target object that is being proxied.
 */
interface TargetInvocationHandler extends InvocationHandler {

    Object getTarget();

}
