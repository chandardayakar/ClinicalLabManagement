package storage;

import Utils.DateDeserializer;
import beans.DateObj;
import beans.Report;
import beans.Search;
import beans.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileSystemStorage {


    public static String getDataPath() throws Exception {
        String dataFolderPath = System.getenv("clinic_app_data_folder");
        if (dataFolderPath == null) {
            throw new Exception("Data folder not configured");
        }
        if (dataFolderPath.endsWith(File.separator)) {
            dataFolderPath = dataFolderPath.substring(0, dataFolderPath.indexOf(File.separator));
        }
        return dataFolderPath;
    }

    public static void storeTest(String testName, Test test) {

        try {

            File allTests = new File(getDataPath() + File.separator + "Tests" + File.separator + "allTests");

            try (JsonReader reader = new JsonReader(new FileReader(allTests))) {
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
            File testFolder = new File(getDataPath() + File.separator + "Tests");
            File file = new File(testFolder.getAbsolutePath() + File.separator + testName);

            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, test);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getAllTests() {
        File file = null;
        try {
            file = new File(getDataPath() + File.separator + "Tests" + File.separator + "allTests");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject json = new JsonObject();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {

            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Test getTest(String testName) {

        File file = null;
        try {
            String path = getDataPath() + File.separator + "Tests" + File.separator + testName;
            file = new File(URLDecoder.decode(path, "UTF-8"));
        } catch (Exception e) {
            return null;
        }

        if (file.exists()) {
            try (JsonReader reader = new JsonReader(new FileReader(file))) {
                Gson gson = new Gson();
                Test test = gson.fromJson(reader, Test.class);
                return test;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void updateTest(Test oldTest, Test newTest) throws Exception {

        File allTests = new File(getDataPath() + File.separator + "Tests" + File.separator + "allTests");

        try (JsonReader reader = new JsonReader(new FileReader(allTests))) {
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

            File testFolder = new File(getDataPath() + File.separator + "Tests");
            File file = new File(testFolder.getAbsolutePath() + File.separator + oldTest.getTestName());

            if (!file.exists()) {
                throw new Exception("Test does not exist");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, newTest);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteTest(String testName, String cost) throws Exception {

        String path = getDataPath() + File.separator + "Tests" + File.separator + testName;
        try {
            File test = new File(URLDecoder.decode(path, "UTF-8"));

            FileUtils.forceDelete(test);

            File allTests = new File(getDataPath() + File.separator + "Tests" + File.separator + "allTests");
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
            throw new Exception("Test cannot be deleted due to - " + e.getMessage());
        }
    }


    public static void storeReport(String reportName, Report report) {

        try {
            File reportsFolder = new File(getDataPath() + File.separator + "Reports");

            File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportName);

            File allReports = new File(getDataPath() + File.separator + "Reports" + File.separator + "allReports");

            ObjectMapper objectMapper = new ObjectMapper();
            try (JsonReader reader = new JsonReader(new FileReader(allReports))) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                JsonArray reports = json.getAsJsonArray("reports");

                JsonObject temp = new JsonObject();
                temp.addProperty("patientName", report.getPatientName());
                temp.addProperty("mobile", report.getMobile());
                temp.addProperty("reportId", reportName);
                temp.addProperty("reportStatus", report.getReportStatus().toString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateReport(String reportId, Report newReport) {

        try {
            File reportsFolder = new File(getDataPath() + File.separator + "Reports");

            File file = new File(reportsFolder.getAbsolutePath() + File.separator + reportId);

            Report oldReport = getReport(reportId);
            if (!oldReport.getMobile().equals(newReport.getMobile())) {
                updateReportField(reportId, "mobile", newReport.getMobile());
            }
            if (!oldReport.getLastModified().equals(newReport.getLastModified())) {
                updateReportField(reportId, "lastModified", new SimpleDateFormat().format(newReport.getLastModified()));
            }
            if (!oldReport.getReportStatus().equals(newReport.getReportStatus())) {
                updateReportField(reportId, "reportStatus", newReport.getReportStatus().toString());
            }

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, newReport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonObject getAllReports() {
        File file = null;
        try {
            file = new File(getDataPath() + File.separator + "Reports" + File.separator + "allReports");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject json = new JsonObject();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            Gson gson = new Gson();
            json = gson.fromJson(reader, JsonObject.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Report getReport(String reportName) {

        try {
            String path = getDataPath() + File.separator + "Reports" + File.separator + reportName;
            File file = new File(URLDecoder.decode(path, "UTF-8"));

            JsonReader reader = new JsonReader(new FileReader(file));
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).create();

            Report report = gson.fromJson(reader, Report.class);
            reader.close();
            return report;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String saveReport(String reportName, InputStream is) throws Exception {
        File reportsFolder = new File(getDataPath() + File.separator + "Reports");

        return saveFile(reportsFolder.getPath(), reportName, is);
    }

    public static String saveFile(String path, String fileName, InputStream is) throws IOException {

        File f = new File(path + "/" + fileName);
        f.createNewFile();
        FileUtils.copyInputStreamToFile(is, f);

        return f.getAbsolutePath();
    }

    public static InputStream downloadReport(String reportId) throws Exception {
        File reportsFolder = new File(getDataPath() + File.separator + "Reports");

        File report = new File(reportsFolder.getPath() + "/" + reportId);
        if (!report.exists()) {
            throw new Exception("Report not found");
        }

        InputStream is = FileUtils.openInputStream(report);
        return is;
    }

    private static void updateReportField(String reportId, String key, String value) {

        File allReports = null;
        try {
            allReports = new File(getDataPath() + File.separator + "Reports" + File.separator + "allReports");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (JsonReader reader = new JsonReader(new FileReader(allReports))) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            JsonArray reports = json.getAsJsonArray("reports");


            Iterator itr = reports.iterator();

            while (itr.hasNext()) {
                JsonObject next = (JsonObject) itr.next();
                if (next.get("reportId").getAsString().equals(reportId))
                    next.addProperty(key, value);

            }
            json.add("reports", reports);

            FileWriter writer = new FileWriter(allReports);
            writer.write(json.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JsonObject searchReports(Search search) {

        JsonObject allReports = getAllReports();

        JsonArray resArray = new JsonArray();

        JsonArray reports = allReports.getAsJsonArray("reports");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
        Type type = new TypeToken<List<Report>>() {
        }.getType();
        ArrayList<Report> reportsArray = gson.fromJson(reports, type);


        List<Report> filteredArray = reportsArray.parallelStream().filter(a -> {
            try {
                Object value = PropertyUtils.getProperty(a, search.getKey());
                switch (search.getOperator()) {
                    case eq: {
                        if(value instanceof Date){
                            Calendar valueAsCal = Calendar.getInstance();
                            valueAsCal.setTime((Date)value);

                            DateObj dateObj = DateObj.fromString(search.getValue());
                            Calendar refCal = dateObj.convertToCalender();

                            return valueAsCal.equals(refCal);

                        }else {
                            String valueStr = (String)value;
                            return valueStr.toLowerCase().contains(search.getValue().toLowerCase());
                        }
                    }
                    default:
                        return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }).collect(Collectors.toList());

        JsonElement element = gson.toJsonTree(filteredArray, new TypeToken<List<Report>>() {
        }.getType());

        JsonObject result = new JsonObject();
        result.add("reports", element.getAsJsonArray());

        return result;
    }
}
