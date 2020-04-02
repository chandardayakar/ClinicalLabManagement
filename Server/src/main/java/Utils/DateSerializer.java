package Utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer implements JsonSerializer<Date> {


    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {

        String dateFormated = new SimpleDateFormat().format(date);
        JsonPrimitive primtive = new JsonPrimitive(dateFormated);
        return primtive;

    }
}
