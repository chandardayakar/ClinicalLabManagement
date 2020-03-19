import { Component, OnInit, ViewChild, ElementRef } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { TestsService } from "../services/tests.service";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-edit-test",
  templateUrl: "./edit-test.component.html",
  styleUrls: ["./edit-test.component.css"]
})
export class EditTestComponent implements OnInit {
  public testId;
  public testForm;
  public testFields = [];
  public fieldCounter = 1;
  public testName;
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private fb: FormBuilder,
    private testService: TestsService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.testId = window.location.href.substring(
      window.location.href.lastIndexOf("/") + 1
    );
    this.testService.getTest(this.testId).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.updateValues(response);
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
    this.testForm = this.fb.group({
      testName: [{ value: "", disabled: true }],
      cost: [{ value: null, disabled: true }]
    });
  }
  saveTest() {
    this.loading.nativeElement.style.display = "block";
    this.testFields.forEach(function(v) {
      delete v.id;
    });
    let data = { fields: this.testFields };
    this.testService.updateTest(this.testId, data).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show("Test updated!!", {
          classname: "bg-success text-light"
        });
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
  }

  updateValues(response) {
    this.testName = response.testName;
    this.testForm.setValue({
      testName: response.testName,
      cost: response.cost
    });
    response.fields.forEach(element => {
      element.id = this.fieldCounter;
      this.fieldCounter++;
    });
    this.testFields = response.fields;
  }
  updateField(ev, fieldId) {
    this.testFields[
      this.testFields.findIndex(ele => {
        return ele.id === fieldId;
      })
    ].value = ev.currentTarget.value;
  }
  addField() {
    this.testFields.push({
      id: this.fieldCounter,
      value: 0,
      refValue: "",
      name: ""
    });
    this.fieldCounter++;
  }
  removeField(field) {
    this.fieldCounter--;
    this.testFields.splice(
      this.testFields.findIndex(ele => {
        return ele.id === field.id;
      }),
      1
    );
  }
}
