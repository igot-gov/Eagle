import { Component, OnInit } from '@angular/core'
import {
  ConfigurationsService,
  NsPage,
} from '../../../../../../../../../library/ws-widget/utils/src/public-api'
import { ActivatedRoute, Data } from '@angular/router'
import { Subscription } from 'rxjs'
import { ILearningPath } from '../../models/academy-learning-paths.model'
import { WidgetUserService } from '../../../../../../../../../library/ws-widget/collection/src/lib/_services/widget-user.service'
import { IUserGroupDetails } from '../../../../../../../../../library/ws-widget/collection/src/lib/_services/widget-user.model'

@Component({
  selector: 'ws-app-learning-path-home',
  templateUrl: './learning-path-home.component.html',
  styleUrls: ['./learning-path-home.component.scss'],
})
export class LearningPathHomeComponent implements OnInit {
  routeSubscription: Subscription | null = null
  learningPaths: ILearningPath[] | null = null
  defaultThumbnail = ''
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  userGroups: IUserGroupDetails[] | null = null

  constructor(
    public configSvc: ConfigurationsService,
    private route: ActivatedRoute,
    private userSvc: WidgetUserService,
  ) {}

  ngOnInit() {
    if (this.route.parent) {
      this.routeSubscription = this.route.parent.data.subscribe((data: Data) => {
        this.learningPaths = data.pageData.data.learningPaths
      })
    }
    if (this.configSvc.userProfile) {
      this.userSvc
        .fetchUserGroupDetails(this.configSvc.userProfile.userId)
        .subscribe((data: IUserGroupDetails[]) => {
          this.userGroups = data
        })
    }

    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
  }

  checkAccess(group: number): boolean {
    if (this.userGroups) {
      const groups = this.userGroups.map((g: IUserGroupDetails) => g.group_id)
      return groups.includes(group)
    }
    return false
  }
}
