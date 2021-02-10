import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { EMPTY, Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { ConfigurationsService, IResolveResponse } from '@ws-widget/utils'
import { NeedApprovalsService } from '../services/need-approvals.service'
import { NSProfileDataV2 } from '../models/profile-v2.model'

@Injectable()
export class WorkflowResolve
  implements
  Resolve<Observable<IResolveResponse<NSProfileDataV2.IProfile>> | IResolveResponse<NSProfileDataV2.IProfile>> {
  constructor(private needApprService: NeedApprovalsService, private configSvc: ConfigurationsService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,

  ): Observable<IResolveResponse<NSProfileDataV2.IProfile>> {
    const path = _route.routeConfig && _route.routeConfig.path
    const departName = this.configSvc.departName
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
    const req = {
      serviceName: 'profile',
      applicationStatus: 'SEND_FOR_APPROVAL',
      applicationIds: [userId],
      deptName: departName,
      offset: 0,
      limit: 100,
    }
    if (departName) {
      return this.needApprService.fetchNeedApprovals(req).pipe(
        map(data => ({ data, error: null })),
        catchError(error => of({ error, data: null })),
      )
    }
    return EMPTY
  }
}
