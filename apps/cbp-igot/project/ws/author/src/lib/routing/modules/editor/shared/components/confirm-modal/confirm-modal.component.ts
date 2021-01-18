import { Component, OnInit } from '@angular/core'
import { MatDialogRef } from '@angular/material'

@Component({
  selector: 'ws-auth-confirm-modal',
  templateUrl: './confirm-modal.component.html',
  styleUrls: ['./confirm-modal.component.scss'],
})
export class ConfirmModalComponent implements OnInit {

  disabled = false
  isDownloadableIos = false
  isDownloadableAndroid = false
  constructor(
    public dialogRef: MatDialogRef<ConfirmModalComponent>,
  ) { }

  ngOnInit() {
  }

  confirmed() {
    this.dialogRef.close({
      result: true,
    })
  }

  rejected() {
    this.dialogRef.close({
      result: false,
    })
  }

}
