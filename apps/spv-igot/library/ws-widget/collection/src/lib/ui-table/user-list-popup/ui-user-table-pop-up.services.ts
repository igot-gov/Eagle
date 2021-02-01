import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_USER_BY_DEPARTMENT: '/apis/protected/v8/user/autocomplete/',
}

@Injectable({
  providedIn: 'root',
})
export class UserViewPopUpService {
  constructor(private http: HttpClient) { }
  getAllUsersByDepartments(searchString: string): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_ALL_USER_BY_DEPARTMENT}${searchString}`)
  }
}
