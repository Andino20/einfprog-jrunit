package plus.einfprog;

import org.junit.Test;
import plus.einfprog.reflection.*;
import plus.einfprog.testsubjects.ReflectionTestSubject;

import static org.junit.Assert.*;

public class ReflectionTest {

    @Test
    public void makeInstanceDefaultConstructorTest() {
        InstanceFactory fac = new InstanceFactory(ReflectionTestSubject.class);
        InstanceWrapper instance = fac.make();
    }

    @Test
    public void callMethodTest() {
        InstanceFactory fac = new InstanceFactory(ReflectionTestSubject.class);
        fac.make().call("publicMethod");
    }

    @Test
    public void callMethodThenCastTest() {
        InstanceFactory fac = new InstanceFactory(ReflectionTestSubject.class);
        String s = fac.make().call("getPrivateField").cast();
        assertEquals("private", s);
    }

    @Test
    public void callMethodThenWrapTest() {
        InstanceFactory fac = new InstanceFactory(ReflectionTestSubject.class);
        InstanceWrapper instance = fac.make().call("getPrivateField").wrap(String.class);
    }
}
