package beans;

import Utils.FileSystemStorageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report {

  private String patientName;

  private String age;

  private String gender;

  private String mobile;

  private List<String> testNames;

  private Map<String, Test> tests;

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

  public Map<String, Test> getTests() {
    return tests;
  }

  public void setTests(Map<String, Test> tests) {
    this.tests = tests;
  }

  public Test getTest(String testName) {
    return tests.get(testName);
  }

  public void setTest(String testName, Test test) {
    tests.put(testName, test);
  }

  public void populateTests() {
    if (testNames == null || testNames.isEmpty()) {
      return;
    }
    tests = new HashMap<String, Test>();
    testNames.forEach(a -> tests.put(a, FileSystemStorageUtil.getTest(a)));
  }
}
