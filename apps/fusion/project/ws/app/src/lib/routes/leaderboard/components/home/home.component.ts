import { Component, OnInit } from '@angular/core'
import { Router, ActivatedRoute } from '@angular/router'
import { EDurationTypeRouteParam } from '../../models/leaderboard.model'

@Component({
  selector: 'ws-app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  tabs: EDurationTypeRouteParam[]
  activeTab: EDurationTypeRouteParam
  canShow: false | undefined

  constructor(private router: Router, private route: ActivatedRoute) {
    this.tabs = [
      EDurationTypeRouteParam.Weekly,
      EDurationTypeRouteParam.Monthly,
      EDurationTypeRouteParam.HallOfFame,
    ]
    this.activeTab = EDurationTypeRouteParam.Weekly
  }

  ngOnInit() {
    this.canShow = false
    this.route.children[0].url.subscribe(url => {
      this.activeTab =
        (url[url.length - 1].path as EDurationTypeRouteParam) || EDurationTypeRouteParam.Weekly
    })
  }

  routeToTab(tab: string) {
    this.activeTab = tab as EDurationTypeRouteParam
    this.router.navigate([`${tab}`], {
      relativeTo: this.route,
    })
  }
}
