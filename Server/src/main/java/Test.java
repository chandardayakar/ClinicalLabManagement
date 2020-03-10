public class Test {

    public static void main(String[] args) {
        String s = "hai %s hai";
        s =String.format(s,"hai");
        System.out.println(s);
    }
}
