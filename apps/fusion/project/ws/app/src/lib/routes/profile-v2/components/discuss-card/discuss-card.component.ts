import { Component, OnInit, Input } from '@angular/core'
import { } from '../../models/profile-v2.model'
// import { Router } from '@angular/router'
// import { ProfileV2Service } from '../../services/profile-v2.servive'
@Component({
  selector: 'app-dicuss-card',
  templateUrl: './discuss-card.component.html',
  styleUrls: ['./discuss-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})

export class DiscussCardComponent implements OnInit {
  @Input()
  user!: any
  constructor(
    // private router: Router,
    // private discussUtils: DiscussUtilsService,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }

  getDiscussion() {
    // this.router.navigate([`/app/discuss/home/${this.discuss.tid}`])
  }

  public getBgColor() {
    // const bgColor = this.discussUtils.stringToColor(tagTitle.toLowerCase())
    // const color = this.discussUtils.getContrast(bgColor)
    // return { color, 'background-color': bgColor }
  }
}
