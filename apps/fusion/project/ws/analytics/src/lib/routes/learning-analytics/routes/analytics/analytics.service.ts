import { Injectable, Type } from '@angular/core'
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
import { ClientAnalyticsComponent } from '../../components/client-analytics/client-analytics.component'
import { IframeAnalyticsComponent } from '../../components/iframe-analytics/iframe-analytics.component'

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {

  constructor(
    private configSvc: ConfigurationsService,
  ) { }

  getComponent(): Type<any> {
    switch (this.configSvc.rootOrg) {
      case EInstance.FORD:
        return ClientAnalyticsComponent
      default:
        return IframeAnalyticsComponent
    }
  }
}
