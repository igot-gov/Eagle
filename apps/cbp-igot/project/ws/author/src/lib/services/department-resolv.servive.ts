import { Injectable } from '@angular/core'
import { Resolve, Router } from '@angular/router'
import { EMPTY, Observable } from 'rxjs'
import { ApiService } from '../modules/shared/services/api.service'
import { GET_MY_DEPARTMENT } from '../constants/apiEndpoints'
import { catchError, map } from 'rxjs/operators'
import { IDepartment } from '../interface/department'
import { AuthKeycloakService, IResolveResponse } from '@ws-widget/utils'

@Injectable()
export class DepartmentResolver
  implements Resolve<Observable<IResolveResponse<IDepartment>> | IResolveResponse<IDepartment>>  {

  constructor(
    private apiService: ApiService,
    private router: Router, private authSvc: AuthKeycloakService
  ) {
  }

  resolve(): Observable<IResolveResponse<IDepartment>> {
    return this.apiService.get(GET_MY_DEPARTMENT).pipe(
      map(data => ({ data, error: null })),
      catchError(() => {
        this.router.navigate(['error-access-forbidden'])
        this.authSvc.logout()
        return EMPTY
      })
    )
  }
}
