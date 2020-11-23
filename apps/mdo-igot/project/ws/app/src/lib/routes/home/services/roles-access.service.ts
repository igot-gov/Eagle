import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'

const API_END_POINTS = {
  ROLES: '/apis/protected/v8/user/roles/rolesv2/usercount',
}

@Injectable({
  providedIn: 'root',
})
export class RolesAccessService {
  constructor(private http: HttpClient) { }
  getRoles(): Observable<any> {
    return this.http.get<any>(`${API_END_POINTS.ROLES}`)
  }
}
