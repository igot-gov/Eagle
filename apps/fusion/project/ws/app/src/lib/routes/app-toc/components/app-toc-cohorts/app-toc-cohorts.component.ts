import { Component, Input, OnInit } from '@angular/core'
import { NsContent, NsAutoComplete } from '@ws-widget/collection'
import { ConfigurationsService } from '@ws-widget/utils'
import { NsCohorts } from '../../models/app-toc.model'
import { AppTocService } from '../../services/app-toc.service'
import { Router } from '@angular/router'

@Component({
  selector: 'ws-app-toc-cohorts',
  templateUrl: './app-toc-cohorts.component.html',
  styleUrls: ['./app-toc-cohorts.component.scss'],
})
export class AppTocCohortsComponent implements OnInit {
  @Input() content!: NsContent.IContent
  cohortResults: {
    [key: string]: { hasError: boolean; contents: NsCohorts.ICohortsContent[] }
  } = {}
  cohortTypesEnum = NsCohorts.ECohortTypes
  @Input() forPreview = false

  constructor(
    private tocSvc: AppTocService,
    private configSvc: ConfigurationsService,
    private router: Router,
  ) { }

  ngOnInit() { }

  public get enableFeature(): boolean {
    if (this.configSvc.restrictedFeatures) {
      return !this.configSvc.restrictedFeatures.has('cohorts')
    }
    return false
  }

  public get enablePeopleSearch(): boolean {
    if (this.configSvc.restrictedFeatures) {
      return !this.configSvc.restrictedFeatures.has('peopleSearch')
    }
    return false
  }

  goToUserProfile(user: NsAutoComplete.IUserAutoComplete) {
    if (this.enablePeopleSearch) {
      this.router.navigate(['/app/person-profile', user.wid])
      // this.router.navigate(['/app/person-profile'], { queryParams: { emailId: user.email } })
    }
  }

  getUserFullName(user: any) {
    if (user && user.first_name && user.last_name) {
      return `${user.first_name.trim()} ${user.last_name.trim()}`
    }
    return ''
  }

  fetchCohorts(cohortType: NsCohorts.ECohortTypes) {
    if (!this.cohortResults[cohortType] && !this.forPreview) {
      this.tocSvc.fetchContentCohorts(cohortType, this.content.identifier).subscribe(
        data => {
          this.cohortResults[cohortType] = {
            contents: data || [],
            hasError: false,
          }
        },
        () => {
          this.cohortResults[cohortType] = {
            contents: [],
            hasError: true,
          }
        },
      )
    } else if (this.cohortResults[cohortType] && !this.forPreview) {
      return
    } else {
      this.cohortResults[cohortType] = {
        contents: [],
        hasError: false,
      }
    }
  }
}
