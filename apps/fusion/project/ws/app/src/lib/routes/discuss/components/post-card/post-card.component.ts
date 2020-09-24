import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { Router } from '@angular/router'
@Component({
  selector: 'app-dicuss-post-card',
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1 margin-top-l' },
  /* tslint:enable */
})

export class PostCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IPosts
  constructor(
    private router: Router,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }
  upvote(discuss: NSDiscussData.IPosts) {
    // console.log(discuss)
    if (discuss) {

    }

  }
  downvote(discuss: NSDiscussData.IPosts) {
    // console.log(discuss)
    if (discuss) {

    }
  }
  getDiscussion() {
    if (this.discuss && this.discuss.topic && this.discuss.topic) {
      this.router.navigate([`/app/discuss/home/${this.discuss.topic.tid}`])
    }
  }
}
