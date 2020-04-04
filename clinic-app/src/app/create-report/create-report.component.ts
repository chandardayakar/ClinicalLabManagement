import { KeyboardUtil } from "./../shared/keyboard.accesibilty.service";
import { ReportsService } from "./../services/reports.service";
import { ToastService } from "./../shared/toast-service";
import { TestsService } from "./../services/tests.service";
import { TestsListComponent } from "./../tests-list/tests-list.component";
import { Component, OnInit, ViewChild, ElementRef } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: "app-create-report",
  templateUrl: "./create-report.component.html",
  styleUrls: ["./create-report.component.css"],
})
export class CreateReportComponent implements OnInit {
  public reportForm;
  public selectedTests = [];
  public availableTests = [];
  public printTests = [];
  public genders = ["Male", "Female", "Others"];
  public today = new Date().toDateString();
  public reportIds = [];
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private fb: FormBuilder,
    private modalService: NgbModal,
    private _testService: TestsService,
    private toastService: ToastService,
    private reportService: ReportsService
  ) {}

  ngOnInit(): void {
    this.reportForm = this.fb.group({
      patientName: ["", [Validators.required, Validators.minLength(3)]],
      age: [null, [Validators.required]],
      gender: [null, [Validators.required]],
      sampleCollectionDate: [null, [Validators.required]],
      reportingDate: [null, [Validators.required]],
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
    });
    this._testService.getTests().subscribe(
      (response) => {
        this.loading.nativeElement.style.display = "none";
        this.availableTests = response.tests;
      },
      (error) => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light",
        });
      }
    );
  }
  changeGender(e) {
    this.reportForm.get("gender").setValue(e.target.value, {
      onlySelf: true,
    });
  }
  removeTest(event, test) {
    if (KeyboardUtil.buttonClick(event)) {
      this.selectedTests.splice(this.selectedTests.indexOf(test.name), 1);
    }
  }
  addTest(event) {
    if (KeyboardUtil.buttonClick(event)) {
      const modalRef = this.modalService.open(TestsListComponent, {
        scrollable: true,
        centered: true,
        size: "lg",
      });
      modalRef.componentInstance.selectedTests = this.selectedTests;
      modalRef.componentInstance.availableTests = this.availableTests;
    }
  }
  getSelectedTests() {
    return this.availableTests.filter((element) => {
      return this.selectedTests.indexOf(element.name) >= 0;
    });
  }
  saveReport() {
    if (confirm("Are you sure to create a report?")) {
      this.loading.nativeElement.style.display = "block";
      this.reportService
        .createReport(this.reportForm.getRawValue(), this.selectedTests)
        .subscribe(
          (response) => {
            this.loading.nativeElement.style.display = "none";
            this.toastService.show("Report generated!!", {
              classname: "bg-success text-light",
            });
            var prtContent = document.getElementById("print");
            var self = this;
            var WinPrint = window.open(
              "",
              "",
              "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0"
            );
            WinPrint.document.write(prtContent.innerHTML);
            this.selectedTests.map((value, index) => {
              WinPrint.document.getElementById(
                value
              ).innerHTML = response.reportIds[index].toString();
            });
            WinPrint.document.close();
            WinPrint.setTimeout(function () {
              WinPrint.focus();
              WinPrint.print();
              WinPrint.close();
              self.reportForm.reset();
              self.selectedTests.splice(0, self.selectedTests.length);
            }, 1000);
          },
          (error) => {
            this.loading.nativeElement.style.display = "none";
            this.toastService.show(error, {
              classname: "bg-danger text-light",
            });
          }
        );
    }
  }
  getTotalAmount() {
    let total = 0;
    this.availableTests.forEach((test) => {
      if (this.selectedTests.includes(test.name)) {
        total += parseInt(test.price);
      }
    });
    return !!total ? total : 0;
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
}
