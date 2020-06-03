import { Injectable } from '@angular/core'
import { Resolve, ActivatedRouteSnapshot } from '@angular/router'
import { Observable, of } from 'rxjs'
import { IResolveResponse } from '../../../../../../../../library/ws-widget/utils/src/public-api'
import { ILeaderboard, EDurationTypeRouteParam } from '../models/leaderboard.model'
import { LeaderboardService } from '../services/leaderboard.service'
import { map, catchError } from 'rxjs/operators'

@Injectable({
  providedIn: 'root',
})
export class LeaderboardResolver implements Resolve<Observable<IResolveResponse<ILeaderboard>>> {
  constructor(private leaderboardSvc: LeaderboardService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResolveResponse<ILeaderboard>> {
    try {
      const tab: EDurationTypeRouteParam = route.url[route.url.length - 1]
        .path as EDurationTypeRouteParam

      return this.leaderboardSvc.getLeaderboard(tab).pipe(
        map(lb => ({ data: lb, error: null })),
        catchError(() => of({ data: null, error: 'LEADERBOARD_API_ERROR' })),
      )
    } catch (err) {
      return of({ data: null, error: 'LEADERBOARD_RESOLVER_ERROR' })
    }
  }
}
