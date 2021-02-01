import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_SPV_USERS: '/apis/protected/v8/portal/spv/department/1?allUsers=true',
}

@Injectable({
  providedIn: 'root',
})
export class UserViewService {
  constructor(private http: HttpClient) { }
  getAllDepartments(): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_ALL_SPV_USERS}`)
  }
}
