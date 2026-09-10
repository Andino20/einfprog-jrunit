package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("einfprog.Task")
public interface TaskProxy {

    String getTitle();

    boolean isDone();

    void setDone(boolean done);

}
