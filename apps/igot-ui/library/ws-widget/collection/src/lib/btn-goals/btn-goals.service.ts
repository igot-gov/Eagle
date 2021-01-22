import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { NsGoal } from './btn-goals.model'
import { forkJoin, of } from 'rxjs'
import { catchError, map, tap } from 'rxjs/operators'

const API_END_POINTS = {
  acceptRejectGoal: (action: string, goalType: string, goalId: string, confirm: boolean) =>
    `/apis/protected/v8/user/goals/action/${action}/${goalType}/${goalId}?confirm=${confirm}`,
  createGoal: `/apis/protected/v8/user/goals`,
  deleteGoal: (type: string, id: string) => `/apis/protected/v8/user/goals/${type}/${id}`,
  deleteGoalForUsers: (type: string, id: string) =>
    `/apis/protected/v8/user/goals/removeUsers/${type}/${id}`,
  getCommonGoals: `/apis/protected/v8/user/goals/common`,
  getActionRequiredGoals: (sourceFields: string) =>
    `/apis/protected/v8/user/goals/action?sourceFields=${sourceFields}`,
  getUserGoals: (type: NsGoal.EGoalTypes, sourceFields: string) =>
    `/apis/protected/v8/user/goals/${type}?sourceFields=${sourceFields}`,
  getOthersGoals: (sourceFields: string) =>
    `/apis/protected/v8/user/goals/for-others?sourceFields=${sourceFields}`,
  trackGoal: (id: string, type: NsGoal.EGoalTypes) =>
    `/apis/protected/v8/user/goals/track/${type}/${id}`,
  shareGoal: (type: string, id: string) => `/apis/protected/v8/user/goals/share/${type}/${id}`,
  shareGoalV2: (type: string, id: string) => `/apis/protected/v8/user/goals/share/${type}/${id}`,
  updateDurationCommonGoal: (type: string, id: string, duration: number) =>
    `/apis/protected/v8/user/goals/updateDurationCommonGoal/${type}/${id}?duration=${duration}`,
  addContentToGoal: (goalId: string, contentId: string, goalType: string) =>
    `/apis/protected/v8/user/goals/addContent/${goalId}/${contentId}?goal_type=${goalType}`,
  removeContentFromGoal: (goalId: string, contentId: string, goalType: string) =>
    `/apis/protected/v8/user/goals/removeContent/${goalId}/${contentId}?goal_type=${goalType}`,
  getSearchData: `/apis/proxies/v8/sunbirdigot/search`,
}

@Injectable({
  providedIn: 'root',
})
export class BtnGoalsService {
  goalsHash: { [goalId: string]: NsGoal.IGoal } = {}

  constructor(private http: HttpClient) { }

  createGoal(upsertRequest: NsGoal.IGoalUpsertRequest) {
    return this.http.post<NsGoal.IGoalUpsertResponse>(API_END_POINTS.createGoal, upsertRequest)
  }

  updateGoal(id: any, upsertRequest: any) {
    return this.http.patch<NsGoal.IGoalUpsertResponse>(`/apis/protected/v8/user/goals/${id}`, upsertRequest)
  }

  createGoals(upsertRequests: NsGoal.IGoalUpsertRequest[]) {
    return forkJoin(
      upsertRequests.map(request => {
        return this.createGoal(request).pipe(
          catchError(error => of(error).pipe(map(_ => error.error.error))),
        )
      }),
    )
  }

  acceptRejectGoal(
    action: string,
    goalType: string,
    goalId: string,
    sharedBy: string,
    message?: string,
    confirm = true,
  ) {
    return this.http.post(API_END_POINTS.acceptRejectGoal(action, goalType, goalId, confirm), {
      message,
      sharedBy,
    })
  }

  getCommonGoals() {
    return this.http.get<NsGoal.IGoalsGroup[]>(API_END_POINTS.getCommonGoals)
  }

  getGoalGroup(groupId: string) {
    return this.http.get<NsGoal.IGoalsGroup>(`${API_END_POINTS.getCommonGoals}/${groupId}`)
  }

  getUserGoals(type: NsGoal.EGoalTypes, sourceFields: string = '') {
    return this.http.post<NsGoal.IUserGoals>(`/apis/proxies/v8/sunbirdigot/search?type=${type}&sourceFields=${sourceFields}`, {
      request: {
        filters: {
          primaryCategory: 'Playlist',
          visibility: 'Private',
          status: ['Draft', 'Live'],

        },
        fields: [],
        limit: 100,
        facets: [

        ],
      },
    })

  }

  getUserGoal(id: string) {
    return this.http.get<NsGoal.IUserGoals>(`/apis/proxies/v8/action/content/v3/hierarchy/${id}?mode=edit`)
  }

  getGoalContent(id: string) {
    return this.http.get<NsGoal.IGoalsGroup>(`/apis/proxies/v8/action/content/v3/hierarchy/${id}?mode=edit`)

  }

  getOthersGoals(sourceFields: string = '') {
    return this.http.get<NsGoal.IGoal[]>(API_END_POINTS.getOthersGoals(sourceFields)).pipe(
      tap(goals => {
        this.goalsHash = goals.reduce((hash: { [id: string]: NsGoal.IGoal }, obj: NsGoal.IGoal) => {
          hash[obj.id] = obj
          return hash
        },                            {})
      }),
    )
  }

  getActionRequiredGoals(sourceFields: string = '') {
    return this.http.get<any[]>(API_END_POINTS.getActionRequiredGoals(sourceFields))
  }

  trackGoal(id: string, type: NsGoal.EGoalTypes) {
    return this.http.get<any[]>(API_END_POINTS.trackGoal(id, type))
  }

  shareGoal(type: string, id: string, users: string[]) {
    return this.http.post<any>(API_END_POINTS.shareGoal(type, id), { users })
  }

  shareGoalV2(type: string, id: string, users: string[], message: string) {
    return this.http.post<any>(API_END_POINTS.shareGoalV2(type, id), {
      users,
      message,
    })
  }

  deleteGoal(type: string, id: string) {
    return this.http.delete(API_END_POINTS.deleteGoal(type, id))
  }

  deleteGoalForUsers(type: string, id: string, users: string[]) {
    return this.http.post(API_END_POINTS.deleteGoalForUsers(type, id), {
      users,
    })
  }

  updateDurationCommonGoal(goalType: string, goalId: string, duration: number) {
    return this.http.get(API_END_POINTS.updateDurationCommonGoal(goalType, goalId, duration))
  }

  addContentToGoal(goalId: string, contentId: string, goalType: NsGoal.EGoalTypes) {
    return this.http.patch<NsGoal.IGoalAddContentResponse>(
      API_END_POINTS.addContentToGoal(goalId, contentId, goalType),
      {},
    )
  }

  removeContentFromGoal(goalId: string, contentId: string, goalType: NsGoal.EGoalTypes) {
    return this.http.delete<NsGoal.IGoalRemoveContentResponse>(
      API_END_POINTS.removeContentFromGoal(goalId, contentId, goalType),
    )
  }
}
