import { Component, Inject, OnInit } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material'

@Component({
  selector: 'ws-app-view-events',
  templateUrl: './view-events.component.html',
  styleUrls: ['./view-events.component.scss'],
})
export class ViewEventsComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ViewEventsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit() {
  }

}
