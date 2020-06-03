import { Component, OnInit, Inject } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog'

@Component({
  selector: 'ws-app-delete-content-dialog',
  templateUrl: './delete-content-dialog.component.html',
  styleUrls: ['./delete-content-dialog.component.scss'],
})
export class DeleteContentDialogComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<DeleteContentDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  closeDialog(msg: string = '') {
    this.dialogRef.close(msg)
  }

  ngOnInit() {
  }

}
