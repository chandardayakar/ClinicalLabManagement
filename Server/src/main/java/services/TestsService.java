package services;

import Utils.FileSystemStorageUtil;
import beans.Field;
import beans.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.StringUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Path("/tests")
public class TestsService {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTests() {
        JsonObject allTests = FileSystemStorageUtil.getAllTests();

        return Response.ok(allTests.toString())
                .build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{testId}")
    public Response getTest(@PathParam("testId") String testId) {
        Test test = FileSystemStorageUtil.getTest(testId);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return Response.ok(mapper.writeValueAsString(test))
                    .build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage())
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

            Test check = FileSystemStorageUtil.getTest(test.getTestName());

            if (check != null) {
                return Response.serverError().entity("Test with given name already exists, - " + check.getTestName()).build();
            }

            FileSystemStorageUtil.storeTest(test.getTestName(), test);

            return Response.created(URI.create(test.getTestName()))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(e.getMessage())
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

            Test storedTest = FileSystemStorageUtil.getTest(testId);

            if (storedTest == null) {
                return Response.serverError().entity("Test with the given name not found. - " + testId).build();
            }

            if (!Strings.isNullOrEmpty(test.getCost())) {
                storedTest.setCost(test.getCost());
            }
            if (test.getFields() != null) {
                Set<Field> fields = test.getFields();
//                fields.addAll(storedTest.getFields());

                storedTest.setFields(fields);
            }

            return Response.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("{testId}")
    public Response deleteTest(@PathParam("testId") String testId) {
        Test test = FileSystemStorageUtil.getTest(testId);
        if (test == null) {
            return Response.serverError().entity("Test with the given name not found. - " + testId).build();
        }

        FileSystemStorageUtil.deleteTest(test.getTestName(), test.getCost());

        return Response.ok().build();
    }

}
