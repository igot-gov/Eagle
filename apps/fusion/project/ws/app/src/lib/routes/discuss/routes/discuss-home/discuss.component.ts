
import { Component, OnInit, OnDestroy } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
@Component({
  selector: 'app-discuss',
  templateUrl: './discuss.component.html',
  styleUrls: ['./discuss.component.scss'],
})
export class DiscussComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  panelOpenState = false
  unread = 0
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any

  constructor(private valueSvc: ValueService, private route: ActivatedRoute) {
    this.unread = this.route.snapshot.data.unread
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
}
