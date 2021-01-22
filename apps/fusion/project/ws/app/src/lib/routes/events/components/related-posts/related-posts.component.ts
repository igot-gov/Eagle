import { Component, OnInit, Input } from '@angular/core'
import { NSDiscussData } from '../../../discuss/models/discuss.model'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-related-posts',
  templateUrl: './related-posts.component.html',
  styleUrls: ['./related-posts.component.scss'],
})
export class RelatedPostsComponent implements OnInit {
  @Input()
  relatedDiscussions!: NSDiscussData.IDiscussionData[]
  tid!: number

  constructor(
    private router: Router,
    ) {

  }
  ngOnInit(): void {
  }

  getDiscussion(discuss: NSDiscussData.IDiscussionData) {
    this.router.navigate([`/app/careers/home/${discuss.tid}`])
  }
}
