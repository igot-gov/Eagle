import { Injectable } from '@angular/core'
import { Resolve } from '@angular/router'
import { Observable, of } from 'rxjs'
import { catchError, map } from 'rxjs/operators'
import { MyContentService } from '../services/my-content.service'

@Injectable()
export class MandatoryContentResolverService
  implements
  Resolve<
  Observable<any> | null
  > {
  constructor(
    private mySvc: MyContentService,
  ) { }

  resolve(): Observable<any> | null {
    return this.mySvc.getUserCourseDetail().pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
