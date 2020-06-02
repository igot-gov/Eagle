import { Component, OnInit } from '@angular/core'
import { MatDialogRef } from '@angular/material'
@Component({
  selector: 'ws-app-cohorts-miniprofile',
  templateUrl: './cohorts-miniprofile.component.html',
  styleUrls: ['./cohorts-miniprofile.component.scss'],
})
export class CohortsMiniprofileComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<CohortsMiniprofileComponent>,
  ) { }

  ngOnInit() {
  }
  onNoClick(): void {
    this.dialogRef.close()
  }
}
