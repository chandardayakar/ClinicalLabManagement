package Utils;

import com.google.gson.JsonObject;

public class Utils {

    public static JsonObject errorMessageToJson(String err){
        JsonObject obj = new JsonObject();
        obj.addProperty("error" ,err);
        return obj;
    }
}
