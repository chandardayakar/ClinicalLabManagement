package beans;

import Utils.FileSystemStorageUtil;

import java.util.ArrayList;
import java.util.List;

public class Report {

  private String patientName;

  private String age;

  private String gender;

  private String mobile;

  private List<String> testNames;

  private List<Test> tests;

  public String getPatientName() {
    return patientName;
  }

  public void setPatientName(String patientName) {
    this.patientName = patientName;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public List<String> getTestNames() {
    return testNames;
  }

  public void setTestNames(List<String> testNames) {
    this.testNames = testNames;
  }

  public List<Test> getTests() {
    return tests;
  }

  public void setTests(List<Test> tests) {
    this.tests = tests;
  }

  public void populateTests(){
    if(testNames == null || testNames.isEmpty()){
      return;
    }
    tests = new ArrayList<Test>();
    testNames.forEach(a -> tests.add(FileSystemStorageUtil.getTest(a)));
  }
}
