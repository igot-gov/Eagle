import { Component, OnInit } from '@angular/core'
// import { NSCompetenciesData } from '../../models/competencies.model'

@Component({
  selector: 'app-competence-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuComponent implements OnInit {
  // @Input() tags!: NSCompetenciesData.ITag[]

  ngOnInit(): void {

  }

}
