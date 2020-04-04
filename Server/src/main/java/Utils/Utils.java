package Utils;

import beans.DateObj;
import beans.Search;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static List<String> dateTypeFields = Arrays.asList("created", "lastModified", "sampleCollectionDate", "reportingDate");

    public static JsonObject errorMessageToJson(String err) {
        JsonObject obj = new JsonObject();
        obj.addProperty("error", err);
        return obj;
    }

    public static Search parseSearch(String searchQuery) throws Exception {
        try {
            String[] s = searchQuery.split(" ");
            Search res = new Search();

            res.setKey(s[0]);
            res.setOperator(Operator.fromString(s[1]));

            String value = "";
            for (int i = 2; i < s.length; i++) {
                value += " " + s[i];
            }
            res.setValue(value.trim());
            if (dateTypeFields.contains(res.getKey())) {
                Gson gson = new GsonBuilder().create();
                DateObj dateObj = gson.fromJson(value, DateObj.class);
                res.setValue(dateObj.toString());
            }

            return res;
        } catch (Exception e) {
            System.out.println("Failed to parse query due to - " + e.getMessage());
            throw new Exception("Invalid Search Query");
        }
    }
}
