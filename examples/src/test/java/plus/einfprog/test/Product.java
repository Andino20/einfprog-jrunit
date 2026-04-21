package plus.einfprog.test;

import plus.einfprog.proxy.Proxy;

@Proxy("plus.einfprog.Product")
public interface Product {

    String getName();

    double getPrice();

    int getStock();

    String getId();

    void sell(int amount);

    void restock(int amount);

    void copyPrice(Product other);

    Product getReference();

    String toString();

}
