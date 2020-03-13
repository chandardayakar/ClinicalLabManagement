import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";

@Injectable({
  providedIn: "root"
})
export class SyncService {
  url = "/Sync/status";
  start = "/Sync/startsync";
  stop = "/Sync/stopsync";
  constructor(private _http: HttpClient) {}
  GetSyncStatus() {
    return this._http.get<any>(this.url);
  }
  StartSync() {
    return this._http.post<any>(this.start, {});
  }
  StopSync() {
    return this._http.post<any>(this.stop, {});
  }
}
