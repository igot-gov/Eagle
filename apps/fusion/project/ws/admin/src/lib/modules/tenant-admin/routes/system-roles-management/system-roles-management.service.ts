import { HttpClient } from '@angular/common/http'
import { Injectable } from '@angular/core'
import { IManageUser, IUsersRole } from './system-roles-management.model'
import { Observable } from 'rxjs'

const END_POINT_BASE = '/apis/protected/v8/user'
const API_END_POINTS = {
  checkUser: (wid: string) => `${END_POINT_BASE}/roles/getRolesV2/${wid}`,
  manageUser: `${END_POINT_BASE}/roles/updateRolesV2`,
  getUser: (role: string) => `${END_POINT_BASE}/roles/getUsersV2/${role}`,
}

interface IUserRoles {
  default_roles: string[]
  user_roles: string[]
}
@Injectable({
  providedIn: 'root',
})
export class SystemRolesManagementService {
  constructor(
    private http: HttpClient,
  ) { }

  checkUser(wid: string): Observable<IUserRoles> {
   return this.http.get<any>(API_END_POINTS.checkUser(wid))
  }
  manageUser(data: IManageUser) {
    return this.http.post<any>(API_END_POINTS.manageUser, data).toPromise()
  }
   getAllUser(role: string) {
     return this.http.get<IUsersRole>(API_END_POINTS.getUser(role)).toPromise()
  }

}
