import { Component, Input, OnDestroy, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { NsWidgetResolver, WidgetBaseComponent } from '@ws-widget/resolver'
import { ConfigurationsService, NsInstanceConfig, ValueService } from '@ws-widget/utils/src/public-api'
import { Subscription } from 'rxjs'
@Component({
  selector: 'ws-widget-card-hubs-list',
  templateUrl: './card-hubs-list.component.html',
  styleUrls: ['./card-hubs-list.component.scss'],
})
export class CardHubsListComponent extends WidgetBaseComponent

  implements OnInit, OnDestroy, NsWidgetResolver.IWidgetData<any> {
  private defaultMenuSubscribe: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  enableFeature = true
  enablePeopleSearch = true
  @Input() widgetData: any
  givenName: string | undefined
  userEmail: string | undefined
  keyTag: string[] = []
  newUserReq: any
  deptUserReq: any
  nameFilter = ''
  visible = false
  searchSpinner = false
  isMobile = false

  constructor(private configSvc: ConfigurationsService, private router: Router, private valueSvc: ValueService) {
    super()
  }

  hubsList!: NsInstanceConfig.IHubs[]

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.hubsList = (instanceConfig.hubs || []).filter(i => i.active)
    }
    this.defaultMenuSubscribe = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.isMobile = isLtMedium
    })
  }
  ngOnDestroy() {
    if (this.defaultMenuSubscribe) {
      this.defaultMenuSubscribe.unsubscribe()
    }
  }
  getUserFullName(user: any) {
    if (user && user.personalDetails.firstname && user.personalDetails.surname) {
      return `${user.personalDetails.firstname.trim()} ${user.personalDetails.surname.trim()}`
    }
    return ''
  }
  getSearchProfileUserFullName(user: any) {
    if (user && user.first_name && user.last_name) {
      return `${user.first_name.trim()} ${user.last_name.trim()}`
    }
    return ''
  }
  goToUserProfile(user: any) {
    this.router.navigate(['/app/person-profile', user.userId])
    // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: user.personalDetails.primaryEmail } })
  }
  searchUser() {

    if (this.nameFilter.length === 0) {
      this.enableFeature = true
    } else {
      this.searchSpinner = true
      this.enableFeature = false

    }

  }
  toggleVisibility() {
    this.visible = !this.visible
  }
}
