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

  selectedUser!: any
  dataSources: any
  finalArray = []
  showTable = true
  score: any
  currentSelection = false
  constructor(
    public dialogRef: MatDialogRef<UserPopupComponent>,

    @Inject(MAT_DIALOG_DATA) public data: IDialogData, ) { }

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
  dataHandler($event: any) {
    this.finalArray = $event
    this.currentSelection = false
  }
  updateSize() {
    this.dialogRef.updateSize('auto', 'auto')
  }
  public getSelectedUserData(date: any): void {
    this.selectedUser = date
  }
}
