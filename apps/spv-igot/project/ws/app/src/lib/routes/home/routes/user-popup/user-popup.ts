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
    const userId = user.row.userId
    let index
    let isUserFound = false
    if (this.selectedUser.length > 0) {
      this.selectedUser.forEach((element: any) => {
        if (element.userId === userId) {
          index = this.selectedUser.indexOf(element)
          isUserFound = true
        }
      })
      if (isUserFound) {
        this.selectedUser.splice(index, 1)
      } else {
        this.selectedUser.push(user.row)
      }
    } else {
      this.selectedUser.push(user.row)
    }
  }
}
