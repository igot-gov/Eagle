
import { Component, OnInit, OnDestroy } from '@angular/core'
import { ActivatedRoute, Router, Event, NavigationEnd, NavigationError } from '@angular/router'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
@Component({
  selector: 'app-discuss',
  templateUrl: './discuss.component.html',
  styleUrls: ['./discuss.component.scss'],
})
export class DiscussComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  panelOpenState = false
  titles = [{ title: 'DISCUSS', url: '/app/discuss/home', icon: 'forum' }]
  unread = 0
  currentRoute = 'home'
  banner!: NsWidgetResolver.IWidgetData<any>
  private bannerSubscription: any
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any

  constructor(private valueSvc: ValueService, private route: ActivatedRoute, private router: Router) {
    this.unread = this.route.snapshot.data.unread
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
    this.bannerSubscription = this.route.data.subscribe(data => {
      if (data && data.pageData) {
        this.banner = data.pageData.data.banner || []
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
    if (this.bannerSubscription) {
      this.bannerSubscription.unsubscribe()
    }
  }
  bindUrl(path: string) {
    if (path) {
      this.currentRoute = path
      if (this.titles.length > 1) {
        this.titles.pop()
      }
      switch (path) {
        case 'home':
          this.titles.push({ title: 'Discussion', icon: '', url: 'none' })
          break
        case 'categories':
          this.titles.push({ title: 'Categories', icon: '', url: 'none' })
          break
        case 'tags':
          this.titles.push({ title: 'Tags', icon: '', url: 'none' })
          break
        case 'leaderboard':
          this.titles.push({ title: 'Leaderboard', icon: '', url: 'none' })
          break
        case 'my-discussions':
          this.titles.push({ title: 'My Discussions', icon: '', url: 'none' })
          break

        default:
          break
      }
    }
  }
}
