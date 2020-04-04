package services;

import Utils.DateSerializer;
import Utils.Utils;
import beans.Report;
import beans.Search;
import beans.Test;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import storage.FileSystemStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;

@Path("/reports")
public class ReportsService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReports(@QueryParam("search") String searchQueryEncoded) throws UnsupportedEncodingException {

        JsonObject allReports = FileSystemStorage.getAllReports();
        if (searchQueryEncoded != null) {
            String searchQuery = URLDecoder.decode(searchQueryEncoded,"UTF-8");
            Search search = null;
            try {
                search = Utils.parseSearch(searchQuery);
            } catch (Exception e) {
                e.printStackTrace();
                JsonObject err = Utils.errorMessageToJson(e.getMessage());
                return Response.serverError().entity(err.toString()).build();
            }
            JsonObject reports = FileSystemStorage.searchReports(search);

            return Response.ok(reports.toString()).build();
        } else {
            return Response.ok(allReports.toString())
                    .build();
        }
    }


    @GET
    @Path("{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@PathParam("reportId") String reportName) {
        Report report = FileSystemStorage.getReport(reportName);

        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create();

        return Response.ok(gson.toJson(report))
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReport(InputStream is) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String payload = writer.toString();

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonObject resJson = new JsonObject();
            JsonArray reportIdArray = new JsonArray();

            JsonObject jsonPayload = new Gson().fromJson(payload, JsonObject.class);
            JsonArray tests = jsonPayload.getAsJsonArray("testNames");
            if (tests == null || tests.size() == 0) {
                JsonObject err = Utils.errorMessageToJson("Cannot create Reports with no Tests");
                return Response.serverError().entity(err.toString())
                        .build();
            }

            for (JsonElement element : tests) {
                Test test = null;
                String testName = element.getAsString();
                try {
                    test = FileSystemStorage.getTest(testName);
                    if (test == null) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    JsonObject err = Utils.errorMessageToJson("Unable to find Test with Name - " + testName);
                    return Response.serverError().entity(err.toString())
                            .build();
                }
                Report report = mapper.readValue(payload, Report.class);

                report.setTestName(testName);
                report.setTest(test);

                if (report.getPatientName() == null || report.getPatientName().isEmpty()) {
                    JsonObject err = Utils.errorMessageToJson("Unable to create a test without Patient Name");
                    return Response.serverError().entity(err.toString())
                            .build();
                }

                report.setCreated(new Date(System.currentTimeMillis()));
                report.setLastModified(new Date(System.currentTimeMillis()));

                String reportId = report.getPatientName() + "_" + report.getTestName() + "_" + System.currentTimeMillis();

                FileSystemStorage.storeReport(reportId, report);

                reportIdArray.add(reportId);

            }

            resJson.add("reportIds", reportIdArray);
            return Response.created(URI.create("")).entity(resJson.toString())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            JsonObject err = Utils.errorMessageToJson("Failed to create Reports, check logs and try again");
            return Response.serverError().entity(err.toString())
                    .build();
        }
    }

    @PUT
    @Path("{reportId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateReport(@PathParam("reportId") String reportId,
                                 InputStream is) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String payload = writer.toString();

            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Report report = mapper.readValue(payload, Report.class);

            Test test = mapper.readValue(payload, Test.class);

            Report storedReport = FileSystemStorage.getReport(reportId);
            Test storedTest = storedReport.getTest();

            // as we dont get test from here we populate it with existing values
            // fields we get so no need to populate them
            test.setTestName(storedTest.getTestName());
            test.setCost(storedTest.getCost());

            if (report.getMobile() != null) {
                storedReport.setMobile(report.getMobile());
            }

            if (report.getAge() != null) {
                storedReport.setAge(report.getAge());
            }

            if (report.getGender() != null) {
                storedReport.setGender(report.getGender());
            }

            if (report.getReportingDate() != null) {
                storedReport.setReportingDate(report.getReportingDate());
            }

            if (report.getSampleCollectionDate() != null) {
                storedReport.setSampleCollectionDate(report.getSampleCollectionDate());
            }

            if (report.getReferredBy() != null) {
                storedReport.setReferredBy(report.getReferredBy());
            }

            storedTest.setFields(test.getFields());

            storedReport.setTest(storedTest);

            storedReport.setLastModified(new Date(System.currentTimeMillis()));

            FileSystemStorage.updateReport(reportId, storedReport);

            return Response.ok()
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            JsonObject err = Utils.errorMessageToJson("Report Updating Failed check server logs for more details");
            return Response.serverError().entity(err.toString())
                    .build();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @POST
    @Path("{reportId}/save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveReport(InputStream is,
                               @PathParam("reportId") String reportId) {
        if (reportId == null || reportId.isEmpty()) {
            JsonObject err = Utils.errorMessageToJson("Report Name cannot be null");
            return Response.serverError()
                    .entity(err.toString()).build();
        }
        String filePath = null;
        try {
            filePath = FileSystemStorage.saveReport(reportId, is);
            return Response.created(new URI(filePath))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject err = Utils.errorMessageToJson("Report Saving Failed check server logs for more details");
            return Response.serverError().entity(err.toString())
                    .build();
        }
    }

    @GET
    @Path("{reportId}/download")
    public Response downloadReport(@PathParam("reportId") String reportId) {
        if (reportId == null || reportId.isEmpty()) {
            JsonObject err = Utils.errorMessageToJson("Report Name cannot be null");
            return Response.serverError()
                    .entity(err.toString()).build();
        }
        try {
            InputStream is = FileSystemStorage.downloadReport(reportId);
            return Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("not Found")) {
                return Response.status(404)
                        .entity("Report Not Found").build();
            }
            JsonObject err = Utils.errorMessageToJson("Server error occurred, please check logs for more details");
            return Response.serverError().entity(err.toString())
                    .build();
        }
    }
}
