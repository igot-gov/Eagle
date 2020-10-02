import { Component, OnDestroy, OnInit } from '@angular/core'
import { ActivatedRoute, NavigationEnd, Router, Event, NavigationError } from '@angular/router'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'

@Component({
  selector: 'ws-app-careers-home',
  templateUrl: './careers-home.component.html',
  styleUrls: ['./careers-home.component.scss'],
})
export class CareersHomeComponent implements OnInit, OnDestroy {
  titles = [{ title: 'CAREER', url: '/app/careers/home', icon: 'work' }]
  banner!: NsWidgetResolver.IWidgetData<any>
  currentRoute = 'home'
  private bannerSubscription: any
  constructor(private route: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        // Hide loading indicator
        // console.log(event.url)
        this.bindUrl(event.urlAfterRedirects.replace('/app/careers/', ''))
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
  }
  bindUrl(path: string) {
    if (path) {
      this.currentRoute = path
    }
  }

  ngOnDestroy() {
    if (this.bannerSubscription) {
      this.bannerSubscription.unsubscribe()
    }
  }
}
