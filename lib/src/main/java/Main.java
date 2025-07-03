import plus.einfprog.reflection.ClassWrapper;
import plus.einfprog.reflection.InstanceFactory;
import plus.einfprog.reflection.InstanceWrapper;
import plus.einfprog.reflection.ReflectionException;

public class Main {

    public static void main(String[] args) throws ReflectionException {
        try {
            ClassWrapper studentClass = new ClassWrapper(Class.forName("StudentClass"));
            InstanceFactory factory = new InstanceFactory(studentClass);

            InstanceWrapper instance = factory.make();
            int result = instance.callAndCast("m1");
            System.out.println(result);
        } catch (ReflectiveOperationException e) {
            throw ReflectionException.from(e);
        }
    }

}
