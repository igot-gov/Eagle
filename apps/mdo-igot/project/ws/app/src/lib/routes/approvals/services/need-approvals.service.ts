import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  WORKFLOW_TO_APPROVE: '/apis/protected/v8/workflowhandler/applicationsSearch',
  PROFILE_REGISTRY: 'apis/protected/v8/user/profileRegistry/getUserRegistryById',
}

@Injectable({
  providedIn: 'root',
})
export class NeedApprovalsService {
  constructor(private http: HttpClient) { }
  fetchNeedApprovals(request: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.WORKFLOW_TO_APPROVE, request)
  }

  fetchProfileDeatils(): Observable<any> {
    return this.http.get<any>(API_END_POINTS.PROFILE_REGISTRY)
  }
}
