import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../models/discuss.model'
/* tslint:disable */
import _ from 'lodash'
import { Router } from '@angular/router'
/* tslint:enable */
@Component({
  selector: 'app-discuss-related-discussion',
  templateUrl: './related-discussion.component.html',
  styleUrls: ['./related-discussion.component.scss'],
  // tslint:disable-next-line
  host: { class: 'margin-left-l' },
})
export class RelatedDiscussionComponent implements OnInit {
  @Input()
  relatedDiscussions!: NSDiscussData.IDiscussionData[]

  constructor(private router: Router) {

  }
  ngOnInit(): void {
  }

  getDiscussion(discuss: NSDiscussData.IDiscussionData) {
    this.router.navigate([`/app/discuss/home/${discuss.tid}`])
  }
}
