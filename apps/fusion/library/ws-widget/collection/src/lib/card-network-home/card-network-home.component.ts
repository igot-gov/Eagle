import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-widget-card-home-network',
  templateUrl: './card-network-home.component.html',
  styleUrls: ['./card-network-home.component.scss'],
})
export class CardNetworkHomeComponent extends WidgetBaseComponent

  implements OnInit, NsWidgetResolver.IWidgetData<any> {
  @Input() widgetData: any
  networkUser!: any
  constructor(private router: Router, public configurationsService: ConfigurationsService) {
    super()
  }

  ngOnInit() {
    if (this.widgetData && this.widgetData.content) {
      this.networkUser = this.widgetData.content
    }
  }

  getUserFullName(user: any) {
    if (user && user.personalDetails.firstname && user.personalDetails.surname) {
      return `${user.personalDetails.firstname.trim()} ${user.personalDetails.surname.trim()}`
    }
    return ''
  }

  goToUserProfile(user: any) {
    this.router.navigate([`/app/person-profile`, user.id])
  }
}
