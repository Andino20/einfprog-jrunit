package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("einfprog.TimedTask")
public interface TimedTaskProxy extends TaskProxy {

    DateProxy getDeadline();

}
