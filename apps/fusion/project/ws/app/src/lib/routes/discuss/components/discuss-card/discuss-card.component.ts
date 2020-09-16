import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
import { Router } from '@angular/router'
@Component({
  selector: 'app-dicuss-card',
  templateUrl: './discuss-card.component.html',
  styleUrls: ['./discuss-card.component.scss'],
  host: { 'class': 'flex flex-1 margin-top-l' }
})

export class DiscussCardComponent implements OnInit {
  @Input()
  discuss!: NSDiscussData.IDiscussionData
  constructor(
    private router: Router,
    // private snackBar: MatSnackBar,
    // private discussionSvc: DiscussService,
    // private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() { }
  upvote(discuss: NSDiscussData.IDiscussionData) {
    console.log(discuss)

  }
  downvote(discuss: NSDiscussData.IDiscussionData) {
    console.log(discuss)
  }
  getDiscussion() {
    this.router.navigate([`/app/discuss/home/${this.discuss.tid}`])
  }
}
