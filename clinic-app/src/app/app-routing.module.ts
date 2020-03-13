import { CreateTestComponent } from "./create-test/create-test.component";
import { EditReportComponent } from "./edit-report/edit-report.component";
import { ReportsComponent } from "./reports/reports.component";
import { CreateReportComponent } from "./create-report/create-report.component";
import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { HomeComponent } from "./home/home.component";

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
  {
    path: "test",
    children: [{ path: "create", component: CreateTestComponent }]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
