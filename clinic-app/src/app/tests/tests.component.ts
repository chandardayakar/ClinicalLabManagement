import { Component, OnInit, ElementRef, ViewChild } from "@angular/core";
import { TestsService } from "../services/tests.service";
import { Router } from "@angular/router";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-tests",
  templateUrl: "./tests.component.html",
  styleUrls: ["./tests.component.css"]
})
export class TestsComponent implements OnInit {
  public displayTests = [];
  public tests = [];
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private router: Router,
    private _testService: TestsService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this._testService.getTests().subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.tests = response.tests;
        this.displayTests = response.tests;
      },
      error => {
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  filterTests(event) {
    if (event.target.value.length > 0)
      this.displayTests = this.tests.filter(value => {
        return value.name.includes(event.target.value);
      });
    else {
      this.displayTests = [...this.tests];
    }
  }
  getTest(id) {
    this.router.navigate(["/test", id]);
  }
}
