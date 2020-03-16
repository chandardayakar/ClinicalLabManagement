package Utils;

import beans.Report;
import beans.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class FileSystemStorageUtil {


    public static void storeTest(String testName, Test test) {
        {

            FileSystemStorageUtil u = new FileSystemStorageUtil();

            try {

                File allTests = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());

                try {
                    JsonReader reader = new JsonReader(new FileReader(allTests));
                    Gson gson = new Gson();
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    JsonArray array = (JsonArray) json.getAsJsonArray("tests");
                    JsonObject testInfo = new JsonObject();
                    testInfo.addProperty("name", testName);
                    testInfo.addProperty("price", test.getCost());
                    array.add(testInfo);
                    json = new JsonObject();
                    json.add("tests", array);

                    FileWriter writer = new FileWriter(allTests);
                    writer.write(json.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File testFolder = new File(u.getClass().getClassLoader().getResource("Tests").getFile());
                File file = new File(testFolder.getAbsolutePath() + File.separator + testName);

                if (!file.exists()) {
                    file.createNewFile();
                }
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(file, test);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JsonObject getAllTests() {
        FileSystemStorageUtil u = new FileSystemStorageUtil();
        File file = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());

        JsonObject json = new JsonObject();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Test getTest(String testName) {
        FileSystemStorageUtil u = new FileSystemStorageUtil();

        File file = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + testName).getFile());

        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            Test test = gson.fromJson(reader, Test.class);
            return test;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteTest(String testName, String cost) {
        FileSystemStorageUtil u = new FileSystemStorageUtil();

        File file = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + testName).getFile());

        try {
            File allTests = new File(file.getPath() + File.pathSeparator + "allTests");
            JsonReader reader = new JsonReader(new FileReader(allTests));
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray array = (JsonArray) json.getAsJsonArray("tests");

            JsonObject testInfo = new JsonObject();
            testInfo.addProperty("name", testName);
            testInfo.addProperty("price", cost);

            array.remove(testInfo);

            File test = new File(file.getPath() + File.pathSeparator + testName);

            test.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void storeReport(String reportName, Report report) {

        FileSystemStorageUtil u = new FileSystemStorageUtil();

        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportName);


        try {

            File allReports = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + "allReports").getFile());

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonReader reader = new JsonReader(new FileReader(allReports));
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                JsonArray reports = json.getAsJsonArray("reports");

                JsonObject temp = new JsonObject();
                temp.addProperty("patientName", report.getPatientName());
                temp.addProperty("mobile", report.getMobile());
                temp.addProperty("reportId", reportName);
                reports.add(temp);

                json.add("reports", reports);

                FileWriter writer = new FileWriter(allReports);
                writer.write(json.toString());
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            file.createNewFile();

            objectMapper.writeValue(file, report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateReport(String reportId, Report report) {

        FileSystemStorageUtil u = new FileSystemStorageUtil();

        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportId);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getAllReports() {
        FileSystemStorageUtil u = new FileSystemStorageUtil();
        File file = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + "allReports").getFile());

        JsonObject json = new JsonObject();
        try {

            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Report getReport(String reportName) {
        FileSystemStorageUtil u = new FileSystemStorageUtil();

        File file = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + reportName).getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new Gson();
            Report report = gson.fromJson(reader, Report.class);
            return report;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveReport(String reportName, InputStream is) throws IOException {
        FileSystemStorageUtil u = new FileSystemStorageUtil();
        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        return saveFile(reportsFolder.getPath(), reportName, is);
    }

    public static String saveFile(String path, String fileName, InputStream is) throws IOException {

        File f = new File(path + "/" + fileName);
        f.createNewFile();
        FileUtils.copyInputStreamToFile(is, f);

        return f.getAbsolutePath();
    }

    public static InputStream downloadReport(String reportId) throws Exception {
        FileSystemStorageUtil u = new FileSystemStorageUtil();
        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File report = new File(reportsFolder.getPath() + "/" + reportId);
        if (!report.exists()) {
            throw new Exception("Report not found");
        }

        InputStream is = FileUtils.openInputStream(report);
        return is;
    }
}
