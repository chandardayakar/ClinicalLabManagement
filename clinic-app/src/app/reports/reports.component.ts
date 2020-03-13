import { Router, ActivatedRoute } from "@angular/router";
import { ReportsService } from "./../services/reports.service";
import { Component, OnInit } from "@angular/core";
import { ToastService } from "../shared/toast-service";

@Component({
  selector: "app-reports",
  templateUrl: "./reports.component.html",
  styleUrls: ["./reports.component.css"]
})
export class ReportsComponent implements OnInit {
  public reports = [];
  public displayReports = [];
  constructor(
    private _reportsService: ReportsService,
    private router: Router,
    private route: ActivatedRoute,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this._reportsService.getReports().subscribe(
      response => {
        this.reports = response.reports;
        this.displayReports = response.reports;
      },
      error => {
        this.toastService.show(error.statusText, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  getReport(id) {
    this.router.navigate(["/report", id]);
  }
  filterReports(event) {
    if (event.target.value.length > 0)
      this.displayReports = this.reports.filter(value => {
        return value.patientName.includes(event.target.value);
      });
    else {
      this.displayReports = [...this.reports];
    }
  }
}
