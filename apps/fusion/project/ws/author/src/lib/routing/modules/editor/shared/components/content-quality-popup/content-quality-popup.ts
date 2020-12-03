import { Component, OnInit, Inject} from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'
export interface IDialogData {
  animal: string
  name: string
  data: any
}

@Component({
  selector: 'ws-auth-content-quality',
  templateUrl: './content-quality-popup.html',
  styleUrls: ['./content-quality-popup.scss'],
})
export class ContentQualityCheckPopupComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ContentQualityCheckPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public data: IDialogData,
  ) {
  }

  ngOnInit(): void {

  }
  onNoClick(): void {
    this.dialogRef.close()
  }
}
