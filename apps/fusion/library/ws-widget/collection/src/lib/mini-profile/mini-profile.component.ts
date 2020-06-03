import { Component, OnInit, Inject } from '@angular/core'
import { WidgetBaseComponent } from '@ws-widget/resolver'
import { MAT_DIALOG_DATA } from '@angular/material/dialog'
import { UserMiniProfileService } from './user-mini-profile.service'
import { NsMiniProfile } from './mini-profile.model'
import { WidgetContentService } from '../_services/widget-content.service'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
// import { DomSanitizer } from @angular/platform-browser;
import { NSSearch } from '../_services/widget-search.model'
@Component({
  selector: 'ws-widget-mini-profile',
  templateUrl: './mini-profile.component.html',
  styleUrls: ['./mini-profile.component.scss'],
})
export class MiniProfileComponent extends WidgetBaseComponent implements OnInit {
  alumni: NsMiniProfile.IMiniProfileData | null = null
  fetchStatus: TFetchStatus = 'fetching'
  // @Input() widgetData!: IMiniProfileDdata
  location: string | null = null
  fetchUploadedContents: TFetchStatus = 'fetching'
  searchRequest: any
  resultsDisplayType: 'basic' | 'advanced' = 'advanced'
  emailId = ''
  userGroupDetails: any
  defaultThumbnail = ''

  constructor(@Inject(MAT_DIALOG_DATA) public data: string,
              private configSvc: ConfigurationsService,
              public userMiniProvileSvc: UserMiniProfileService,
              private contentSvc: WidgetContentService,
  ) {
    super()

  }

  ngOnInit() {
    this.userProfileDetails(this.data)
    this.search(this.data)
    this.fetchUserGroupDetails(this.data)
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }

  }
  fetchUserGroupDetails(userId: string) {
    this.userMiniProvileSvc.fetchGroupDetails(userId).subscribe(
      data => {
        if (data) {
          this.userGroupDetails = data

        }
      },
    )

  }
  userProfileDetails(wid: string) {

    this.alumni = null
    this.emailId = ''
    this.userMiniProvileSvc.viewMiniProfile(wid).subscribe(
      data => {
        this.alumni = data
        this.emailId = data.email

        if (this.alumni) {
          this.fetchStatus = 'done'

          if (this.alumni.organization && !this.alumni.organization_privacy
            && this.alumni.teaching_state && !this.alumni.teaching_state_privacy) {
            this.location = `${this.alumni.organization}, ${this.alumni.teaching_state}`
          } else if (this.alumni.organization && !this.alumni.organization_privacy) {
            this.location = this.alumni.organization
          } else if (this.alumni.teaching_state && !this.alumni.teaching_state_privacy) {
            this.location = this.alumni.teaching_state
          } else {
            this.location = null
          }

        }
      },
      _ => {
        this.fetchStatus = 'error'
      },
    )

  }
  search(wid: string) {

    const req: NSSearch.ISearchRequest = {
      query: '',
      filters: {

        creatorContacts: [
          wid,
        ],
      },

      pageNo: 0,
      sort: [
        {
          lastUpdatedOn: 'desc',
        },
      ],
      pageSize: 24,
      uuid: wid,
      rootOrg: 'Pathfinders',
    }
    this.contentSvc.search(req).subscribe(
      response => {
        if (response) {
          this.fetchUploadedContents = 'done'
          this.searchRequest = response
        }

      },
      _ => {
        this.fetchUploadedContents = 'error'
      })
  }
}
