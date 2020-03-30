import storage.FileSystemStorage;
import beans.Report;

public class Test1 {

  public static void main(String[] args) {

    Report report = new Report();
    report.setAge("12");
    report.setGender("Male");
    report.setMobile("8790242424");
    report.setPatientName("abc");
    FileSystemStorage.storeReport("report2", report);

    System.out.println(FileSystemStorage.getAllReports());
    FileSystemStorage.getReport("report1");


  }
}
