public class StudentClass {
    public StudentClass() {}

    public StudentClass(int a) {

    }

    public StudentClass(String name) {
        System.out.println("StudentClass Constructor called with " + name);
    }

    public StudentClass(StudentClass other) {

    }

    public static int m1() {
        return 10;
    }

    public static int m2(int a) {
        return a;
    }

}
