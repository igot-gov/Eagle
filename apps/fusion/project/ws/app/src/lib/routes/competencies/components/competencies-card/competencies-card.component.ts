import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core'
import { NSCompetencie } from '../../models/competencies.model'
// import { Router } from '@angular/router'
@Component({
  selector: 'app-competence-card',
  templateUrl: './competencies-card.component.html',
  styleUrls: ['./competencies-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-xs' },
  /* tslint:enable */

})

export class CompetenceCardComponent implements OnInit {
  @Input()
  data!: NSCompetencie.ICompetencie
  @Input()
  isSelected!: boolean

  @Output() setSelected = new EventEmitter<string>()
  @Output() addComp = new EventEmitter<string>()
  constructor() { }
  ngOnInit() { }
  setSelectedCompetency() {
    this.isSelected = true
    this.setSelected.emit(this.data.id)
  }
  add() {
    this.addComp.emit(this.data.id)
  }
}
