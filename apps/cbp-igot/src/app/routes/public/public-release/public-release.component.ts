import { Component, OnInit, OnDestroy } from '@angular/core'
import { Subscription } from 'rxjs'
import { ActivatedRoute } from '@angular/router'
import { IReleaseNotes } from '@ws-widget/collection'

@Component({
  selector: 'ws-public-release',
  templateUrl: './public-release.component.html',
  styleUrls: ['./public-release.component.scss'],
})
export class PublicReleaseComponent implements OnInit, OnDestroy {
  releasePage: IReleaseNotes | null = null
  private subscriptionRelease: Subscription | null = null

  // isSmallScreen$ = this.breakpointObserver
  //   .observe(Breakpoints.XSmall)
  //   .pipe(map(breakPointState => breakPointState.matches))
  constructor(
    // private breakpointObserver: BreakpointObserver,
    // private domSanitizer: DomSanitizer,
    // private configSvc: ConfigurationsService,
    private activateRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.subscriptionRelease = this.activateRoute.data.subscribe(data => {
      this.releasePage = data.pageData.data
    })
  }

  ngOnDestroy() {
    if (this.subscriptionRelease) {
      this.subscriptionRelease.unsubscribe()
    }
  }
}
