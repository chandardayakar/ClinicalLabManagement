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
    return Response.ok(GoogleDriveAuthentication.generatePermissionUrl())
        .header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @Path("/status")
  @GET
  public Response getStatus() {
    return Response.ok(SyncStatus.getSyncStatus())
        .header("Access-Control-Allow-Origin", "*")
        .build();
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
      return Response.status(500).type("text/plain").entity("Check Logs and try again later")
          .header("Access-Control-Allow-Origin", "*")
          .build();
    }

    return Response.ok(SyncStatus.getSyncStatus())
        .header("Access-Control-Allow-Origin", "*")
        .build();
  }

  @Path("/stopsync")
  @POST
  public Response stopSync(){
    SyncStatus.setSyncStatus("STOP");
    return Response.ok()
        .header("Access-Control-Allow-Origin", "*")
        .build();
  }
}
