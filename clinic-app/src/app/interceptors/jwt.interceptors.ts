import { Injectable } from "@angular/core";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders
} from "@angular/common/http";
import { Observable } from "rxjs";

import { AuthenticationService } from "./../services/auth-service.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  public baseUrl = "http://localhost:8888/Server/rest";
  constructor(private authenticationService: AuthenticationService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // add authorization header with jwt token if available
    let currentUser = this.authenticationService.currentUserValue;
    if (currentUser && currentUser.authToken) {
      const headerSettings: { [name: string]: string | string[] } = {};
      for (const key of request.headers.keys()) {
        headerSettings[key] = request.headers.getAll(key);
      }
      headerSettings["g_access_token"] = currentUser.authToken;
      headerSettings["Content-Type"] = "application/json";
      const newHeader = new HttpHeaders(headerSettings);
      request = request.clone({
        headers: newHeader
      });
    }

    request = request.clone({ url: `${this.baseUrl}${request.url}` });
    return next.handle(request);
  }
}
