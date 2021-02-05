import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { EMPTY, Observable } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { IResolveResponse } from '@ws-widget/utils'
import { NSProfileDataV2 } from '../../home/models/profile-v2.model'
import { ProfileV2Service } from '../services/home.servive'

@Injectable()
export class DepartmentResolve
  implements
  Resolve<Observable<IResolveResponse<NSProfileDataV2.IProfile>> | IResolveResponse<NSProfileDataV2.IProfile>> {
  constructor(
    private profileService: ProfileV2Service,
    // private router: Router,
    // private authSvc: AuthKeycloakService
  ) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSProfileDataV2.IProfile>> {

    return this.profileService.getMyDepartment().pipe(
      map(data => ({ data, error: null })),
      catchError(() => {
        // this.router.navigate(['error-access-forbidden'])
        // this.authSvc.logout()
        return EMPTY
      }))
  }
}
