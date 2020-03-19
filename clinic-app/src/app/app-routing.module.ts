import { TestsComponent } from "./tests/tests.component";
import { CreateTestComponent } from "./create-test/create-test.component";
import { EditReportComponent } from "./edit-report/edit-report.component";
import { ReportsComponent } from "./reports/reports.component";
import { CreateReportComponent } from "./create-report/create-report.component";
import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { EditTestComponent } from "./edit-test/edit-test.component";

const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "home" },
  { path: "home", component: HomeComponent },
  {
    path: "report",
    children: [
      { path: "create", component: CreateReportComponent },
      {
        path: ":id",
        component: EditReportComponent
      }
    ]
  },
  { path: "reports", component: ReportsComponent },
  { path: "tests", component: TestsComponent },
  {
    path: "test",
    children: [
      { path: "create", component: CreateTestComponent },
      { path: ":id", component: EditTestComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
