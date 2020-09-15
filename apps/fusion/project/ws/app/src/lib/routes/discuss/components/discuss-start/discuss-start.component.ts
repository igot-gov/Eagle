import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
export interface DialogData {
  animal: string
  name: string
}
@Component({
  selector: 'app-discuss-start',
  templateUrl: './discuss-start.component.html',
  styleUrls: ['./discuss-start.component.scss'],
})
export class DiscussStartComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<DiscussStartComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {

  }

  ngOnInit(): void {

  }
  onNoClick(): void {
    this.dialogRef.close()
  }

}
