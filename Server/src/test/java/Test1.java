import Utils.DateSerializer;
import beans.DateObj;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import storage.FileSystemStorage;
import beans.Report;

import java.util.Date;

public class Test1 {

  public static void main(String[] args) {

    Report report = new Report();
    report.setAge("12");
    report.setGender("Male");
    report.setMobile("8790242424");
    report.setPatientName("abc");
    report.setLastModified(new Date() );
    report.setCreated(new Date());
    report.setSampleCollectionDate(new DateObj());
    report.setReportingDate(new DateObj());
    FileSystemStorage.storeReport("report2", report);

    System.out.println(FileSystemStorage.getAllReports());
    FileSystemStorage.getReport("report2");

    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer()).create();
    String s = gson.toJson(report);
    System.out.println(s);


  }
}
