package plus.einfprog;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import plus.einfprog.proxy.ProxyUtil;

public class TaskTest {

    @Test
    void testMethod() {
        TaskProxy t1 = ProxyUtil.create(TaskProxy.class, "Shopping");
        Assertions.assertEquals("Shopping", t1.getTitle());
        Assertions.assertEquals("Painting", t1.getTitle());
    }

}
