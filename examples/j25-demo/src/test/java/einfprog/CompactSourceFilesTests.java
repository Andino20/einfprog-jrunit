package einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import plus.einfprog.junit.EinfprogJRunitExtension;
import plus.einfprog.proxy.ProxyUtil;

public class CompactSourceFilesTests {

    @RegisterExtension
    static EinfprogJRunitExtension einfprogJrunit = EinfprogJRunitExtension.getDefault();

    @Test
    void callAddTest_Framework() {
        ProgramProxy proxy = ProxyUtil.create(ProgramProxy.class);
        Assertions.assertEquals(42, proxy.add(21, 21));
    }
}
