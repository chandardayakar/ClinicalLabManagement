import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { HeaderComponent } from "./header/header.component";
import { HomeComponent } from "./home/home.component";
import { FooterComponent } from "./footer/footer.component";
import { CreateReportComponent } from "./create-report/create-report.component";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { TestsListComponent } from "./tests-list/tests-list.component";
import { ReportsComponent } from "./reports/reports.component";
import { ToastsContainer } from "./shared/toasts-container.component";
import { EditReportComponent } from "./edit-report/edit-report.component";
import {
  SocialLoginModule,
  AuthServiceConfig,
  LoginOpt
} from "angularx-social-login";
import { GoogleLoginProvider } from "angularx-social-login";
import { ErrorInterceptor } from "./interceptors/error.interceptor";
import { JwtInterceptor } from "./interceptors/jwt.interceptors";
import { CreateTestComponent } from "./create-test/create-test.component";
import { TestsComponent } from "./tests/tests.component";
import { EditTestComponent } from "./edit-test/edit-test.component";

const googleLoginOptions: LoginOpt = {
  scope: "https://www.googleapis.com/auth/drive"
};

let config = new AuthServiceConfig([
  {
    id: GoogleLoginProvider.PROVIDER_ID,
    provider: new GoogleLoginProvider(
      "838886148448-v8mumj7hida3kn4tdr945o8gbhriu2kd.apps.googleusercontent.com",
      googleLoginOptions
    )
  }
]);

export function provideConfig() {
  return config;
}

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    FooterComponent,
    CreateReportComponent,
    TestsListComponent,
    ReportsComponent,
    ToastsContainer,
    EditReportComponent,
    CreateTestComponent,
    TestsComponent,
    EditTestComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    SocialLoginModule
  ],
  providers: [
    {
      provide: AuthServiceConfig,
      useFactory: provideConfig
    },
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
