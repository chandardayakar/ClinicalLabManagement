package Utils;

import beans.Search;
import com.google.gson.JsonObject;

public class Utils {

    public static JsonObject errorMessageToJson(String err){
        JsonObject obj = new JsonObject();
        obj.addProperty("error" ,err);
        return obj;
    }

    public static Search parseSearch(String searchQuery){
        String[] s = searchQuery.split(" ");
        Search res = new Search();

        res.setKey(s[0]);
        res.setOperator(Operator.fromString(s[1]));
        res.setValue(s[2]);

        return res;
    }
}
