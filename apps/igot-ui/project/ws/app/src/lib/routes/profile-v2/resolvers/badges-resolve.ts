import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable } from 'rxjs'
// import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { ConfigurationsService, IResolveResponse } from '@ws-widget/utils'
// import { ProfileV2UtillService } from '../services/profile-v2-utill.service'
import { NSProfileDataV2 } from '../models/profile-v2.model'

@Injectable()
export class Profilev2BadgesResolve
  implements
  Resolve<Observable<IResolveResponse<NSProfileDataV2.IBadgeResponse>> | IResolveResponse<NSProfileDataV2.IBadgeResponse>> {
  constructor(private configSvc: ConfigurationsService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSProfileDataV2.IBadgeResponse>> {
    const path = _route.routeConfig && _route.routeConfig.path
    let userId = ''
    if (path !== 'me') {
      userId = _route.params.userId
      if (!userId) {
        userId = _route.queryParams.userId
      }

      if (!userId) {
        userId = this.configSvc.userProfile && this.configSvc.userProfile.userId || ''
      }
    } else {
      userId = this.configSvc.userProfile && this.configSvc.userProfile.userId || ''
    }
    const data: any = ''
    return data
    // return this.profileV2Svc.fetchBadges(userId).pipe(
    //   map(data => ({ data, error: null })),
    //   catchError(error => of({ error, data: null })),
    // )
  }
}
