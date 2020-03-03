package services;

import Utils.FileSystemStorageUtil;
import beans.Field;
import beans.Report;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Path("/reports")
public class ReportsService {

  @GET
  public String getAllReports() {
    JsonObject allReports = FileSystemStorageUtil.getAllReports();

    return allReports.toString();
  }


  @GET
  @Path("{reportId}")
  public String getReport(@PathParam("reportId") String reportName) {
    Report report = FileSystemStorageUtil.getReport(reportName);

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(report);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return null;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public String createReport(InputStream is) {
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, StandardCharsets.UTF_8);
      String payload = writer.toString();

      ObjectMapper mapper = new ObjectMapper();
      Report report = mapper.readValue(payload, Report.class);
      report.populateTests();

      String reportId = report.getPatientName() + "_" + System.currentTimeMillis();

      FileSystemStorageUtil.storeReport(reportId, report);

      String json = "{\"ReportId\" = \"" + reportId + "\"}";
      JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

      return jsonObject.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  @PUT
  @Path("{reportId}/{testname}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void updateReport(@PathParam("reportId") String reportId,
                           @PathParam("testname") String testName,
                           InputStream is) {
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, StandardCharsets.UTF_8);
      String payload = writer.toString();

      ObjectMapper mapper = new ObjectMapper();
      Test test = mapper.readValue(payload, Test.class);

      Report storedReport = FileSystemStorageUtil.getReport(reportId);
      Test storedTest = storedReport.getTest(testName);

      storedTest.setFields(test.getFields());

      storedReport.setTest(testName,storedTest);

      FileSystemStorageUtil.updateReport(reportId, storedReport);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
