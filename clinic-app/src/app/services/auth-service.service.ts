import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable, from } from "rxjs";
import { map } from "rxjs/operators";
import { AuthService, SocialUser } from "angularx-social-login";
import { GoogleLoginProvider } from "angularx-social-login";

@Injectable({ providedIn: "root" })
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<SocialUser>;
  public currentUser: Observable<SocialUser>;

  constructor(private http: HttpClient, private authService: AuthService) {
    this.currentUserSubject = new BehaviorSubject<SocialUser>(
      JSON.parse(localStorage.getItem("currentUser"))
    );
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): SocialUser {
    return this.currentUserSubject.value;
  }

  login() {
    return from(this.authService.signIn(GoogleLoginProvider.PROVIDER_ID)).pipe(
      map(response => {
        localStorage.setItem("currentUser", JSON.stringify(response));
        this.currentUserSubject.next(response);
        return response;
      })
    );
  }
  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem("currentUser");
    this.currentUserSubject.next(null);
  }
}
