import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

// const API_END_POINTS = {
//   GET_ALL_USERS: '/apis/protected/v8/portal/mdo/mydepartment?allUsers=true',
//   GET_MY_DEPARTMENT: '/apis/protected/v8/portal/mdo/mydepartment?allUsers=true',
//   CREATE_USER: 'apis/protected/v8/admin/userRegistration/create-user',
//   PROFILE_REGISTRY: 'apis/protected/v8/user/profileRegistry/getUserRegistryByUser/',
//   ADD_USER_TO_DEPARTMENT: 'apis/protected/v8/portal/deptAction',
//   WF_HISTORY_BY_APPID: 'apis/protected/v8/workflowhandler/historyByApplicationId/',
//   SEARCH_USER: 'apis/protected/v8/user/autocomplete/department',
// }

const API_END_POINTS = {
  CREATE_EVENT: '/apis/authApi/action/content/create?rootOrg=igot&org=dopt',
  UPDATE_EVENT: '/apis/authApi/action/content/v2/hierarchy/update?rootOrg=igot&org=dopt',
  PUBLISH_EVENT: '/apis/authApi/action/content/status/change',
  SEARCH_EVENT: '/apis/protected/v8/content/searchV6',
  GET_PARTICIPANTS: '/apis/protected/v8/portal/mdo/mydepartment?allUsers=true',
  IMAGE_UPLOAD: '/apis/authContent/upload/igot/dopt/Public/lex_auth_01319267541061632039/artifacts',
}

@Injectable({
  providedIn: 'root',
})
export class EventsService {
  constructor(private http: HttpClient) { }

  createEvent(req: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.CREATE_EVENT, req)
  }

  updateEvent(req: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.UPDATE_EVENT, req)
  }

  publishEvent(req: any, eventId: string): Observable<any> {
    return this.http.post<any>(`${API_END_POINTS.PUBLISH_EVENT}/${eventId}`, req)
  }

  onSearchEvent(req: any): Observable<any> {
    return this.http.post<any>(`${API_END_POINTS.SEARCH_EVENT}`, req)
  }

  getParticipants(): Observable<any> {
    return this.http.get<any>(API_END_POINTS.GET_PARTICIPANTS)
  }

  uploadCoverImage(req: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.IMAGE_UPLOAD, req)
  }

  getEvents(): Observable<any> {
    return this.http.get<any>(API_END_POINTS.SEARCH_EVENT)
  }
}
