import { Component, OnInit, OnDestroy } from '@angular/core'

@Component({
  selector: 'ws-auth-confirm',
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.scss'],

})
export class ConfirmationComponent implements OnInit, OnDestroy {
  isChecked = false
  ngOnInit(): void {

  }
  ngOnDestroy(): void {

  }

  setAll(checked: boolean) {
    if (checked) {
      this.isChecked = true
    } else {
      this.isChecked = false
    }
  }

}
