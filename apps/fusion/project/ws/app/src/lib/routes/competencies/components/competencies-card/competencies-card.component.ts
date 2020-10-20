import { Component, OnInit, Input } from '@angular/core'
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
  constructor(
    // private router: Router,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }

  getDiscussion() {
    // this.router.navigate([`/app/discuss/home/${this.category.cid}`])
  }
}
