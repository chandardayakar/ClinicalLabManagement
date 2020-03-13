import { ReportsService } from "./../services/reports.service";
import { Component, OnInit, Input } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { FormBuilder, Validators } from "@angular/forms";
import { TestsService } from "../services/tests.service";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-edit-report",
  templateUrl: "./edit-report.component.html",
  styleUrls: ["./edit-report.component.css"]
})
export class EditReportComponent implements OnInit {
  public reportForm;
  public report;
  public testName;
  public testFileds = [];
  public reportId;
  public genders = ["male", "female"];
  constructor(
    private fb: FormBuilder,
    private reportsService: ReportsService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.reportId = window.location.href.substring(
      window.location.href.lastIndexOf("/") + 1
    );
    this.reportsService.getReport(this.reportId).subscribe(
      response => {
        this.updateValues(response);
      },
      error => {
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
    this.reportForm = this.fb.group({
      patientName: [{ value: "", disabled: true }],
      age: [{ value: null, disabled: true }],
      gender: [{ value: null, disabled: true }],
      mobile: [{ value: null, disabled: true }]
    });
  }
  saveReport() {
    this.testFileds.forEach(function(v) {
      delete v.id;
    });
    let data = { fields: this.testFileds };
    this.reportsService.saveReport(this.reportId, data).subscribe(
      response => {
        this.toastService.show("Report updated!!", {
          classname: "bg-success text-light"
        });
      },
      error => {
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  updateValues(response) {
    this.reportForm.setValue({
      patientName: response.patientName,
      age: response.age,
      gender: response.gender,
      mobile: response.mobile
    });
    let fieldCounter = 1;
    response.test.fields.forEach(element => {
      element.id = fieldCounter;
      fieldCounter++;
    });
    this.testFileds = response.test.fields;
    this.testName = response.testName;
  }
  updateField(ev, fieldId) {
    this.testFileds[
      this.testFileds.findIndex(ele => {
        return ele.id === fieldId;
      })
    ].value = ev.currentTarget.value;
  }
}
