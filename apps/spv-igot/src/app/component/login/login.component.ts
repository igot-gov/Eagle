import { Component, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'
import { AuthKeycloakService, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { ILoginDescriptiveFooterConfig, IWSPublicLoginConfig } from './login.model'

@Component({
  selector: 'ws-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  objectKeys = Object.keys
  productLogo = ''
  contactUs = false
  productLogoWidth: string | undefined = ''
  showIconBackground = false
  developedBy = ''
  appIcon: SafeUrl | null = null
  // todo what to do for client login
  isClientLogin = false
  loginConfig: IWSPublicLoginConfig | null = null
  welcomeFooter: ILoginDescriptiveFooterConfig | null = null
  title = ''
  subTitle = ''
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
        instanceConfig.logos.appTransparent,
      )
      this.productLogo = instanceConfig.logos.company
      this.developedBy = instanceConfig.logos.developedBy
    }
  }

  ngOnInit() {
    this.subscriptionLogin = this.activateRoute.data.subscribe(data => {
      // todo
      this.loginConfig = data.pageData.data
      this.isClientLogin = data.pageData.data.isClient
      this.welcomeFooter = data.pageData.data.footer.descriptiveFooter
      this.title = data.pageData.data.topbar.title
      this.subTitle = data.pageData.data.topbar.subTitle
      this.contactUs = data.pageData.data.footer.contactUs
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

  login(key: 'E' | 'N' | 'S') {
    this.authSvc.login(key, this.redirectUrl)
  }
}
