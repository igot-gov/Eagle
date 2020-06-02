import { Injectable } from '@angular/core'
import { HttpClient, HttpHeaders } from '@angular/common/http'
import { Observable } from 'rxjs'
import { ConfigurationsService } from '@ws-widget/utils'

const PROTECTED_SLAG_V8 = `/LA1/api`

const LA_API_END_POINTS = {
  COMPASS_ROLES: `${PROTECTED_SLAG_V8}/nso/getCourseAndProgress`,
}
@Injectable({
  providedIn: 'root',
})

export class CompassRoleService {
  httpOptions = {
    headers: new HttpHeaders({
      validator_URL: `https://${this.configSvc.hostPath}/apis/protected/v8/user/validate`,
    }),
  }
  constructor(private http: HttpClient, private configSvc: ConfigurationsService) { }

  getRoles(
    roleId: string,
  ): Observable<any> {
    return this.http.get<any>(
      `${LA_API_END_POINTS.COMPASS_ROLES}?role_id=${roleId}`, this.httpOptions,
    )
  }
}
