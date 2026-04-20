package plus.einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("plus.einfprog.Task")
public interface TaskProxy {

    String getTitle();

}
