import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_SPV_USERS: '/apis/protected/v8/portal/spv/department/1?allUsers=true',
  GET_ALL_USER_BY_DEPARTMENT: '/apis/protected/v8/user/autocomplete/department/',
}

@Injectable({
  providedIn: 'root',
})
export class UserViewService {
  constructor(private http: HttpClient) { }
  getAllDepartments(): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_ALL_SPV_USERS}`)
  }
  getAllUsersByDepartments(searchString: string): Observable<any> {
    const department = { departments: ['igot', 'istm', 'iGOT', 'NPA', 'NACIN', 'LSNAA'] }
    return this.http.post<any>(`${API_END_POINTS.GET_ALL_USER_BY_DEPARTMENT}${searchString}`, department)
  }
}
