// import { Injectable } from '@angular/core'
// import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router'
// import { EMPTY, Observable } from 'rxjs'
// import { map, catchError } from 'rxjs/operators'
// import { } from '@ws-widget/collection'
// import { AuthKeycloakService, IResolveResponse } from '@ws-widget/utils'
// import { NSProfileDataV2 } from '../../home/models/profile-v2.model'
// import { UsersService } from '../services/users.service'

// @Injectable()
// export class DepartmentResolve
//   implements
//   Resolve<Observable<IResolveResponse<NSProfileDataV2.IProfile>> | IResolveResponse<NSProfileDataV2.IProfile>> {
//   constructor(private usersService: UsersService, private router: Router, private authSvc: AuthKeycloakService) { }

//   resolve(
//     _route: ActivatedRouteSnapshot,
//     _state: RouterStateSnapshot,
//   ): Observable<IResolveResponse<NSProfileDataV2.IProfile>> {
//     return this.usersService.getMyDepartment().pipe(
//       map(data => ({ data, error: null })),
//       catchError(() => {
//         this.router.navigate(['error-access-forbidden'])
//         this.authSvc.logout()
//         return EMPTY
//       })
//     )
//   }
// }
