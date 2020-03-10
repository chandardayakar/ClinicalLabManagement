package sync;

public class SyncStatus {
  private static String syncStatus = "NOTSTARTED";


  public static String getSyncStatus() {
    return syncStatus;
  }

  public static void setSyncStatus(String syncStatus) {
    SyncStatus.syncStatus = syncStatus;
  }

}
