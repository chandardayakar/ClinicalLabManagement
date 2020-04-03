import { Injectable } from "@angular/core";

@Injectable()
export class KeyboardUtil {
  static buttonClick(event) {
    if (event.type === "click") {
      return true;
    } else if (event.type === "keydown") {
      var code = event.charCode || event.keyCode;
      if (code === 32 || code === 13) {
        return true;
      }
    } else {
      return false;
    }
  }
}
