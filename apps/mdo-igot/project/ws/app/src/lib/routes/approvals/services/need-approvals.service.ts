import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  WORKFLOW_TO_APPROVE: '/apis/protected/v8/workflowhandler/applicationsSearch',
  PROFILE_REGISTRY: 'apis/protected/v8/user/profileRegistry/getUserRegistryByUser/',
  WORKFLOW_HANDLER: 'apis/protected/v8/workflowhandler/transition',
  WF_HISTORY_BY_APPID: 'apis/protected/v8/workflowhandler/historyByApplicationId/',
}

@Injectable({
  providedIn: 'root',
})
export class NeedApprovalsService {
  constructor(private http: HttpClient) { }
  fetchNeedApprovals(request: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.WORKFLOW_TO_APPROVE, request)
  }

  fetchProfileDeatils(userid: string): Observable<any> {
    return this.http.get<any>(API_END_POINTS.PROFILE_REGISTRY + userid)
  }

  handleWorkflow(request: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.WORKFLOW_HANDLER, request)
  }

  getWfHistoryByAppId(appid: string): Observable<any> {
    return this.http.get<any>(API_END_POINTS.WF_HISTORY_BY_APPID + appid)
  }
}
