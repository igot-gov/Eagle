import { Component, OnInit, Input } from '@angular/core'
import { NSProfileData } from '../../models/profile-v2.model'
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
  relatedDiscussions!: NSProfileData.IProfile[]

  constructor(private router: Router) {

  }
  ngOnInit(): void {
  }

  getDiscussion(discuss: NSProfileData.IProfile) {
    this.router.navigate([`/app/discuss/home/${discuss}`])
  }
}
