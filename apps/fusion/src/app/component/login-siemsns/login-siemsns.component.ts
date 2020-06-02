import { Component, OnInit, OnDestroy } from '@angular/core'
import { SafeUrl, DomSanitizer } from '@angular/platform-browser'
import { ILoginDescriptiveFooterConfig, IWSPublicLoginConfigSiemens } from '../login/login.model'
import { Subscription } from 'rxjs'
import { ActivatedRoute } from '@angular/router'
import { AuthKeycloakService, ConfigurationsService } from '../../../../library/ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-login-siemens',
  templateUrl: './login-siemsns.component.html',
  styleUrls: ['./login-siemsns.component.scss'],
})
export class LoginSiemensComponent implements OnInit, OnDestroy {
  objectKeys = Object.keys
  productLogo = ''
  contactUs = false
  productLogoWidth: string | undefined = ''
  showIconBackground = false
  developedBy = ''
  appIcon: SafeUrl | null = null
  androidIcon: SafeUrl | null = null
  iosIcon: SafeUrl | null = null
  // todo what to do for client login
  isClientLogin = false
  loginConfig: IWSPublicLoginConfigSiemens | null = null
  welcomeFooter: ILoginDescriptiveFooterConfig | null = null
  title = ''
  subTitle = ''
  appName = ''
  private redirectUrl = ''
  private subscriptionLogin: Subscription | null = null

  constructor(
    private activateRoute: ActivatedRoute,
    private authSvc: AuthKeycloakService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
  ) {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        instanceConfig.logos.company,
      )
      this.appName = instanceConfig.details.appName

    }
  }

  ngOnInit() {
    this.subscriptionLogin = this.activateRoute.data.subscribe(data => {
      // todo
      this.loginConfig = data.pageData.data
      this.androidIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        data.pageData.data.footer.android
      )
      this.iosIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        data.pageData.data.footer.ios
      )
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
}
