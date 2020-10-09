import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { ConfigurationsService, IResolveResponse } from '@ws-widget/utils'
import { ProfileV2Service } from '../services/profile-v2.servive'
import { NSProfileData } from '../models/profile-v2.model'

@Injectable()
export class Profilev2Resolve
  implements
  Resolve<Observable<IResolveResponse<NSProfileData.IProfile>> | IResolveResponse<NSProfileData.IProfile>> {
  constructor(private profileV2Svc: ProfileV2Service, private configSvc: ConfigurationsService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSProfileData.IProfile>> {
    let emailId = _route.params.emailId
    if (!emailId) {
      emailId = _route.queryParams.emailId
    }
    if (!emailId) {
      emailId = this.configSvc.userProfile && this.configSvc.userProfile.email || null
    }
    return this.profileV2Svc.fetchProfile(emailId).pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
