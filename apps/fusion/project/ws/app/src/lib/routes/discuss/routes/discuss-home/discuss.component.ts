
import { Component, OnInit, OnDestroy } from '@angular/core'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { map } from 'rxjs/operators'
import { NSDiscussData } from '../../models/discuss.model'
@Component({
  selector: 'app-discuss',
  templateUrl: './discuss.component.html',
  styleUrls: ['./discuss.component.scss'],
})
export class DiscussComponent implements OnInit, OnDestroy {
  TrendingTags: NSDiscussData.ITag[] = []
  sideNavBarOpened = true
  panelOpenState = false
  public screenSizeIsLtMedium = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  private defaultSideNavBarOpenedSubscription: any

  constructor(private valueSvc: ValueService) { }

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
