import { Injectable, Type } from '@angular/core'
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
import { AppTocCohortsPathfindersComponent } from '../../components/app-toc-cohorts-pathfinders/app-toc-cohorts-pathfinders.component'

@Injectable({
  providedIn: 'root',
})
export class AppTocCohortsService {

  constructor(
    private configSvc: ConfigurationsService,
  ) { }

  getComponent(): Type<any> {
    switch (this.configSvc.rootOrg) {
      case EInstance.PATHFINDERS:
        return AppTocCohortsPathfindersComponent
      default:
        return AppTocCohortsPathfindersComponent
    }
  }
}
