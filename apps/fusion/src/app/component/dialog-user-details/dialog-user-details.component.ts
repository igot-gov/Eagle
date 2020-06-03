import { Component, OnInit, OnDestroy } from '@angular/core'
import { FormGroup, FormControl, Validators } from '@angular/forms'
import { MatDialogRef } from '@angular/material'

@Component({
  selector: 'ws-dialog-user-details',
  templateUrl: './dialog-user-details.component.html',
  styleUrls: ['./dialog-user-details.component.scss'],
})
export class DialogUserDetailsComponent implements OnInit, OnDestroy {
  showLoader = false
  showMessage = false
  openEpochCustomUrl =
    'https://www.infosys.com/investors/news-events/annual-general-meeting/2019.html'
  logUserDetails!: FormGroup

  constructor(public dialogRef: MatDialogRef<DialogUserDetailsComponent>) {}

  ngOnInit() {
    localStorage.setItem('uuid', this.createUUID())
    this.logUserDetails = new FormGroup({
      firstname: new FormControl(null, [Validators.required]),
      lastname: new FormControl(null, [Validators.required]),
      emailid: new FormControl(null, [Validators.required, Validators.email]),
      companyname: new FormControl(null, [Validators.required]),
    })
  }

  submitUserDetails() {
    this.showLoader = true
    this.showMessage = true
    this.showLoader = false
  }

  createUUID() {
    let dt = new Date().getTime()
    const uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
      // tslint:disable-next-line: no-bitwise
      const r = (dt + Math.random() * 16) % 16 | 0
      dt = Math.floor(dt / 16)
      // tslint:disable-next-line: no-bitwise
      return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16)
    })
    return uuid
  }

  ngOnDestroy() {
    localStorage.removeItem('uuid')
  }
}
