import { Component, OnInit, ViewChild, ElementRef } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";
import { TestsService } from "../services/tests.service";
import { ToastService } from "../shared/toast-service";
import { KeyboardUtil } from "../shared/keyboard.accesibilty.service";

@Component({
  selector: "app-edit-test",
  templateUrl: "./edit-test.component.html",
  styleUrls: ["./edit-test.component.css"]
})
export class EditTestComponent implements OnInit {
  public testId;
  public testForm;
  public testFields = [];
  public fieldCounter = 1;
  public testName;
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private fb: FormBuilder,
    private testService: TestsService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.testId = window.location.href.substring(
      window.location.href.lastIndexOf("/") + 1
    );
    this.testService.getTest(this.testId).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.updateValues(response);
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
    this.testForm = this.fb.group({
      testName: [{ value: "", disabled: true }],
      cost: [null, [Validators.pattern("^[0-9]*$")]]
    });
  }
  saveTest() {
    this.loading.nativeElement.style.display = "block";
    let data = {
      fields: this.testFields,
      cost: this.testForm.get("cost").value
    };
    this.testService.updateTest(this.testId, data).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show("Test updated!!", {
          classname: "bg-success text-light"
        });
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
  }

  updateValues(response) {
    this.testName = response.testName;
    this.testForm.setValue({
      testName: response.testName,
      cost: response.cost
    });
    this.fieldCounter = response.fields.length + 1;
    this.testFields = response.fields;
  }

  updateField(ev, fieldId) {
    if (ev.currentTarget.classList.contains("field-name")) {
      this.testFields[
        this.testFields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].name = ev.currentTarget.value;
    } else if (ev.currentTarget.classList.contains("field-refValue")) {
      this.testFields[
        this.testFields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].refValue = ev.currentTarget.value;
    } else if (ev.currentTarget.classList.contains("field-unit")) {
      this.testFields[
        this.testFields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].unit = ev.currentTarget.value;
    }
  }

  addField(event) {
    if (KeyboardUtil.buttonClick(event)) {
      this.testFields.push({
        fieldId: this.fieldCounter,
        value: 0,
        refValue: "",
        name: ""
      });
      this.fieldCounter++;
    }
  }
  removeField(event, field) {
    if (KeyboardUtil.buttonClick(event)) {
      let i;
      this.fieldCounter--;
      this.testFields.splice(
        this.testFields.findIndex(ele => {
          i = field.fieldId;
          return ele.fieldId === field.fieldId;
        }),
        1
      );
      this.testFields.forEach(function(v) {
        if (v.fieldId > i) v.fieldId = v.fieldId - 1;
      });
    }
  }
}
