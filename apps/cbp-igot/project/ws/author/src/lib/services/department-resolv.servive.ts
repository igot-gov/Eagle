import { Injectable } from '@angular/core'
import { Resolve } from '@angular/router'
import { Observable, of } from 'rxjs'
import { ApiService } from '../modules/shared/services/api.service'
import { GET_MY_DEPARTMENT } from '../constants/apiEndpoints'
import { catchError } from 'rxjs/operators'
import { IDepartment } from '../interface/department'

@Injectable()
export class DepartmentResolver implements Resolve<IDepartment> {

  constructor(
    private apiService: ApiService,
    // private router: Router,
  ) {
  }

  resolve(
  ): Observable<IDepartment> {
    return this.apiService.get<IDepartment>(
      `${GET_MY_DEPARTMENT}`,
    ).pipe(
      catchError((v: any) => {
        // this.router.navigateByUrl('/error-somethings-wrong')
        return of(v)
      }),
    )
  }
}
