import { Component, OnInit } from '@angular/core'
import { MatDialogRef } from '@angular/material/dialog'

export interface IParticipantElement {
  firstname: string,
  lastname: string,
  email: number
}

@Component({
  selector: 'ws-app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.scss'],
})
export class SuccessComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SuccessComponent>) { }

  ngOnInit() {

  }

  confirm() {
    this.dialogRef.close()
  }

}
