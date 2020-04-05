import { getTestBed } from "@angular/core/testing";
import { SearchService } from "./../services/search.service";
import { Router, ActivatedRoute } from "@angular/router";
import { ReportsService } from "./../services/reports.service";
import { Component, OnInit, ViewChild, ElementRef } from "@angular/core";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-reports",
  templateUrl: "./reports.component.html",
  styleUrls: ["./reports.component.css"],
})
export class ReportsComponent implements OnInit {
  public reports = [];
  public displayReports = [];
  public delayTimer;
  public reportStatuses = [
    "SAMPLES_NOT_YET_COLLECTED",
    "SAMPLES_COLLECTED",
    "REPORT_COMPLETE",
  ];
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private _reportsService: ReportsService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService,
    private searchService: SearchService
  ) {}

  ngOnInit(): void {
    this.getReports();
  }
  getReports() {
    this._reportsService.getReports().subscribe(
      (response) => {
        this.loading.nativeElement.style.display = "none";
        this.reports = response.reports;
        this.displayReports = response.reports;
      },
      (error) => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light",
        });
      }
    );
  }
  getReport(id) {
    this.router.navigate(["/report", id]);
  }
  filterReports(event) {
    clearTimeout(this.delayTimer);
    let self = this;
    let filterValue = document.getElementById(
      "filter_value"
    ) as HTMLInputElement;
    this.delayTimer = setTimeout(function () {
      if (filterValue.value.length > 0) {
        let filterParam = document.getElementById(
          "filter_param"
        ) as HTMLSelectElement;
        let test = filterParam.options[filterParam.selectedIndex].value;
        self.loading.nativeElement.style.display = "block";
        self.searchService
          .getSearchResults({ filter: test, value: filterValue.value })
          .subscribe(
            (response) => {
              self.loading.nativeElement.style.display = "none";
              self.reports = response.reports;
              self.displayReports = response.reports;
            },
            (error) => {
              self.loading.nativeElement.style.display = "none";
              self.toastService.show(error, {
                classname: "bg-danger text-light",
              });
            }
          );
      } else {
        if (event.type !== "change") self.getReports();
      }
    }, 1000);
  }
}
