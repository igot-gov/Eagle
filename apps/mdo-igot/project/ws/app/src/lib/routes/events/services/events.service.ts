import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  CREATE_EVENT: '/apis/authApi/action/content/create?rootOrg=igot&org=dopt',
  UPDATE_EVENT: '/apis/authApi/action/content/v2/hierarchy/update?rootOrg=igot&org=dopt',
  PUBLISH_EVENT: '/apis/authApi/action/content/status/change',
  SEARCH_EVENT: '/apis/protected/v8/content/searchV6',
  GET_PARTICIPANTS: '/apis/protected/v8/portal/mdo/mydepartment?allUsers=true',
  IMAGE_UPLOAD: '/apis/authContent/upload/igot/dopt/Public',
  SEARCH_USERS: '/apis/protected/v8/user/autocomplete/department',
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

  searchEvent(req: any) {
    return this.http.post<any>(API_END_POINTS.SEARCH_EVENT, req)
  }

  getParticipants(): Observable<any> {
    return this.http.get<any>(API_END_POINTS.GET_PARTICIPANTS)
  }

  uploadCoverImage(req: any, eventId: string): Observable<any> {
    return this.http.post<any>(`${API_END_POINTS.IMAGE_UPLOAD}/${eventId}/artifacts`, req)
  }

  getEvents(): Observable<any> {
    return this.http.get<any>(API_END_POINTS.SEARCH_EVENT)
  }

  searchUser(value: any, req: any): Observable<any> {
      return this.http.post<any>(`${API_END_POINTS.SEARCH_USERS}/${value}`, req)
  }
}
