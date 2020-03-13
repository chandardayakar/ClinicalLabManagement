import { Component, OnInit, Input } from "@angular/core";
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: "app-tests-list",
  templateUrl: "./tests-list.component.html",
  styleUrls: ["./tests-list.component.css"]
})
export class TestsListComponent implements OnInit {
  @Input() availableTests;
  @Input() selectedTests;

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}
  selectAllClick(event) {
    if (event.target.checked) {
      this.availableTests.forEach(element => {
        if (this.selectedTests.indexOf(element.name) < 0) {
          this.selectedTests.push(element.name);
        }
      });
    } else {
      this.selectedTests.splice(0, this.selectedTests.length);
    }
  }
  selectTest(event, test) {
    if (event.target.checked) {
      if (this.selectedTests.indexOf(test.name) < 0) {
        this.selectedTests.push(test.name);
      }
    } else {
      if (this.selectedTests.indexOf(test.name) >= 0) {
        this.selectedTests.splice(this.selectedTests.indexOf(test.name), 1);
      }
    }
  }
}
