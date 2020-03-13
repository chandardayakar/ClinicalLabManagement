import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root"
})
export class TestsService {
  testsUrl = "/tests";
  constructor(private _http: HttpClient) {}
  getTests() {
    return this._http.get<any>(this.testsUrl);
  }
  saveTest(data) {
    return this._http.post<any>(this.testsUrl, data);
  }
}
