import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { map, catchError } from 'rxjs/operators'
import { } from '@ws-widget/collection'
import { IResolveResponse } from '@ws-widget/utils'
import { DiscussService } from '../services/discuss.service'
import { NSDiscussData } from '../models/discuss.model'

@Injectable()
export class DiscussResolve
  implements
  Resolve<Observable<IResolveResponse<NSDiscussData.IDiscussionData>> | IResolveResponse<NSDiscussData.IDiscussionData>> {
  constructor(private discussSvc: DiscussService) { }

  resolve(
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<IResolveResponse<NSDiscussData.IDiscussionData>> {
    const topicId = route.params.topicId
    return this.discussSvc.getTopicDetails(topicId).pipe(
      map(data => ({ data, error: null })),
      catchError(error => of({ error, data: null })),
    )
  }
}
