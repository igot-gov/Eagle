import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import {
  ELeaderboardDurationType,
  ILeaderboard,
  IHallOfFameItem,
} from '../models/leaderboard.model'
import { Observable } from 'rxjs'

@Injectable({
  providedIn: 'root',
})
export class LeaderboardApiService {
  constructor(private http: HttpClient) {}

  private readonly API_BASE = '/apis/protected/v8/leaderboard'

  getLeaderboardData(
    durationType: ELeaderboardDurationType,
    durationValue?: number,
    year?: number,
  ): Observable<ILeaderboard> {
    let url = `${this.API_BASE}/${durationType}`
    url += durationValue && year ? `/${durationValue}/${year}` : ''

    return this.http.get<ILeaderboard>(url)
  }

  getHallOfFameData(): Observable<IHallOfFameItem[]> {
    return this.http.get<IHallOfFameItem[]>(`${this.API_BASE}/hallOfFame`)
  }
}
