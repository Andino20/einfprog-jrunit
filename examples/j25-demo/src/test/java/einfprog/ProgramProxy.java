package einfprog;

import plus.einfprog.proxy.Proxy;

@Proxy("Program")
public interface ProgramProxy {
    void main();
    int add(int a, int b);
    void thisThrows(Throwable t) throws Throwable;
    int[] addVector(int[] a, int[] b);
    boolean[][] getGraphBack(boolean[][] graph);
}
