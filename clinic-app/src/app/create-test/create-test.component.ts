import { KeyboardUtil } from "./../shared/keyboard.accesibilty.service";
import { ToastService } from "./../shared/toast-service";
import { TestsService } from "./../services/tests.service";
import { Component, OnInit, ViewChild, ElementRef } from "@angular/core";
import { FormBuilder, Validators } from "@angular/forms";

@Component({
  selector: "app-create-test",
  templateUrl: "./create-test.component.html",
  styleUrls: ["./create-test.component.css"]
})
export class CreateTestComponent implements OnInit {
  public fields = [];
  public testForm;
  public fieldCounter = 1;
  @ViewChild("loading", { read: ElementRef }) loading: ElementRef;
  constructor(
    private fb: FormBuilder,
    private testsService: TestsService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.testForm = this.fb.group({
      testName: ["", [Validators.required, Validators.minLength(3)]],
      cost: [null, [Validators.required, Validators.pattern("^[0-9]*$")]]
    });
  }
  saveTest() {
    this.loading.nativeElement.style.display = "block";
    this.fieldCounter = 1;
    let data = this.testForm.getRawValue();
    data.fields = this.fields;
    this.testsService.saveTest(data).subscribe(
      response => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show("Test saved!!", {
          classname: "bg-success text-light"
        });
        this.testForm.reset();
        this.fields.splice(0, this.fields.length);
      },
      error => {
        this.loading.nativeElement.style.display = "none";
        this.toastService.show(error, {
          classname: "bg-danger text-light"
        });
      }
    );
  }
  addField(event) {
    if (KeyboardUtil.buttonClick(event)) {
      this.fields.push({
        fieldId: this.fieldCounter,
        value: 0,
        refValue: "",
        name: ""
      });
      this.fieldCounter++;
    }
  }
  updateField(ev, fieldId) {
    if (ev.currentTarget.classList.contains("field-name")) {
      this.fields[
        this.fields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].name = ev.currentTarget.value;
    } else if (ev.currentTarget.classList.contains("field-refValue")) {
      this.fields[
        this.fields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].refValue = ev.currentTarget.value;
    } else if (ev.currentTarget.classList.contains("field-unit")) {
      this.fields[
        this.fields.findIndex(ele => {
          return ele.fieldId === fieldId;
        })
      ].unit = ev.currentTarget.value;
    }
  }
  removeField(event, field) {
    if (KeyboardUtil.buttonClick(event)) {
      let i;
      this.fieldCounter--;
      this.fields.splice(
        this.fields.findIndex(ele => {
          i = field.fieldId;
          return ele.fieldId === field.fieldId;
        }),
        1
      );
      this.fields.forEach(function(v) {
        if (v.fieldId > i) v.fieldId = v.fieldId - 1;
      });
    }
  }
  get testName() {
    return this.testForm.get("testName");
  }
  get cost() {
    return this.testForm.get("cost");
  }
}
