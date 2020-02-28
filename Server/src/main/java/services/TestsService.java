package services;

import Utils.FileSystemStorageUtil;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Path("/tests")
public class TestsService {


  @GET
  public String getAllTests() {
    JsonObject allTests = FileSystemStorageUtil.getAllTests();

    return allTests.toString();
  }


  @GET
  @Path("{testId}")
  public String getTest(@PathParam("testId") String testId) {
    Test test = FileSystemStorageUtil.getTest(testId);

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(test);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return null;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void createTest(InputStream is) {
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(is, writer, StandardCharsets.UTF_8);
      String payload = writer.toString();

      ObjectMapper mapper = new ObjectMapper();
      Test test = mapper.readValue(payload, Test.class);

      FileSystemStorageUtil.storeTest(test.getTestName(), test);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
