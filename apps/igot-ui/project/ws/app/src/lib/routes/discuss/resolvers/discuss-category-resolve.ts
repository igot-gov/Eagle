import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse } from '@ws-widget/utils'
import { DiscussService } from '../services/discuss.service'
import { NSDiscussData } from '../models/discuss.model'

@Injectable()
export class DiscussCategoriesResolve
  implements
  Resolve<Observable<IResolveResponse<NSDiscussData.ICategorie[]>> | IResolveResponse<NSDiscussData.ICategorie[]>> {
  constructor(private discussionSvc: DiscussService) { }

  resolve(
    _route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSDiscussData.ICategorie[]>> {
    return this.discussionSvc.fetchAllCategorie().pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
