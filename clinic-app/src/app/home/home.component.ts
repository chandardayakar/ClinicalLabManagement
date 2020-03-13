import { SyncService } from "./../services/sync.service";
import { Component, OnInit } from "@angular/core";
import { ToastService } from "./../shared/toast-service";

@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.css"]
})
export class HomeComponent implements OnInit {
  public status = "fetching...";
  constructor(
    private _syncService: SyncService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.getSyncStatus();
  }
  getSyncStatus() {
    this._syncService.GetSyncStatus().subscribe(
      response => {
        this.status = response.result;
      },
      error => {
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  startSync() {
    this._syncService.StartSync().subscribe(
      response => {
        this.getSyncStatus();
      },
      error => {
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  stopSync() {
    this._syncService.StopSync().subscribe(
      response => {
        this.getSyncStatus();
      },
      error => {
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
}
