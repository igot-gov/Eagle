import { Component, OnInit, Input } from '@angular/core'
import { WidgetBaseComponent, NsWidgetResolver } from '@ws-widget/resolver'
import { DiscussService } from '@ws/app/src/lib/routes/discuss/services/discuss.service'

/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'ws-widget-home-component',
  templateUrl: './card-home-discuss.component.html',
  styleUrls: ['./card-home-discuss.component.scss'],
  providers: [DiscussService],
})
export class CardHomeDiscussComponent extends WidgetBaseComponent implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  homePageMaxLength = 4
  constructor(private discussService: DiscussService) {
    super()
  }
  discussionList = Array()
  // starColor: StarRatingColor = StarRatingColor.accent
  // starColorP: StarRatingColor = StarRatingColor.primary
  // starColorW: StarRatingColor = StarRatingColor.warn
  ngOnInit(): void {
    this.fillPopular()
  }

  fillPopular() {
    this.discussService.fetchRecentD().subscribe(response => {
      this.discussionList = _.get(response, 'topics')
      this.discussionList.length = this.homePageMaxLength
    })
  }
}
