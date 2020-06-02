import { Component, OnInit, OnDestroy } from '@angular/core'
import { SafeUrl, DomSanitizer } from '@angular/platform-browser'
import { ILoginDescriptiveFooterConfig } from '../login/login.model'
import { Subscription } from 'rxjs'
import { ActivatedRoute } from '@angular/router'
import {
  AuthKeycloakService,
  ConfigurationsService,
} from '../../../../library/ws-widget/utils/src/public-api'
import { MatDialog } from '@angular/material'
import { DialogUserDetailsComponent } from '../dialog-user-details/dialog-user-details.component'
import { IWSPublicLoginConfigEpoch } from './login.model'

@Component({
  selector: 'ws-epoch-login',
  templateUrl: './epoch-login.component.html',
  styleUrls: ['./epoch-login.component.scss'],
})
export class EpochLoginComponent implements OnInit, OnDestroy {
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
  loginConfig: IWSPublicLoginConfigEpoch | null = null
  welcomeFooter: ILoginDescriptiveFooterConfig | null = null
  title = ''
  subTitle = ''
  appName = ''
  private redirectUrl = ''
  private subscriptionLogin: Subscription | null = null
  backgroundImageUrl: SafeUrl = ''

  constructor(
    public dialog: MatDialog,
    private activateRoute: ActivatedRoute,
    private authSvc: AuthKeycloakService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
  ) {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(instanceConfig.logos.company)
      this.appName = instanceConfig.details.appName
    }
  }

  ngOnInit() {
    localStorage.removeItem('uuid')
    this.subscriptionLogin = this.activateRoute.data.subscribe(data => {
      // todo
      this.loginConfig = data.pageData.data
      this.androidIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        data.pageData.data.footer.android,
      )
      this.iosIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(data.pageData.data.footer.ios)
      if (this.loginConfig && this.loginConfig.bodyBackgroundImageUrl) {
        this.backgroundImageUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(
          this.loginConfig.bodyBackgroundImageUrl,
        )
      }
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

  login(key: 'E' | 'N' | 'S' | 'siemens-entitlement' | 'NSH') {
    if (key === 'NSH') {
      this.dialog.open(DialogUserDetailsComponent, {
        width: '450px',
      })
    } else {
      this.authSvc.login(key, this.redirectUrl)
    }
  }
}
