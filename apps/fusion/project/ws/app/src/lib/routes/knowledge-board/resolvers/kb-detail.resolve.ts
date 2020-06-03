import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'

import { Observable, of } from 'rxjs'
import { map, catchError, first } from 'rxjs/operators'

import { IResolveResponse } from '@ws-widget/utils'
import { NsContent, WidgetContentService } from '@ws-widget/collection'

@Injectable()
export class KbDetailResolve
  implements
  Resolve<
  | Observable<IResolveResponse<NsContent.IContent>>
  | IResolveResponse<NsContent.IContent>
  > {
  constructor(private contentSvc: WidgetContentService) { }

  resolve(
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NsContent.IContent>> {
    return this.contentSvc.fetchContent(
      route.params.id,
      'detail',
      ['averageRating', 'creatorContacts', 'creatorDetails', 'totalRating',
        'uniqueLearners', 'viewCount'])
      .pipe(
        first(),
        map(data => ({ data, error: null })),
        catchError(error => of({ error, data: null })),
      )
  }
}
