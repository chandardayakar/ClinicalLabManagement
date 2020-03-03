package beans;

import java.util.List;
import java.util.Set;

public class Test {

  private String testName;

  private String cost;

  private Set<Field> fields;

  public String getTestName() {
    return testName;
  }

  public void setTestName(String testName) {
    this.testName = testName;
  }

  public String getCost() {
    return cost;
  }

  public void setCost(String cost) {
    this.cost = cost;
  }

  public Set<Field> getFields() {
    return fields;
  }

  public void setFields(Set<Field> fields) {
    this.fields = fields;
  }
}
