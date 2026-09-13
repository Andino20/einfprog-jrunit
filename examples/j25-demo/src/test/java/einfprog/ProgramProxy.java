package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("Program")
public interface ProgramProxy {
    void main();
    void helloWorld();
    int add(int a, int b);
}
