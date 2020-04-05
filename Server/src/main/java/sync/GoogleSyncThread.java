package sync;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import storage.FileSystemStorage;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleSyncThread implements Runnable {

    private static final String APPLICATION_NAME = "";
    private Drive driveService = null;
    private String reportsGFileId;
    private String testsGFileId;


    public GoogleSyncThread(String accessToken, String refreshToken) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Credential cred = new Credential(BearerToken.authorizationHeaderAccessMethod());
        cred.setAccessToken(accessToken);
        cred.setRefreshToken(refreshToken);
        driveService = new Drive.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), cred)
                .setApplicationName(APPLICATION_NAME)
                .build();

        populateGDriveFolderIds();

    }

    @Override
    public void run() {

        try {
            while (!SyncStatus.getSyncStatus().equals("STOP")) {
                SyncStatus.setSyncStatus("RUNNING");

                String reportsFolderPath = FileSystemStorage.getDataPath() + File.separator + "Reports";
                File reportsFolder = new File(reportsFolderPath);

                long lastModifiedTime = reportsFolder.lastModified();
                File reportsLastSynced = new File(FileSystemStorage.getDataPath() + File.separator + "Reports" + File.separator + "lastSynced");
                FileReader fr = new FileReader(reportsLastSynced);
                BufferedReader br = new BufferedReader(fr);
                String value = br.readLine();
                long lastSyncedTime = Long.parseLong(value != null ? value : "0");

                if (lastModifiedTime != lastSyncedTime) {
                    syncToGdrive(reportsFolder, "Reports");
                    FileWriter fw = new FileWriter(reportsLastSynced);
                    fw.write(String.valueOf(lastModifiedTime));
                    fw.close();
                }

                String testsFolderPath = FileSystemStorage.getDataPath() + File.separator + "Tests";
                File testsFolder = new File(testsFolderPath);

                lastModifiedTime = testsFolder.lastModified();

                File testsLastSynced = new File(FileSystemStorage.getDataPath() + File.separator + "Tests" + File.separator + "lastSynced");
                fr = new FileReader(testsLastSynced);
                br = new BufferedReader(fr);
                value = br.readLine();
                lastSyncedTime = Long.parseLong(value != null ? value : "0");

                if (lastModifiedTime != lastSyncedTime) {
                    syncToGdrive(testsFolder, "Tests");
                    FileWriter fw = new FileWriter(testsLastSynced);
                    fw.write(String.valueOf(lastModifiedTime));
                    fw.close();
                }

                Thread.sleep(30 * 1000);
            }

        } catch (Exception e) {
            SyncStatus.setSyncStatus("ERROR");
            e.printStackTrace();
        }
    }

    private void populateGDriveFolderIds() throws IOException {

        String clinicalLabDataFolderId = getGdriveFolderId("ClinicalLabData", "root");
        if (clinicalLabDataFolderId == null) {
            clinicalLabDataFolderId = createGdriveFolder("ClinicalLabData", "root");
        }

        testsGFileId = getGdriveFolderId("Tests", clinicalLabDataFolderId);
        if (testsGFileId == null) {
            testsGFileId = createGdriveFolder("Tests", clinicalLabDataFolderId);
        }

        reportsGFileId = getGdriveFolderId("Reports", clinicalLabDataFolderId);
        if (reportsGFileId == null) {
            reportsGFileId = createGdriveFolder("Reports", clinicalLabDataFolderId);
        }

    }

    private String createGdriveFolder(String folderName, String parentFolderId) throws IOException {

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(parentFolderId));

        com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata)
                .setFields("id")
                .execute();
        return file.getId();
    }

    private String getGdriveFolderId(String folderName, String parent) throws IOException {
        String pageToken = null;
        FileList result = driveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and name='" +folderName+ "' and parents in '" +parent+"'")
                .setSpaces("drive")
                .setFields("nextPageToken, files(id)")
                .setPageToken(pageToken)
                .execute();
        for (com.google.api.services.drive.model.File file : result.getFiles()) return file.getId();

        return null;

    }

    private void syncToGdrive(File syncFolder, String folderName) throws IOException {
        File[] files = syncFolder.listFiles();
        String gDriveParentId = folderName == "Reports"? reportsGFileId :testsGFileId;
        for (File f : files) {
            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
            fileMetadata.setName(f.getName());
            fileMetadata.setParents(Collections.singletonList(gDriveParentId));
            FileContent mediaContent = new FileContent("text/plain", f);
            com.google.api.services.drive.model.File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
        }
    }

}
