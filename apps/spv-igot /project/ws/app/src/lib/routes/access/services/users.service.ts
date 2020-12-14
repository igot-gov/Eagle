import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  USERS: '/apis/protected/v8/user/roles/getUsersV2',
}

@Injectable({
  providedIn: 'root',
})
export class UsersService {
  constructor(private http: HttpClient) { }
  getUsers(role: string): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.USERS}/${role}/`)
  }
}
