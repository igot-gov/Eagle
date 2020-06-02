import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NsCohorts } from '../models/app-toc.model'
import { NsTocPathfinders } from '../models/app-toc-pathfinders-model'

const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  COHORTS_GROUP_USER: (groupId: number) =>
    `${PROTECTED_SLAG_V8}/cohorts/${groupId}`,
  ATTENDED_USERS: (contentId: string) => `${PROTECTED_SLAG_V8}/attended-content/attendedUsers/${contentId}`,
  VERIFY_ATTENDED_USERS: (contentIds: string) =>
    `${PROTECTED_SLAG_V8}/attended-content/verifyAttendedUsers?contentIds=${contentIds}`
  ,
}

@Injectable({
  providedIn: 'root',
})
export class AppTocLestoreService {

  constructor(private http: HttpClient) { }

  fetchCohortGroupUsers(groupId: number) {
    return this.http.get<NsCohorts.ICohortsGroupUsers[]>(API_END_POINTS.COHORTS_GROUP_USER(groupId))
  }

  fetchAttendedUsers(contentId: string) {
    return this.http.get<NsTocPathfinders.IAttendedUsers[]>(API_END_POINTS.ATTENDED_USERS(contentId))
  }
  verifyAttendedUsers(contentIds: string) {
    return this.http.get<{ [key: string]: boolean }>(API_END_POINTS.VERIFY_ATTENDED_USERS(contentIds))
  }

}
