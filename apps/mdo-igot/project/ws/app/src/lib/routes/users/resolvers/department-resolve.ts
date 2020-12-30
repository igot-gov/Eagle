import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse } from '@ws-widget/utils'
import { NSProfileDataV2 } from '../../home/models/profile-v2.model'
import { UsersService } from '../services/users.service'

@Injectable()
export class DepartmentResolve
  implements
  Resolve<Observable<IResolveResponse<NSProfileDataV2.IProfile>> | IResolveResponse<NSProfileDataV2.IProfile>> {
  constructor(private usersService: UsersService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSProfileDataV2.IProfile>> {

    return this.usersService.getMyDepartment().pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
