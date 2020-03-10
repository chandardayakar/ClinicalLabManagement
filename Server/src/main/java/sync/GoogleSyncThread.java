package sync;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import java.io.*;
import java.security.GeneralSecurityException;

public class GoogleSyncThread implements Runnable {

  private static final String APPLICATION_NAME = "";
  private Drive driveService = null;

  public GoogleSyncThread(String accessToken, String refreshToken) throws GeneralSecurityException, IOException {
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    Credential cred = new Credential(BearerToken.authorizationHeaderAccessMethod());
    cred.setAccessToken(accessToken);
    cred.setRefreshToken(refreshToken);
    driveService = new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), cred)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  @Override
  public void run() {

    try {
      while (!SyncStatus.getSyncStatus().equals("STOP")) {
        SyncStatus.setSyncStatus("RUNNING");

        String reportsFolderPath = this.getClass().getResource("Reports").getFile();
        File reportsFolder = new File(reportsFolderPath);

        long lastModifiedTime = reportsFolder.lastModified();

        File reportsLastSynced = new File(this.getClass().getResource("Reports" + File.pathSeparator + "lastSynced").getFile());
        FileReader fr = new FileReader(reportsLastSynced);
        BufferedReader br = new BufferedReader(fr);
        long lastSyncedTime = Long.valueOf(br.readLine());

        if (lastModifiedTime != lastSyncedTime) {
          syncToGdrive(reportsFolder, "Reports");
          FileWriter fw = new FileWriter(reportsLastSynced);
          fw.write(String.valueOf(lastModifiedTime));
          fw.close();
        }

        String testsFolderPath = this.getClass().getResource("Tests").getFile();
        File testsFolder = new File(reportsFolderPath);

        lastModifiedTime = reportsFolder.lastModified();

        File testsLastSynced = new File(this.getClass().getResource("Tests" + File.pathSeparator + "lastSynced").getFile());
        fr = new FileReader(reportsLastSynced);
        br = new BufferedReader(fr);
        lastSyncedTime = Long.valueOf(br.readLine());

        if (lastModifiedTime != lastSyncedTime) {
          syncToGdrive(testsFolder, "Tests");
          FileWriter fw = new FileWriter(testsLastSynced);
          fw.write(String.valueOf(lastModifiedTime));
          fw.close();
        }

        SyncStatus.setSyncStatus("DONE");
        wait(30 * 60 * 1000);
      }

    } catch (Exception e) {
      SyncStatus.setSyncStatus("ERROR");
      e.printStackTrace();
    }
  }

  private void syncToGdrive(File syncFolder, String folderName) throws IOException {
    File[] files = syncFolder.listFiles();

    for (File f : files) {
      com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
      fileMetadata.setName("ClinicFiles/" + folderName + "/" + f.getName());
      FileContent mediaContent = new FileContent("image/jpeg", f);
      com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata, mediaContent)
          .setFields("id")
          .execute();
    }
  }

}
