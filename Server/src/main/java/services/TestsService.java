package services;

import Utils.FileSystemStorageUtil;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Path("/tests")
public class TestsService {


    @GET
    public Response getAllTests() {
        JsonObject allTests = FileSystemStorageUtil.getAllTests();

        return Response.ok(allTests.toString())
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }


    @GET
    @Path("{testId}")
    public Response getTest(@PathParam("testId") String testId) {
        Test test = FileSystemStorageUtil.getTest(testId);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Response.ok(mapper.writeValueAsString(test))
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTest(InputStream is) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String payload = writer.toString();

            ObjectMapper mapper = new ObjectMapper();
            Test test = mapper.readValue(payload, Test.class);

            FileSystemStorageUtil.storeTest(test.getTestName(), test);

            return Response.created(URI.create(test.getTestName())).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

}
