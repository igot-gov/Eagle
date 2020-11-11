import { Injectable } from '@angular/core'
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
  ActivatedRoute,
} from '@angular/router'
import { Observable } from 'rxjs'
import { ConfigurationsService, AuthKeycloakService } from '@ws-widget/utils'

@Injectable({
  providedIn: 'root',
})
export class EmptyRouteGuard implements CanActivate {
  constructor(
    private router: Router,
    private configSvc: ConfigurationsService,
    private authSvc: AuthKeycloakService,
    private activateRoute: ActivatedRoute
  ) { }
  canActivate(
    _next: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.configSvc.isAuthenticated) {
      // logger.log('Redirecting to application home page');
      return this.router.parseUrl('/app/home')
    }
    // logger.log('redirecting to login page as the user is not loggedIn');
    // return this.router.parseUrl('/login')
    const paramsMap = this.activateRoute.snapshot.queryParamMap
    let redirectUrl
    if (paramsMap.has('ref')) {
      redirectUrl = document.baseURI + paramsMap.get('ref')
    } else {
      redirectUrl = document.baseURI
    }
    Promise.resolve(this.authSvc.login('S', redirectUrl))
    return false
  }
}
