import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  GET_ALL_USERS: '/apis/protected/v8/portal/mydepartment?allUsers=true',
  GET_MY_DEPARTMENT: '/apis/protected/v8/portal/mydepartment?allUsers=true',
  CREATE_USER: 'apis/protected/v8/admin/userRegistration/create-user',
  PROFILE_REGISTRY: 'apis/protected/v8/user/profileRegistry/getUserRegistryByUser/',
}

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  constructor(private http: HttpClient) { }
  getAllUsers(): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_ALL_USERS}`)
  }

  getMyDepartment(): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.GET_MY_DEPARTMENT}`)
  }

  createUser(req: any): Observable<any> {
    return this.http.post<any>(API_END_POINTS.CREATE_USER, req)
  }

  getUserById(userid: string): Observable<any> {
    return this.http.get<any>(API_END_POINTS.PROFILE_REGISTRY + userid)
  }
}
