import { Injectable, Type } from '@angular/core'
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
// commercial_begin
import { AppTocOverviewPathfindersComponent } from '../../components/app-toc-overview-pathfinders/app-toc-overview-pathfinders.component'
import { AppTocOverviewLestoreComponent } from '../../components/app-toc-overview-lestore/app-toc-overview-lestore.component'
// commercial_end
import { AppTocOverviewComponent } from '../../components/app-toc-overview/app-toc-overview.component'

@Injectable({
  providedIn: 'root',
})
export class AppTocOverviewService {

  constructor(
    private configSvc: ConfigurationsService,
  ) { }

  getComponent(): Type<any> {
    // commercial_begin
    switch (this.configSvc.rootOrg) {
      case EInstance.PATHFINDERS:
        return AppTocOverviewPathfindersComponent
      case EInstance.LESTORE:
        return AppTocOverviewLestoreComponent
      default:
        return AppTocOverviewComponent
    }
    // commercial_end

    // opensource_start
    // return AppTocOverviewComponent
    // opensource_end
  }
}
