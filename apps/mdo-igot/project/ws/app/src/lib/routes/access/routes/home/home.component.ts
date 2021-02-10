import { Component, OnInit, OnDestroy } from '@angular/core'
import { Router, Event, NavigationEnd, ActivatedRoute } from '@angular/router'
import { ILeftMenuWithoutLogo } from '@ws-widget/collection'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
import { ConfigurationsService, ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  currentRoute = 'roles-access'
  myRoles!: Set<string>
  widgetData!: NsWidgetResolver.IWidgetData<ILeftMenuWithoutLogo>
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any
  public screenSizeIsLtMedium = false
  sideNavBarOpened = true
  role: any
  constructor(private valueSvc: ValueService,
              private router: Router,
              private activeRoute: ActivatedRoute,
              private configService: ConfigurationsService
  ) {
    if (this.configService.userRoles) {
      this.myRoles = this.configService.userRoles
    }

    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.bindUrl(event.urlAfterRedirects.replace('/app/roles-access/', ''))
        if (this.activeRoute.snapshot.data.department.data) {
          const leftData = this.activeRoute.snapshot.data.pageData.data.menus
          _.set(leftData, 'widgetData.logo', true)
          _.set(leftData, 'widgetData.logoPath', _.get(this.activeRoute, 'snapshot.data.department.data.logo'))
          _.set(leftData, 'widgetData.name', _.get(this.activeRoute, 'snapshot.data.department.data.deptName')
            || _.get(this.activeRoute, 'snapshot.data.department.data.description'))
          _.set(leftData, 'widgetData.userRoles', this.myRoles)
          this.widgetData = leftData
        } else {
          this.widgetData = this.activeRoute.snapshot.data.pageData.data.menus
        }
      }
    })
  }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
    const url = this.router.url.split('/')
    this.role = url[url.length - 2]
    this.role = this.role.replace(/%20/g, ' ')
  }

  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }

  bindUrl(path: string) {
    if (path) {
      this.currentRoute = path
    }
  }
}
