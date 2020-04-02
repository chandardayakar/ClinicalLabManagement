package storage;

import Utils.DateDeserializer;
import beans.Report;
import beans.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class FileSystemStorage {


    public static void storeTest(String testName, Test test) {
        FileSystemStorage u = new FileSystemStorage();

        try {

            File allTests = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());

            try(JsonReader reader = new JsonReader(new FileReader(allTests))) {
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

    public static JsonObject getAllTests() {
        FileSystemStorage u = new FileSystemStorage();
        File file = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());

        JsonObject json = new JsonObject();
        try(JsonReader reader = new JsonReader(new FileReader(file))) {
            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Test getTest(String testName) {
        FileSystemStorage u = new FileSystemStorage();

        File file = null;
        try {
            String path = u.getClass().getClassLoader().getResource("Tests" + File.separator + testName).getPath();
            file = new File(URLDecoder.decode(path, "UTF-8"));
        } catch (NullPointerException | UnsupportedEncodingException e) {
            return null;
        }

        if (file.exists()) {
            try (JsonReader reader = new JsonReader(new FileReader(file))){
                Gson gson = new Gson();
                Test test = gson.fromJson(reader, Test.class);
                return test;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
    public static void updateTest(Test oldTest, Test newTest) throws Exception
    {
        FileSystemStorage u = new FileSystemStorage();

        File allTests = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());

        try(JsonReader reader = new JsonReader(new FileReader(allTests))) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray array = (JsonArray) json.getAsJsonArray("tests");

            JsonObject oldTestInfo = new JsonObject();
            oldTestInfo.addProperty("name", oldTest.getTestName());
            oldTestInfo.addProperty("price", oldTest.getCost());

            if (array.contains(oldTestInfo)) {

                array.remove(oldTestInfo);

                JsonObject newTestInfo = new JsonObject();
                newTestInfo.addProperty("name", newTest.getTestName());
                newTestInfo.addProperty("price", newTest.getCost());
                array.add(newTestInfo);
                json = new JsonObject();
                json.add("tests", array);

                FileWriter writer = new FileWriter(allTests);
                writer.write(json.toString());
                writer.close();

            } else {
                throw new Exception("Test does not exist");
            }

            File testFolder = new File(u.getClass().getClassLoader().getResource("Tests").getFile());
            File file = new File(testFolder.getAbsolutePath() + File.separator + oldTest.getTestName());

            if (!file.exists()) {
                throw new Exception("Test does not exist");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, newTest);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteTest(String testName, String cost) throws Exception {
        FileSystemStorage u = new FileSystemStorage();

        String path = u.getClass().getClassLoader().getResource("Tests" + File.separator + testName).getPath();
        try {
            File test = new File(URLDecoder.decode(path, "UTF-8"));

            FileUtils.forceDelete(test);

            File allTests = new File(u.getClass().getClassLoader().getResource("Tests" + File.separator + "allTests").getFile());
            JsonReader reader = new JsonReader(new FileReader(allTests));
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            reader.close();
            JsonArray array = (JsonArray) json.getAsJsonArray("tests");

            JsonObject testInfo = new JsonObject();
            testInfo.addProperty("name", testName);
            testInfo.addProperty("price", cost);

            array.remove(testInfo);

            json = new JsonObject();
            json.add("tests", array);

            FileWriter writer = new FileWriter(allTests);
            writer.write(json.toString());
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Test cannot be deleted due to - "+ e.getMessage());
        }
    }


    public static void storeReport(String reportName, Report report) {

        FileSystemStorage u = new FileSystemStorage();

        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportName);


        try {

            File allReports = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + "allReports").getFile());

            ObjectMapper objectMapper = new ObjectMapper();
            try(JsonReader reader = new JsonReader(new FileReader(allReports))) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                JsonArray reports = json.getAsJsonArray("reports");

                JsonObject temp = new JsonObject();
                temp.addProperty("patientName", report.getPatientName());
                temp.addProperty("mobile", report.getMobile());
                temp.addProperty("reportId", reportName);
                temp.addProperty("testName", report.getTestName());
                temp.addProperty("created", new SimpleDateFormat().format(report.getCreated()));
                temp.addProperty("lastModified", new SimpleDateFormat().format(report.getLastModified()));

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

        FileSystemStorage u = new FileSystemStorage();

        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportId);
        try {

            updateReportLastModifiedDate(reportId, new SimpleDateFormat().format(report.getLastModified()));
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, report);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getAllReports() {
        FileSystemStorage u = new FileSystemStorage();
        File file = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + "allReports").getFile());

        JsonObject json = new JsonObject();
        try(JsonReader reader = new JsonReader(new FileReader(file))) {
            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Report getReport(String reportName) {
        FileSystemStorage u = new FileSystemStorage();

        String path = u.getClass().getClassLoader().getResource("Reports" + File.separator + reportName).getPath();


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File file = new File(URLDecoder.decode(path, "UTF-8"));

            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

            Report report = gson.fromJson(reader, Report.class);
            reader.close();
            return report;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveReport(String reportName, InputStream is) throws IOException {
        FileSystemStorage u = new FileSystemStorage();
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
        FileSystemStorage u = new FileSystemStorage();
        File reportsFolder = new File(u.getClass().getClassLoader().getResource("Reports").getFile());

        File report = new File(reportsFolder.getPath() + "/" + reportId);
        if (!report.exists()) {
            throw new Exception("Report not found");
        }

        InputStream is = FileUtils.openInputStream(report);
        return is;
    }

    private static void updateReportLastModifiedDate(String reportId, String lastmodified) {

        FileSystemStorage u = new FileSystemStorage();
        File allReports = new File(u.getClass().getClassLoader().getResource("Reports" + File.separator + "allReports").getFile());

        ObjectMapper objectMapper = new ObjectMapper();
        try (JsonReader reader = new JsonReader(new FileReader(allReports))){
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray reports = json.getAsJsonArray("reports");


            Iterator itr = reports.iterator();

            while (itr.hasNext()) {
                JsonObject next = (JsonObject) itr.next();
                if (next.get("reportId").getAsString().equals(reportId))
                    next.addProperty("lastModified", lastmodified);

            }
            json.add("reports", reports);

            FileWriter writer = new FileWriter(allReports);
            writer.write(json.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
