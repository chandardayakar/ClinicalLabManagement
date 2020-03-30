import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        Date t = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        System.out.println(dateFormat.format(t));
        System.out.println(t.toString());
    }
}
