import { ReportsService } from "./../services/reports.service";
import { ToastService } from "./../shared/toast-service";
import { TestsService } from "./../services/tests.service";
import { TestsListComponent } from "./../tests-list/tests-list.component";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: "app-create-report",
  templateUrl: "./create-report.component.html",
  styleUrls: ["./create-report.component.css"]
})
export class CreateReportComponent implements OnInit {
  public reportForm;
  public selectedTests = [];
  public availableTests = [];
  public genders = ["male", "female"];
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
      mobile: [
        "",
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10)
        ]
      ]
    });
    this._testService.getTests().subscribe(
      response => {
        this.availableTests = response.tests;
      },
      error => {
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  changeGender(e) {
    this.reportForm.get("gender").setValue(e.target.value, {
      onlySelf: true
    });
  }
  removeTest(test) {
    this.selectedTests.splice(this.selectedTests.indexOf(test.name), 1);
  }
  addTest() {
    const modalRef = this.modalService.open(TestsListComponent, {
      scrollable: true,
      centered: true,
      size: "lg"
    });
    modalRef.componentInstance.selectedTests = this.selectedTests;
    modalRef.componentInstance.availableTests = this.availableTests;
  }
  getSelectedTests() {
    return this.availableTests.filter(element => {
      return this.selectedTests.indexOf(element.name) >= 0;
    });
  }
  saveReport() {
    if (confirm("Are you sure to create a report?")) {
      this.reportService
        .createReport(this.reportForm, this.selectedTests)
        .subscribe(
          response => {
            this.toastService.show("Report generated!!", {
              classname: "bg-success text-light"
            });
            this.reportForm.reset();
            this.selectedTests.splice(0, this.selectedTests.length);
          },
          error => {
            this.toastService.show(error.statusText, {
              classname: "bg-danger text-light"
            });
          }
        );
    }
  }
  getTotalAmount() {
    let total = 0;
    this.availableTests.forEach(test => {
      if (this.selectedTests.includes(test.name)) {
        total += parseInt(test.price);
      }
    });
    return !!total ? total : 0;
  }
}
