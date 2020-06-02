import { Injectable, Type } from '@angular/core'
import { ConfigurationsService, EInstance } from '@ws-widget/utils'
// commercial_begin
import { AppTocHomePathfindersComponent } from '../../components/app-toc-home-pathfinders/app-toc-home-pathfinders.component'
import { AppTocHomeLestoreComponent } from '../../components/app-toc-home-lestore/app-toc-home-lestore.component'
// commercial_end
import { AppTocHomeComponent } from '../../components/app-toc-home/app-toc-home.component'

@Injectable({
  providedIn: 'root',
})
export class AppTocHomeService {

  constructor(
    private configSvc: ConfigurationsService,
  ) { }

  getComponent(): Type<any> {
    // commercial_begin
    switch (this.configSvc.rootOrg) {
      case EInstance.PATHFINDERS:
        return AppTocHomePathfindersComponent
      case EInstance.LESTORE:
        return AppTocHomeLestoreComponent
      default:
        return AppTocHomeComponent
    }
    // commercial_end

    // opensource_start
    // return AppTocHomeComponent
    //  opensource_end
  }
}
