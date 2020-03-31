import { ReportsService } from "./../services/reports.service";
import { Component, OnInit, Input, ViewChild, ElementRef } from "@angular/core";
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
  public testFields = [];
  public reportId;
  public genders = ["male", "female"];
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
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
        this.loading.nativeElement.style.display = "none";
        this.updateValues(response);
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
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
    this.loading.nativeElement.style.display = "block";
    this.testFields.forEach(function(v) {
      delete v.id;
    });
    let data = { fields: this.testFields };
    this.reportsService.saveReport(this.reportId, data).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show("Report updated!!", {
          classname: "bg-success text-light"
        });
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
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
    this.testFields = response.test.fields;
    this.testName = response.testName;
  }
  updateField(ev, fieldId) {
    this.testFields[
      this.testFields.findIndex(ele => {
        return ele.id === fieldId;
      })
    ].value = ev.currentTarget.value;
  }
  printReport() {
    var prtContent = document.getElementById("print");
    var WinPrint = window.open(
      "",
      "",
      "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
    );
    WinPrint.document.write(prtContent.innerHTML);
    WinPrint.document.close();
    WinPrint.setTimeout(function() {
      WinPrint.focus();
      WinPrint.print();
      WinPrint.close();
    }, 1000);
  }
  get patientName() {
    return this.reportForm.get("patientName");
  }
  get age() {
    return this.reportForm.get("age");
  }
  get gender() {
    return this.reportForm.get("gender");
  }
  get mobile() {
    return this.reportForm.get("mobile");
  }
}
