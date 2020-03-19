import Utils.FileSystemStorageUtil;
import beans.Report;

public class Test1 {

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
