import { Component, OnInit, OnDestroy, Input } from '@angular/core'
// import { BreakpointObserver } from '@angular/cdk/layout'
// import { DomSanitizer } from '@angular/platform-browser'
// import { ConfigurationsService } from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { ActivatedRoute } from '@angular/router'
import { Subscription } from 'rxjs'
import { NSDiscussData } from '../../models/discuss.model'

@Component({
  selector: 'app-discuss-left-menu',
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.scss'],
})
export class LeftMenuComponent implements OnInit, OnDestroy {
  // tabs: any = []
  // tabs: any = []
  @Input() unseen = 0
  tabsData!: NSDiscussData.IDiscussJsonData
  private tabs: Subscription | null = null
  constructor(
    // private breakpointObserver: BreakpointObserver,
    // private domSanitizer: DomSanitizer,
    // private configSvc: ConfigurationsService,
    private activateRoute: ActivatedRoute,
  ) { }

  ngOnInit(): void {
    this.tabs = this.activateRoute.data.subscribe(data => {
      if (data && data.pageData) {
        this.tabsData = data.pageData.data.tabs || []

      }
    })
  }
  ngOnDestroy() {
    if (this.tabs) {
      this.tabs.unsubscribe()
    }
  }
}
