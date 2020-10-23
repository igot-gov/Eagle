import { Component, OnInit, OnDestroy } from '@angular/core'
import { Router, Event, NavigationEnd, NavigationError, ActivatedRoute } from '@angular/router'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'app-competence',
  templateUrl: './competence.component.html',
  styleUrls: ['./competence.component.scss'],
  /* tslint:disable */
  host: { class: 'margin-top-l' },
  /* tslint:enable */
})
export class CompetenceComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  panelOpenState = false
  titles = [{ title: 'Competencies', url: '/page/competencies', icon: 'extension' }]
  unread = 0
  currentRoute = 'home'
  banner!: NsWidgetResolver.IWidgetData<any>
  private bannerSubscription: any
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  userRouteName = ''
  private defaultSideNavBarOpenedSubscription: any
  constructor(
    private valueSvc: ValueService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        this.bindUrl(event.url.replace('/app/competencies/', ''))
      }
      if (event instanceof NavigationError) {

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

  bindUrl(path: string) {
    if (path) {
      // this.currentRoute = path
      // if (this.titles.length > 1) {
      // this.titles.pop()
      // }
      // switch (path) {
      //   case 'home':
      //     this.titles.push({ title: 'Discussion', icon: '', url: 'none' })
      //     break
      //   case 'categories':
      //     this.titles.push({ title: 'Categories', icon: '', url: 'none' })
      //     break
      //   case 'tags':
      //     this.titles.push({ title: 'Tags', icon: '', url: 'none' })
      //     break
      //   case 'leaderboard':
      //     this.titles.push({ title: 'Leaderboard', icon: '', url: 'none' })
      //     break
      //   case 'my-discussions':
      //     this.titles.push({ title: 'My Discussions', icon: '', url: 'none' })
      //     break

      //   default:
      //     break
      // }
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
