import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
// tslint:disable-next-line:import-spacing
// import  *  as  contentQuality  from  './content-quality.json'
export interface IDialogData {
  animal: string
  name: string
  data: any
}
@Component({
  selector: 'ws-auth-content-quality-popup',
  templateUrl: './user-popup.html',
  styleUrls: ['./user-popup.scss'],
})
export class UserPopupComponent implements OnInit {

  selectedUser: any = []
  dataSources: any
  finalArray = []
  tabledata: any = []
  dataTable: any = []
  score: any
  currentSelection = false
  constructor(
    public dialogRef: MatDialogRef<UserPopupComponent>,

    @Inject(MAT_DIALOG_DATA) public data: IDialogData) { }

  ngOnInit() {

  }

  onNoClick(): void {
    this.dialogRef.close()
  }
  markAsComplete() {
    if (!this.currentSelection) {
      this.dialogRef.close({ event: 'close', data: this.selectedUser })
      this.currentSelection = true
      this.dialogRef = this.selectedUser
    }

  }
  selectedUserFrom(user: any) {
    if (this.selectedUser.lenght === 0) {
      this.selectedUser.push(user.row)
    } else {
      this.selectedUser.splice(0, this.selectedUser.length)
      this.selectedUser.push(user.row)
    }
  }
}
