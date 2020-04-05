import { ReportsService } from "./../services/reports.service";
import { Component, OnInit, Input, ViewChild, ElementRef } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { FormBuilder, Validators } from "@angular/forms";
import { TestsService } from "../services/tests.service";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-edit-report",
  templateUrl: "./edit-report.component.html",
  styleUrls: ["./edit-report.component.css"],
})
export class EditReportComponent implements OnInit {
  public reportForm;
  public report;
  public testName;
  public testFields = [];
  public reportId;
  public genders = ["Male", "Female", "Others"];
  public reportStatuses = [
    "SAMPLES_NOT_YET_COLLECTED",
    "SAMPLES_COLLECTED",
    "REPORT_COMPLETE",
  ];
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
      (response) => {
        this.loading.nativeElement.style.display = "none";
        this.updateValues(response);
      },
      (error) => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light",
        });
      }
    );
    this.reportForm = this.fb.group({
      patientName: [{ value: "", disabled: true }],
      age: [null, [Validators.required]],
      gender: [null, [Validators.required]],
      sampleCollectionDate: [null],
      reportingDate: [null],
      mobile: [
        "",
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10),
          Validators.pattern("^[0-9]*$"),
        ],
      ],
      referredBy: [null],
      reportStatus: ["SAMPLES_NOT_YET_COLLECTED"],
    });
  }
  saveReport() {
    this.loading.nativeElement.style.display = "block";
    let data = this.reportForm.getRawValue();
    data.fields = this.testFields;
    this.reportsService.saveReport(this.reportId, data).subscribe(
      (response) => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show("Report updated!!", {
          classname: "bg-success text-light",
        });
      },
      (error) => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light",
        });
      }
    );
  }
  updateValues(response) {
    response.test.fields.sort((a, b) => {
      if (a.fieldId > b.fieldId) return 1;
      if (b.fieldId > a.fieldId) return -1;
      return 0;
    });
    let data = this.reportForm.getRawValue();
    data.patientName = response.patientName;
    data.age = response.age;
    data.gender = response.gender;
    data.mobile = response.mobile;
    if (!!response.sampleCollectionDate)
      data.sampleCollectionDate = response.sampleCollectionDate;
    if (!!response.reportingDate) data.reportingDate = response.reportingDate;
    if (!!response.referredBy) data.referredBy = response.referredBy;
    if (!!response.reportStatus) data.reportStatus = response.reportStatus;
    this.reportForm.setValue(data);
    this.testFields = response.test.fields;
    this.testName = response.testName;
  }
  updateField(ev, fieldId) {
    this.testFields[
      this.testFields.findIndex((ele) => {
        return ele.fieldId === fieldId;
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
    WinPrint.setTimeout(function () {
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
  get sampleCollectionDate() {
    return this.reportForm.get("sampleCollectionDate");
  }
  get reportingDate() {
    return this.reportForm.get("reportingDate");
  }
  get referredBy() {
    return this.reportForm.get("referredBy");
  }
  getformattedDate(dateObject) {
    let formattedDate;
    if (!!dateObject) {
      formattedDate =
        dateObject.day + "-" + dateObject.month + "-" + dateObject.year;
    }
    return formattedDate;
  }
  get reportStatus() {
    return this.reportForm.get("reportStatus");
  }
  changeGender(e) {
    this.reportForm.get("gender").setValue(e.target.value, {
      onlySelf: true,
    });
  }
  changeStatus(e) {
    this.reportForm.get("reportStatus").setValue(e.target.value, {
      onlySelf: true,
    });
  }
}
