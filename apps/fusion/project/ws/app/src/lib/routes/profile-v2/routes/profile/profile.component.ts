
import { Component, OnInit, OnDestroy } from '@angular/core'
import { Router, Event, NavigationEnd, NavigationError } from '@angular/router'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-profile-v2',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  /* tslint:disable */
  host: { class: 'margin-top-l' },
  /* tslint:enable */
})
export class ProfileComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  panelOpenState = false
  titles = [{ title: 'NETWORK', url: '/page/network', icon: 'group' }]
  unread = 0
  currentRoute = 'home'
  banner!: NsWidgetResolver.IWidgetData<any>
  private bannerSubscription: any
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any
  constructor(private valueSvc: ValueService, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        // Hide loading indicator
        // console.log(event.url)
        this.bindUrl(event.urlAfterRedirects.replace('/app/discuss/', ''))
      }

      if (event instanceof NavigationError) {
        // Hide loading indicator

        // Present error to user
        // console.log(event.error)
      }
    })
  }
  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
  }

  bindUrl(path: string) {
    if (path) {
      this.currentRoute = path
    }
  }

  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
    if (this.bannerSubscription) {
      this.bannerSubscription.unsubscribe()
    }
  }

}
