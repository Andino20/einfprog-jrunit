package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("einfprog.Date")
public interface DateProxy {

    int compareTo(DateProxy other);

}
