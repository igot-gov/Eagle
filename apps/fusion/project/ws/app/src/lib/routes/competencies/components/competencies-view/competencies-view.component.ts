import { Component, OnInit, Inject } from '@angular/core'
import { MAT_DIALOG_DATA } from '@angular/material'
import { NSCompetencie } from '../../models/competencies.model'
// import { Router } from '@angular/router'

export interface IDialogData {
  name: string
}
@Component({
  selector: 'app-competence-view',
  templateUrl: './competencies-view.component.html',
  styleUrls: ['./competencies-view.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-right-xs margin-top-xs margin-bottom-s' },
  /* tslint:enable */

})

export class CompetenceViewComponent implements OnInit {

  constructor(@Inject(MAT_DIALOG_DATA) public dData: NSCompetencie.ICompetencie) { }
  ngOnInit() { }
  add() {
    // this.addComp.emit(this.data.id)
  }
}
