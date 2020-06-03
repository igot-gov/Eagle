import { Component, OnInit } from '@angular/core'
import {
  ConfigurationsService,
  AuthKeycloakService,
} from '../../../../library/ws-widget/utils/src/public-api'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-assistedge-login',
  templateUrl: './assistedge-login.component.html',
  styleUrls: ['./assistedge-login.component.scss'],
})
export class AssistedgeLoginComponent implements OnInit {
  appIcon: SafeUrl | null = null
  contactUsMail = ''
  private redirectUrl = ''
  constructor(
    private activateRoute: ActivatedRoute,
    private authSvc: AuthKeycloakService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
  ) {}

  ngOnInit() {
    const paramsMap = this.activateRoute.snapshot.queryParamMap
    if (paramsMap.has('ref')) {
      this.redirectUrl = document.baseURI + paramsMap.get('ref')
    } else {
      this.redirectUrl = document.baseURI
    }
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.landingLogo,
      )
      this.contactUsMail = this.configSvc.instanceConfig.mailIds.contactUs
    }
  }

  login(key: 'E' | 'N' | 'S' | 'siemens-entitlement') {
    this.authSvc.login(key, this.redirectUrl)
  }
}
