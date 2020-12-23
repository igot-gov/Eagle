import { Component, OnInit, Inject, Input } from '@angular/core'
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material'
import { NSCompetencie } from '../../../../../../../interface/competencies.model'
// import { Router } from '@angular/router'

export interface IDialogData {
  name: string
}
@Component({
  selector: 'ws-auth-competence-view',
  templateUrl: './competencies-view.component.html',
  styleUrls: ['./competencies-view.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-right-xs margin-top-xs margin-bottom-s' },
  /* tslint:enable */
})

export class CompetenceViewComponent implements OnInit {
  @Input() isUpdate = false
  constructor(
    public dialogRef: MatDialogRef<CompetenceViewComponent>,
    @Inject(MAT_DIALOG_DATA) public dData: NSCompetencie.ICompetencie
  ) { }
  ngOnInit() { }
  add() {
    this.dialogRef.close({
      id: this.dData.id,
      action: 'ADD',
    })
  }
  remove() {
    this.dialogRef.close({
      id: this.dData.id,
      action: 'DELETE',
    })
  }
}
