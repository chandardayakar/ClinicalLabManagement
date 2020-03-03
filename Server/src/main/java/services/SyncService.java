package services;

import jdk.nashorn.internal.objects.annotations.Getter;
import sync.GoogleDriveAuthentication;
import sync.GoogleSyncThread;
import sync.SyncStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/Sync")
public class SyncService {

  @Path("/googleauthurl")
  @GET
  public Response getGoogleAuthUrl(){
    return Response.ok(GoogleDriveAuthentication.generatePermissionUrl()).build();
  }

  @Path("/status")
  @GET
  public String getStatus() {
    return SyncStatus.getSyncStatus();
  }

  @Path("/startsync")
  @POST
  public Response startSync(@HeaderParam("code") String code) {

    try {
      GoogleDriveAuthentication.CODE = code;
      GoogleDriveAuthentication.populateTokens();

      if (GoogleDriveAuthentication.aceToken == null || GoogleDriveAuthentication.aceToken.isEmpty()) {
        return Response.status(403).type("text/plain").entity("Please login to google and try again").build();
      }

      GoogleSyncThread gsync = new GoogleSyncThread(GoogleDriveAuthentication.aceToken, GoogleDriveAuthentication.refToken);
      SyncStatus.setSyncStatus("STARTED");

      Thread t = new Thread(gsync);
      t.setName("SyncThread");
      t.start();

    } catch (Exception e) {
      SyncStatus.setSyncStatus("ERROR");
      e.printStackTrace();
      return Response.status(500).type("text/plain").entity("Check Logs and try again later").build();
    }

    return Response.ok(SyncStatus.getSyncStatus()).build();
  }

  @Path("/stopsync")
  @POST
  public Response stopSync(){
    SyncStatus.setSyncStatus("STOP");
    return Response.ok().build();
  }
}
