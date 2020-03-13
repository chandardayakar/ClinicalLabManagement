import { AuthenticationService } from "./../services/auth-service.service";
import { Component, OnInit } from "@angular/core";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.css"]
})
export class HeaderComponent implements OnInit {
  currentUser;
  constructor(private authenticationService: AuthenticationService) {}

  ngOnInit(): void {
    this.authenticationService.currentUser.subscribe(
      response => {
        this.currentUser = response;
      },
      error => {}
    );
  }
  doLogin() {
    this.authenticationService.login().subscribe(
      response => {
        this.currentUser = response;
      },
      error => {}
    );
  }
  doLogout() {
    this.authenticationService.logout();
  }
}
