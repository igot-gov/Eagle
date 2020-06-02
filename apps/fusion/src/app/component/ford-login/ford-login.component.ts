import { Component, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'
import { Subscription } from 'rxjs'
import { AuthKeycloakService, ConfigurationsService, NsPage } from '../../../../library/ws-widget/utils/src/public-api'
import { IWSPublicLoginConfig } from '../login/login.model'

@Component({
  selector: 'ws-ford-login',
  templateUrl: './ford-login.component.html',
  styleUrls: ['./ford-login.component.scss'],
})
export class FordLoginComponent implements OnInit, OnDestroy {
  appIcon: SafeUrl | null = null
  logo = ''

  navBar: Partial<NsPage.INavBackground> | null = null
  isClientLogin = false
  loginConfig: IWSPublicLoginConfig | null = null
  private redirectUrl = ''
  private subscriptionLogin: Subscription | null = null

  constructor(
    private activateRoute: ActivatedRoute,
    private authSvc: AuthKeycloakService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
  ) {

  }

  ngOnInit() {
    this.subscriptionLogin = this.activateRoute.data.subscribe(data => {
      // todo
      this.loginConfig = data.pageData.data
      this.isClientLogin = data.pageData.data.isClient
    })
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.landingLogo,
      )
      this.logo = this.configSvc.instanceConfig.logos.company
      this.navBar = this.configSvc.primaryNavBar
    }

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
}
