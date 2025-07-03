package plus.einfprog;

import org.junit.Test;
import plus.einfprog.reflection.*;

import static org.junit.Assert.*;

public class ReflectionTest {

    private static final String TEST_SUBJECT_BASE = "plus.einfprog.testsubjects.";

    @Test
    public void makeInstanceDefaultConstructorTest() throws ReflectiveOperationException {
        ClassWrapper cw = new ClassWrapper(Class.forName(TEST_SUBJECT_BASE + "ReflectionTestSubject"));
        InstanceFactory fac = new InstanceFactory(cw);
        InstanceWrapper instance = fac.make();
    }

    @Test
    public void callMethodTest() throws ReflectiveOperationException {
        ClassWrapper cw = new ClassWrapper(Class.forName(TEST_SUBJECT_BASE + "ReflectionTestSubject"));
        InstanceFactory fac = new InstanceFactory(cw);
        fac.make().call("publicMethod");
    }

    @Test
    public void callMethodThenCastTest() throws ReflectiveOperationException {
        ClassWrapper cw = new ClassWrapper(Class.forName(TEST_SUBJECT_BASE + "ReflectionTestSubject"));
        InstanceFactory fac = new InstanceFactory(cw);
        String s = fac.make().call("getPrivateField").cast();
        assertEquals("private", s);
    }

    @Test
    public void callMethodThenWrapTest() throws ReflectiveOperationException {
        ClassWrapper cw = new ClassWrapper(Class.forName(TEST_SUBJECT_BASE + "ReflectionTestSubject"));
        InstanceFactory fac = new InstanceFactory(cw);
        InstanceWrapper instance = fac.make().call("getPrivateField").wrap(String.class);
    }
}
