package plus.einfprog.proxy;

import java.lang.reflect.InvocationHandler;

public interface TargetInvocationHandler extends InvocationHandler {

    Object getTarget();

}
