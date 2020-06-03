import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NsMiniProfile } from './mini-profile.model'
import { Observable, throwError } from 'rxjs'
import { catchError } from 'rxjs/operators'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'
const API_END_POINTS = {
  userMiniProfile: (wid: string) => `${PROTECTED_SLAG_V8}/user/mini-profile/${wid}`,
  CONTENT_SEARCH_V5: `${PROTECTED_SLAG_V8}/content/searchV5`,
  FETCH_USER_GROUPS: (userId: string) => `${PROTECTED_SLAG_V8}/user/group/fetchUserGroup?userId=${userId}`,
}

@Injectable({
  providedIn: 'root',
})
export class UserMiniProfileService {
  constructor(
    private http: HttpClient,

  ) { }
  viewMiniProfile(wid: string): Observable<any> {
    return this.http.get<NsMiniProfile.IMiniProfileData>(API_END_POINTS.userMiniProfile(wid)).pipe(
      catchError(this.handleError))
  }

  handleError(error: ErrorEvent) {
    let errorMessage = ''
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.error.message}`
    }
    return throwError(errorMessage)
  }
  fetchGroupDetails(userId: string): Observable<any> {
    return this.http.get<NsMiniProfile.IUserGroupDetails>(API_END_POINTS.FETCH_USER_GROUPS(userId)).pipe(
      catchError(this.handleError))
  }
}
