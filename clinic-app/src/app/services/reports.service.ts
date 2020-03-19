import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root"
})
export class ReportsService {
  public reports = "/reports";

  constructor(private _http: HttpClient) {}
  getReports() {
    return this._http.get<any>(this.reports);
  }

  getReport(id) {
    return this._http.get<any>(this.reports + "/" + id);
  }

  saveReport(id, data) {
    return this._http.put<any>(this.reports + "/" + id, data);
  }

  createReport(reportForm, selectedTests) {
    let formData = reportForm.getRawValue();
    formData.testNames = selectedTests;
    return this._http.post<any>(this.reports, formData);
  }
}
