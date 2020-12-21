import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse, ConfigurationsService } from '@ws-widget/utils'
import { NetworkV2Service } from '../services/network-v2.service'
import { NSNetworkDataV2 } from '../models/network-v2.model'

@Injectable({
  providedIn: 'root',
})
export class MyMdoResolveService implements
  Resolve<Observable<IResolveResponse<NSNetworkDataV2.IRecommendedUserResponse>> |
  IResolveResponse<NSNetworkDataV2.IRecommendedUserResponse>> {
  constructor(private networkV2Service: NetworkV2Service, private configSvc: ConfigurationsService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSNetworkDataV2.IRecommendedUserResponse>> {
    let usrDept = 'iGOT'
    if (this.configSvc.userProfile) {
      usrDept = this.configSvc.userProfile.departmentName || 'iGOT'
    }
    let req: NSNetworkDataV2.IRecommendedUserReq
    req = {
      size: 50,
      offset: 0,
      search: [
        {
          field: 'employmentDetails.departmentName',
          values: [usrDept],
        },
      ],
    }
    return this.networkV2Service.fetchAllRecommendedUsers(req).pipe(
      map((data: any) => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
