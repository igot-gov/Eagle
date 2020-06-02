import { Component, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'
import { AuthKeycloakService, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { IWSPublicLoginConfig } from './academy-login-model'
@Component({
  selector: 'ws-academy-login',
  templateUrl: './academy-login.component.html',
  styleUrls: ['./academy-login.component.scss'],
})
export class AcademyLoginComponent implements OnInit, OnDestroy {
  appIcon: SafeUrl | null = null
  mainIcon: SafeUrl | null = null
  isClientLogin = false
  loginConfig: IWSPublicLoginConfig | null = null
  private redirectUrl = ''
  private subscriptionLogin: Subscription | null = null
  constructor(
    private activateRoute: ActivatedRoute,
    private authSvc: AuthKeycloakService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer
  ) {
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.landingLogo,
      )
      this.mainIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        '/assets/instances/Wingspan-Academy/login-page/infosys-logo.svg',
      )
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
