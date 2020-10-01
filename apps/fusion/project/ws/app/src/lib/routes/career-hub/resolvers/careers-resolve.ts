import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse } from '@ws-widget/utils'
import { DiscussService } from '../../discuss/services/discuss.service'
import { NSDiscussData } from '../../discuss/models/discuss.model'

@Injectable()
export class CareerRecentResolve
  implements
  Resolve<Observable<IResolveResponse<NSDiscussData.ICategoryData>> | IResolveResponse<NSDiscussData.ICategoryData>> {
  constructor(private discussionSvc: DiscussService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSDiscussData.ICategoryData>> {
    const categoryId = _route.data['careersCategoryId'] || 1
    return this.discussionSvc.fetchSingleCategoryDetails(categoryId).pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
