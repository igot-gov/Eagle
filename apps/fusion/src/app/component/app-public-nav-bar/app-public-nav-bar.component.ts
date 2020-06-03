import { Component, OnInit } from '@angular/core'
import { DomSanitizer, SafeUrl } from '@angular/platform-browser'
import { ConfigurationsService, EInstance, NsPage } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-public-nav-bar',
  templateUrl: './app-public-nav-bar.component.html',
  styleUrls: ['./app-public-nav-bar.component.scss'],
})
export class AppPublicNavBarComponent implements OnInit {
  appIcon: SafeUrl | null = null
  logo = ''
  appName = ''
  navBar: Partial<NsPage.INavBackground> | null = null
  constructor(private domSanitizer: DomSanitizer, private configSvc: ConfigurationsService) {}

  public get showPublicNavbar(): boolean {
    // commercial_begin
    switch (this.configSvc.rootOrg) {
      case EInstance.PATHFINDERS:
        return false
      case EInstance.FORD:
        return false
      case EInstance.EPOCH:
        return false
      case EInstance.ACADEMY:
        return false
      case EInstance.ASSISTEDGE:
        return false
      default:
        return true
    }
    // commercial_end

    // opensource_begin
    // return true
    // opensource_end
  }

  ngOnInit() {
    if (this.configSvc.instanceConfig) {
      this.appIcon = this.domSanitizer.bypassSecurityTrustResourceUrl(
        this.configSvc.instanceConfig.logos.appTransparent,
      )
      this.appName = this.configSvc.instanceConfig.details.appName
      this.navBar = this.configSvc.primaryNavBar
    }
  }
}
