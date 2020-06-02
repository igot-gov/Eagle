import { Component, OnInit, OnDestroy } from '@angular/core'
import { NsPage, ValueService, ConfigurationsService } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { map } from 'rxjs/operators'
import { ActivatedRoute, ParamMap } from '@angular/router'

@Component({
  selector: 'ws-app-skill-management',
  templateUrl: './skill-management.component.html',
  styleUrls: ['./skill-management.component.scss'],
})
export class SkillManagementComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  isLtMedium$ = this.valueSvc.isLtMedium$
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  screenSizeIsLtMedium = false
  currentTab = ''
  tabs = ['add-skill', 'created-skills', 'all-skills', 'add-learning-path']
  paramSubscription: Subscription | null = null
  constructor(
    private valueSvc: ValueService,
    private configSvc: ConfigurationsService,
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })

    this.paramSubscription = this.route.paramMap.subscribe((params: ParamMap) => {
      let tab = params.get('tab')
      if (tab) {
        if (this.tabs.indexOf(tab) === -1) {
          tab = 'add-skill'
        }
        this.currentTab = tab
      }
    })
  }
  sideNavOnClick() {
    // this.currentTab = tab
    if (this.screenSizeIsLtMedium) {
      this.sideNavBarOpened = !this.sideNavBarOpened
    }
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
}
