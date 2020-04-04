import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class SearchService {
  public search = "/reports?search=";

  constructor(private _http: HttpClient) {}

  getSearchResults(param) {
    return this._http.get<any>(
      this.search + encodeURI(param.filter + " = " + param.value)
    );
  }
}
