import { Component, OnInit, Input, HostBinding } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { DiscussService } from '@ws/app/src/lib/routes/discuss/services/discuss.service'

/* tslint:disable */
import _ from 'lodash'
import { NSDiscuss } from './discuss.model'
import { Router } from '@angular/router'
/* tslint:enable */
@Component({
  selector: 'ws-widget-home-discuss-component',
  templateUrl: './card-home-discuss.component.html',
  styleUrls: ['./card-home-discuss.component.scss'],
  providers: [DiscussService],
})
export class CardHomeDiscussComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  discuss!: NSDiscuss.IDiscuss
  @HostBinding('id')
  public id = 'h-d-component'
  constructor(private discussService: DiscussService, private router: Router) {
    super()
  }

  discussionList = Array()
  // starColor: StarRatingColor = StarRatingColor.accent
  // starColorP: StarRatingColor = StarRatingColor.primary
  // starColorW: StarRatingColor = StarRatingColor.warn
  ngOnInit(): void {
    if (this.widgetData && this.widgetData.content) {
      this.discuss = ([this.widgetData.content] || []).map((d: any) => {
        return {
          tid: d.tid,
          title: d.title,
          description: d.title,
          category: d.category, // d.category.name
          count: d.viewcount,
          timeinfo: d.timestamp,
          user: d.user,
          upvotes: d.upvotes,
          downvotes: d.downvotes,
          tags: d.tags,
          postcount: d.postcount,

        }
      })[0]
    }
  }

  fillPopular() {
    this.discussService.fetchRecentD().subscribe((response: any) => {
      this.discuss = _.get(response, 'topics')
      // console.log(this.discuss)
    })
  }

  getDiscussion(discuss: any) {
    this.router.navigate([`/app/discuss/home/${discuss.tid}`])
  }
}
