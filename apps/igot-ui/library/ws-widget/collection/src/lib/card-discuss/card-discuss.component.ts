import { Component, Input, OnInit } from '@angular/core'
import { NSDiscussData } from './discuss.model'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-widget-card-discuss',
  templateUrl: './card-discuss.component.html',
  styleUrls: ['./card-discuss.component.scss'],

})

export class CardDiscussComponent implements OnInit {
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

}
