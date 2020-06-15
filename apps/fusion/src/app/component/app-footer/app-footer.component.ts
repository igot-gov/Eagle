import { Component } from '@angular/core'
import { ConfigurationsService, ValueService } from '@ws-widget/utils'

@Component({
  selector: 'ws-app-footer',
  templateUrl: './app-footer.component.html',
  styleUrls: ['./app-footer.component.scss'],
})
export class AppFooterComponent {

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
  }

}
