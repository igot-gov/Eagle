import { Injectable } from '@angular/core'
import { IHallOfFameItem } from '../models/leaderboard.model'
import { Resolve } from '@angular/router'
import { LeaderboardApiService } from '../apis/leaderboard-api.service'
import { Observable, of } from 'rxjs'
import { IResolveResponse } from '@ws-widget/utils'
import { map, catchError } from 'rxjs/operators'

@Injectable({
  providedIn: 'root',
})
export class HallOfFameResolver
  implements Resolve<Observable<IResolveResponse<IHallOfFameItem[]>>> {
  constructor(private leaderboardApi: LeaderboardApiService) {}

  resolve(): Observable<IResolveResponse<IHallOfFameItem[]>> {
    try {
      return this.leaderboardApi.getHallOfFameData().pipe(
        map(hof => {
          return {
            data: hof,
            error: null,
          }
        }),
        catchError(() => {
          const result: IResolveResponse<IHallOfFameItem[]> = {
            data: null,
            error: 'HALL_OF_FAME_API_ERROR',
          }

          return of(result)
        }),
      )
    } catch (err) {
      const result: IResolveResponse<IHallOfFameItem[]> = {
        data: null,
        error: 'HALL_OF_FAME_RESOLVER_ERROR',
      }
      return of(result)
    }
  }
}
