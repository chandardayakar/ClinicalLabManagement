package services;

import Utils.Utils;
import storage.FileSystemStorage;
import beans.Field;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Path("/tests")
public class TestsService {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTests() {
        JsonObject allTests = FileSystemStorage.getAllTests();

        return Response.ok(allTests.toString())
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{testId}")
    public Response getTest(@PathParam("testId") String testId) {
        Test test = FileSystemStorage.getTest(testId);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Response.ok(mapper.writeValueAsString(test))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            JsonObject err = Utils.errorMessageToJson(e.getMessage());
            return Response.serverError().entity(err.toString())
                    .build();
        }

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

            Test check = FileSystemStorage.getTest(test.getTestName());

            if (check != null) {
                JsonObject err = Utils.errorMessageToJson("Test with given name already exists, - " + check.getTestName());
                return Response.serverError().entity(err.toString()).build();
            }

            FileSystemStorage.storeTest(test.getTestName(), test);


            return Response.created(URI.create(URLEncoder.encode(test.getTestName(), "UTF-8")))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            JsonObject err = Utils.errorMessageToJson(e.getMessage());
            return Response.serverError()
                    .entity(err.toString())
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{testId}")
    public Response updateTest(@PathParam("testId") String testId,
                               InputStream is) {
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, StandardCharsets.UTF_8);
            String payload = writer.toString();

            ObjectMapper mapper = new ObjectMapper();
            Test test = mapper.readValue(payload, Test.class);

            Test storedTest = FileSystemStorage.getTest(testId);

            if (storedTest == null) {
                JsonObject err = Utils.errorMessageToJson("Test with the given name not found. - " + testId);
                return Response.serverError().entity(err.toString()).build();
            }

            test.setTestName(storedTest.getTestName());
            if(test.getCost() == null){
                test.setCost(storedTest.getCost());
            }

            FileSystemStorage.updateTest(storedTest,test);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(Utils.errorMessageToJson(e.getMessage()).toString())
                    .build();
        }
    }

    @DELETE
    @Path("{testId}")
    public Response deleteTest(@PathParam("testId") String testId) {
        Test test = FileSystemStorage.getTest(testId);
        if (test == null) {
            JsonObject err = Utils.errorMessageToJson("Test with the given name not found. - " + testId);
            return Response.serverError().entity(err.toString()).build();
        }

        try {
            FileSystemStorage.deleteTest(test.getTestName(), test.getCost());
        } catch (Exception e) {
            JsonObject err = Utils.errorMessageToJson(e.getMessage());
            return Response.serverError().entity(err.toString()).build();
        }

        return Response.ok().build();
    }

}
