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

  dataSources: any
  finalArray = []
  showTable = true
  score: any
  currentSelection = false
  first = true
  constructor(
    public dialogRef: MatDialogRef<UserPopupComponent>,

    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
  ) {
  }

  ngOnInit() {
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
  markAsComplete() {

    if (!this.currentSelection && this.first) {
      this.currentSelection = true
      this.first = false
    }

  }
  dataHandler($event: any) {
    this.finalArray = $event
    this.currentSelection = false
    this.first = false
  }
  updateSize() {
    this.dialogRef.updateSize('auto', 'auto')
  }
}
