import Utils.FileSystemStorageUtil;
import beans.Report;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Test {

  public static void main(String[] args) {

    Report report = new Report();
    report.setAge("12");
    report.setGender("Male");
    report.setMobile("8790242424");
    report.setPatientName("abc");
    FileSystemStorageUtil.storeReport("report2", report);

    System.out.println(FileSystemStorageUtil.getAllReports());
    FileSystemStorageUtil.getReport("report1");


  }
}
