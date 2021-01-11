import { Component, OnDestroy, OnInit } from '@angular/core'
import { ActivatedRoute, NavigationEnd, Router, Event, NavigationError } from '@angular/router'
import { NsWidgetResolver } from 'library/ws-widget/resolver/src/public-api'

@Component({
  selector: 'ws-app-events-home',
  templateUrl: './events-home.component.html',
  styleUrls: ['./events-home.component.scss'],
})
export class EventsHomeComponent implements OnInit, OnDestroy {
  titles = [{ title: 'EVENTS', url: '/app/event-hub/home', icon: 'calendar' }]
  banner!: NsWidgetResolver.IWidgetData<any>
  currentRoute = 'home'
  private bannerSubscription: any
  constructor(private route: ActivatedRoute, private router: Router) {
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        // Hide loading indicator
        // console.log(event.url)
        this.bindUrl(event.urlAfterRedirects.replace('/app/event-hub/', ''))
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
