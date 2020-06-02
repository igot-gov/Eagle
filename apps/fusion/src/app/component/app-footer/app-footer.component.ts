import { Component } from '@angular/core'
import { ConfigurationsService, ValueService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-footer',
  templateUrl: './app-footer.component.html',
  styleUrls: ['./app-footer.component.scss'],
})
export class AppFooterComponent {

  isSiemensContributionShown = false
  isFordFooter = false
  isXSmall = false
  termsOfUser = true

  constructor(
    private configSvc: ConfigurationsService,
    private valueSvc: ValueService
  ) {
    if (this.configSvc.restrictedFeatures) {
      if (this.configSvc.restrictedFeatures.has('termsOfUser')) {
        this.termsOfUser = false
      }
    }
    this.valueSvc.isXSmall$.subscribe(isXSmall => {
      this.isXSmall = isXSmall
    })
    if (this.configSvc.instanceConfig) {
      if (this.configSvc.instanceConfig.rootOrg === 'Siemens') {
        this.isSiemensContributionShown = true
      } else {
        this.isSiemensContributionShown = false
      }
      if (this.configSvc.instanceConfig.rootOrg === 'Ford') {
        this.isFordFooter = true
      } else {
        this.isFordFooter = false
      }
    }
  }

}
