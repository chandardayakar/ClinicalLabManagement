import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
        Date t = new Date(1585827068001L);
        String decoded = URLDecoder.decode("project%20%3D%20HSP","UTF-8");
        System.out.println(decoded);

        Calendar c = new GregorianCalendar();

    }
}
