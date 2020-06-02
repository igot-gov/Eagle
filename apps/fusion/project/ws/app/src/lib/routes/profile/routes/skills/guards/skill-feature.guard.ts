import { Injectable } from '@angular/core'
import { CanActivate, UrlTree } from '@angular/router'
import { Observable } from 'rxjs'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Injectable({
  providedIn: 'root',
})
export class SkillGuard implements CanActivate {
  constructor(
    private configSvc: ConfigurationsService,
  ) {
  }
  canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.configSvc.userRoles && this.configSvc.userRoles.has('my-skills')) {
      return true
    }
    return false
  }
}
