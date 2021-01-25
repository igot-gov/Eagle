import { Component, OnInit, OnDestroy } from '@angular/core'
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
export class HomeComponent implements OnInit, OnDestroy {
  currentRoute = 'users'
  widgetData!: NsWidgetResolver.IWidgetData<ILeftMenu>
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any
  public screenSizeIsLtMedium = false
  sideNavBarOpened = true
  mydept!: string
  role: any
  dept!: string
  constructor(private valueSvc: ValueService, private router: Router, private activeRoute: ActivatedRoute) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.bindUrl(event.urlAfterRedirects.replace('/app/roles-access/', ''))
        this.widgetData = this.activeRoute.snapshot.data &&
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

    const url = this.router.url.split('/')
    const dept = this.router.url.split('=')
    if (dept[1]) {
      const nxt = dept[1].split(';')
      const re = /\%20/gi
      if (nxt[0] === 'true' || nxt[0].includes('%')) {
        this.dept = url[3].replace(re, ' ')
      } else {
        this.dept = nxt[0].replace(re, ' ')
      }
      if (dept[3]) {
        this.mydept = dept[3].replace(re, ' ')
      } else {
        this.mydept = 'Basic Information'
      }

      this.role = url[url.length - 2]
    }
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
