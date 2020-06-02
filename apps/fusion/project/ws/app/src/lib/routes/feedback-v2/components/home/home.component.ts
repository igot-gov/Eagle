import { Component, OnDestroy } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { Subscription } from 'rxjs'

import { ConfigurationsService, NsPage } from '@ws-widget/utils'
import { EFeedbackType } from '@ws-widget/collection'

@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnDestroy {
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  feedbackType?: EFeedbackType
  feedbackTypes: typeof EFeedbackType
  private queryParamSub?: Subscription

  constructor(private configSvc: ConfigurationsService, private route: ActivatedRoute) {
    this.pageNavbar = this.configSvc.pageNavBar
    this.feedbackTypes = EFeedbackType

    this.queryParamSub = this.route.queryParamMap.subscribe(queryParams => {
      const feedbackType = queryParams.get('feedbackType') as EFeedbackType
      if (feedbackType) {
        this.feedbackType = feedbackType
      } else {
        this.feedbackType = undefined
      }
    })
  }

  ngOnDestroy() {
    if (this.queryParamSub && !this.queryParamSub.closed) {
      this.queryParamSub.unsubscribe()
    }
  }
}
