import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { Router } from '@angular/router'
import { DiscussUtilsService } from '../../services/discuss-utils.service'
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
  discuss!: NSDiscussData.IDiscussionData
  constructor(
    private router: Router,
    private discussUtils: DiscussUtilsService,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }
  upvote(discuss: NSDiscussData.IDiscussionData) {
    // console.log(discuss)
    if (discuss) {

    }

  }
  downvote(discuss: NSDiscussData.IDiscussionData) {
    // console.log(discuss)
    if (discuss) {

    }
  }
  getDiscussion() {
    this.router.navigate([`/app/discuss/home/${this.discuss.tid}`])
  }

  public getBgColor(tagTitle: any) {
    const bgColor = this.discussUtils.stringToColor(tagTitle.toLowerCase())
    const color = this.discussUtils.getContrast(bgColor)
    return { color, 'background-color': bgColor }
  }
}
