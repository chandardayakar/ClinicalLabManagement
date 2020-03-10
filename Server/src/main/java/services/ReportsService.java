package services;

import Utils.FileSystemStorageUtil;
import beans.Report;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

@Path("/reports")
public class ReportsService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllReports() {
        JsonObject allReports = FileSystemStorageUtil.getAllReports();
        return Response.ok(allReports.toString())
                .build();
    }


    @GET
    @Path("{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReport(@PathParam("reportId") String reportName) {
        Report report = FileSystemStorageUtil.getReport(reportName);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Response.ok(mapper.writeValueAsString(report))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage())
                    .build();
        }

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
                return Response.serverError().entity("Cannot create Reports with no Tests")
                        .build();
            }

            Iterator<JsonElement> iterator = tests.iterator();
            while (iterator.hasNext()) {
                JsonElement element = iterator.next();
                Test test =  null;
                String testName = element.getAsString();
                try {
                     test = FileSystemStorageUtil.getTest(testName);
                } catch (Exception e) {
                    return Response.serverError().entity("Unable to find Test with Name - " + testName)
                            .build();
                }
                Report report = mapper.readValue(payload, Report.class);

                report.setTestName(testName);
                report.setTest(test);

                if(report.getPatientName() == null || report.getPatientName().isEmpty()){
                    return Response.serverError().entity("Unable to create a test without Patient Name")
                            .build();
                }

                String reportId = report.getPatientName() + "_" + report.getTestName() + "_" + System.currentTimeMillis();

                FileSystemStorageUtil.storeReport(reportId, report);

                reportIdArray.add(reportId);

            }

            resJson.add("reportIds", reportIdArray);
            return Response.ok(resJson.toString())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();

            return Response.serverError().entity("Failed to create Reports, check logs and try again")
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

            ObjectMapper mapper = new ObjectMapper();
            Test test = mapper.readValue(payload, Test.class);

            Report storedReport = FileSystemStorageUtil.getReport(reportId);
            Test storedTest = storedReport.getTest();

            storedTest.setFields(test.getFields());

            storedReport.setTest(storedTest);

            FileSystemStorageUtil.updateReport(reportId, storedReport);

            return Response.ok()
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().entity("Report Updating Failed check server logs for more details")
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
    @Path("save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveReport(InputStream is,
                               @HeaderParam("reportName") String reportName) {
        if (reportName == null || reportName.isEmpty()) {
            return Response.serverError()
                    .entity("Report Name cannot be null").build();
        }
        String filePath = null;
        try {
            filePath = FileSystemStorageUtil.saveReport(reportName, is);
            return Response.created(new URI(filePath))
                    .build();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return Response.serverError().entity("Report Saving Failed check server logs for more details")
                    .build();
        }
    }

    @GET
    @Path("download/{reportId}")
    public Response downloadReport(@PathParam("reportId") String reportId) {
        if (reportId == null || reportId.isEmpty()) {
            return Response.serverError()
                    .entity("Report Name cannot be null").build();
        }
        try {
            InputStream is = FileSystemStorageUtil.downloadReport(reportId);
            return Response.ok(is, MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("not Found")) {
                return Response.status(404)
                        .entity("Report Not Found").build();
            }
            return Response.serverError().entity("Server error occurred, please check logs for more details")
                    .build();
        }
    }
}
