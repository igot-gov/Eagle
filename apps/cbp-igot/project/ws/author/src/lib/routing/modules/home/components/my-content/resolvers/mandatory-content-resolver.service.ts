import { Injectable } from '@angular/core'
import { Resolve } from '@angular/router'
import { Observable, of } from 'rxjs'
import { catchError, map } from 'rxjs/operators'
import { ConfigurationsService } from '@ws-widget/utils'
import { MyContentService } from '../services/my-content.service'



@Injectable()
export class MandatoryContentResolverService
  implements
  Resolve<
  Observable<any> | null
  > {
  constructor(
    private mySvc: MyContentService,
    // private routePipe: PipeContentRoutePipe,
    private configSvc: ConfigurationsService,

  ) { }

  resolve(): Observable<any> | null {
    if (this.configSvc.userProfile) {
      const userId = this.configSvc.userProfile.userId
      return this.mySvc.getUserCourseDetail(userId).pipe(
        map(data => ({ data, error: null })),
        catchError(error => of({ error, data: null })),
      )
    }
    return null
  }
}
