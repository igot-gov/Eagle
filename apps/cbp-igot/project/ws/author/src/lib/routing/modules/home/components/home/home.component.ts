import { Component, OnInit, OnDestroy } from '@angular/core'
import { map } from 'rxjs/operators'
import { ValueService } from '@ws-widget/utils/src/public-api'
import { AccessControlService } from '@ws/author/src/lib/modules/shared/services/access-control.service'
import { REVIEW_ROLE, PUBLISH_ROLE, CREATE_ROLE } from '@ws/author/src/lib/constants/content-role'
import { ILeftMenu } from '@ws-widget/collection'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */

@Component({
  selector: 'ws-auth-root-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class AuthHomeComponent implements OnInit, OnDestroy {
  sideNavBarOpened = true
  panelOpenState = false
  allowReview = false
  allowAuthor = false
  allowRedo = false
  allowPublish = false
  allowExpiry = false
  allowRestore = false
  isNewDesign = false
  isLtMedium$ = this.valueSvc.isLtMedium$
  private defaultSideNavBarOpenedSubscription: any
  mode$ = this.isLtMedium$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  public screenSizeIsLtMedium = false
  leftmenues!: ILeftMenu[]
  constructor(private valueSvc: ValueService, private accessService: AccessControlService
  ) {
  }

  ngOnInit() {
    this.allowAuthor = this.canShow('author')
    this.allowRedo = _.get(this.accessService, 'authoringConfig.allowRedo')
    this.allowRestore = _.get(this.accessService, 'authoringConfig.allowRestore')
    this.allowExpiry = _.get(this.accessService, 'authoringConfig.allowExpiry')
    this.allowReview = this.canShow('review') && _.get(this.accessService, 'authoringConfig.allowReview')
    this.allowPublish = this.canShow('publish') && _.get(this.accessService, 'authoringConfig.allowPublish')
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe(isLtMedium => {
      this.sideNavBarOpened = !isLtMedium
      this.screenSizeIsLtMedium = isLtMedium
    })
    this.isNewDesign = _.get(this.accessService, 'authoringConfig.newDesign')
    // this.leftmenues = this.initSvc.authAdditionalConfig.menus
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }

  canShow(role: string): boolean {
    switch (role) {
      case 'review':
        return this.accessService.hasRole(REVIEW_ROLE)
      case 'publish':
        return this.accessService.hasRole(PUBLISH_ROLE)
      case 'author':
        return this.accessService.hasRole(CREATE_ROLE)
      default:
        return false
    }
  }
}
