import { Component, OnDestroy, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils'
import { NSProfileDataV2 } from '../../models/profile-v2.model'

@Component({
  selector: 'app-profile-v2-right-menu',
  templateUrl: './right-menu.component.html',
  styleUrls: ['./right-menu.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuComponent implements OnInit, OnDestroy {
  completedPercent!: number
  badgesSubscription: any
  portalProfile!: NSProfileDataV2.IProfile
  badges!: NSProfileDataV2.IBadgeResponse
  currentUser!: any
  constructor(
    private route: ActivatedRoute,
    configSvc: ConfigurationsService,
  ) {
    this.currentUser = configSvc.userProfile && configSvc.userProfile.userId
    this.badgesSubscription = this.route.data.subscribe(response => {
      this.badges = response && response.badges && response.badges.data
      this.portalProfile = response.profile.data[0]
      this.completedPercent = this.calculatePercent(this.portalProfile || null)
    })
  }
  ngOnInit(): void {
    // this.completedPercent = 86
  }
  calculatePercent(profile: NSProfileDataV2.IProfile | null): number {
    let count = 20
    if (!profile) {
      return count
    }
    if (profile.academics && profile.academics[0] && profile.academics[0].nameOfQualification) {
      count += 11.43
    }
    if (profile.employmentDetails && profile.employmentDetails.departmentName) {
      count += 11.43
    }
    if (profile.personalDetails && profile.personalDetails.nationality) {
      count += 11.43
    }
    if (profile.photo) {
      count += 11.43
    }
    if (profile.professionalDetails && profile.professionalDetails[0] && profile.professionalDetails[0].designation) {
      count += 11.43
    }
    if (profile.skills && profile.skills[0] && profile.skills[0].additionalSkills) {
      count += 11.43
    }
    if (profile.interests && profile.interests[0] && profile.interests[0].hobbies && profile.interests[0].hobbies.length > 0) {
      count += 11.43
    }
    if (count > 100) {
      count = 100
    }
    return count
  }
  ngOnDestroy() {
    if (this.badgesSubscription) {
      this.badgesSubscription.unsubscribe()
    }
  }
}
