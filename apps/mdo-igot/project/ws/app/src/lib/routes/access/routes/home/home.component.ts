import { Component, OnInit } from '@angular/core'
import { Router, Event, NavigationEnd, NavigationError, ActivatedRoute } from '@angular/router'
import { ILeftMenu } from '@ws-widget/collection'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'

@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  currentRoute = 'roles-access'
  WidgetData!: NsWidgetResolver.IWidgetData<ILeftMenu>
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any
  public screenSizeIsLtMedium = false
  sideNavBarOpened = true

  constructor(private valueSvc: ValueService, private router: Router, private activeRoute: ActivatedRoute) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.bindUrl(event.urlAfterRedirects.replace('/app/home/', ''))
        this.WidgetData = this.activeRoute.snapshot.data &&
          this.activeRoute.snapshot.data.pageData.data.menus || []
      }

      if (event instanceof NavigationError) {

      }
    })
  }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
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
