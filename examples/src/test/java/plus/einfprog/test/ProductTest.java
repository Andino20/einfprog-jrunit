package plus.einfprog.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import plus.einfprog.proxy.ProxyUtil;

class ProductTest {

    @Test
    void getterTests() {
        Product p1 = ProxyUtil.create(Product.class, "#001", "Apple", 1.5, 55);
        Assertions.assertEquals("Apple", p1.getName());
        Assertions.assertEquals(1.5, p1.getPrice(), 0.001);
        Assertions.assertEquals(55, p1.getStock());
        Assertions.assertEquals("#001", p1.getId());

        p1.copyPrice(p1);
        Product p2 = p1.getReference();
        Assertions.assertEquals(1.5, p2.getPrice());

        p1.sell(Integer.valueOf(3));
    }

}
