import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_USER_BY_DEPARTMENT: '/apis/protected/v8/user/autocomplete/department/',
}

@Injectable({
  providedIn: 'root',
})
export class UserViewPopUpService {
  constructor(private http: HttpClient) { }
  getAllUsersByDepartments(searchString: string): Observable<any> {
    const department = { departments: ['igot', 'istm', 'iGOT', 'NPA', 'NACIN', 'LSNAA'] }
    return this.http.post<any>(`${API_END_POINTS.GET_ALL_USER_BY_DEPARTMENT}${searchString}`, department)
  }
}
