import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { NSProfileDataV2 } from '../models/profile-v2.model'
import { map } from 'rxjs/operators'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  USER_BADGE: (wid: string) => `${PROTECTED_SLAG_V8}/user/badge/for/${wid}`,
  USER_BADGE_RECENT: `${PROTECTED_SLAG_V8}/user/badge/notification`,
  USER_BADGES_UPDATE: `${PROTECTED_SLAG_V8}/user/badge/update`,
}

@Injectable({
  providedIn: 'root',
})
export class ProfileV2UtillService {
  constructor(private http: HttpClient) { }
  fetchBadges(wid: string): Observable<NSProfileDataV2.IBadgeResponse> {
    return this.http.get<NSProfileDataV2.IBadgeResponse>(`${API_END_POINTS.USER_BADGE(wid)}`)
  }

  reCalculateBadges(): Observable<any> {
    return this.http.post(`${API_END_POINTS.USER_BADGES_UPDATE}`, {})
  }

  fetchRecentBadge(): Observable<NSProfileDataV2.IUserNotifications> {
    return this.http
      .get<any>(API_END_POINTS.USER_BADGE_RECENT)
      .pipe(map(notifications => notifications))
  }
}
