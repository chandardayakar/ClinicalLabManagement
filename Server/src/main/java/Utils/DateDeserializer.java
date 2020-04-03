package Utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();

        try {
            return new Date(Long.valueOf(date));
        } catch (NumberFormatException e) {
            try {
                return new SimpleDateFormat().parse(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }
}