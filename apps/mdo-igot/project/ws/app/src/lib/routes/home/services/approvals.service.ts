import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  APPROVALS: '/apis/protected/v8/workflowhandler/applicationsSearch',
}

@Injectable({
  providedIn: 'root',
})
export class ApprovalsService {
  constructor(private http: HttpClient) { }
  getApprovals(request: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.APPROVALS, request)
  }
}
