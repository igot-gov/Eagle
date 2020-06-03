import { Component, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'
import { AuthKeycloakService, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { IWSPublicLoginConfig } from './login.model'

@Component({
  selector: 'ws-pathfinders-login',
  templateUrl: './pathfinders-login.component.html',
  styleUrls: ['./pathfinders-login.component.scss'],
})
export class PathfindersLoginComponent implements OnInit, OnDestroy {
  appIcon: SafeUrl | null = null
  logo = ''
  bottomLogo = ''
  isClientLogin = false
  loginConfig: IWSPublicLoginConfig | null = null
  private redirectUrl = ''
  private subscriptionLogin: Subscription | null = null

  constructor(private activateRoute: ActivatedRoute,
              private authSvc: AuthKeycloakService,
              private configSvc: ConfigurationsService,
              private domSanitizer: DomSanitizer) {
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.landingLogo,
      )
      this.logo = this.configSvc.instanceConfig.logos.company
      this.bottomLogo = '/assets/instances/Wingspan-Pathfinders/app_logos/InfosysFoundationUSA.png'
    }
  }

  ngOnInit() {
    this.subscriptionLogin = this.activateRoute.data.subscribe(data => {
      // todo
      this.loginConfig = data.pageData.data
      this.isClientLogin = data.pageData.data.isClient

    })

    const paramsMap = this.activateRoute.snapshot.queryParamMap
    if (paramsMap.has('ref')) {
      this.redirectUrl = document.baseURI + paramsMap.get('ref')
    } else {
      this.redirectUrl = document.baseURI
    }
  }

  ngOnDestroy() {
    if (this.subscriptionLogin) {
      this.subscriptionLogin.unsubscribe()
    }
  }
  login(key: 'E' | 'N' | 'S' | 'siemens-entitlement') {
    this.authSvc.login(key, this.redirectUrl)
  }

  register() {
    this.authSvc.register(this.redirectUrl)
  }

}
