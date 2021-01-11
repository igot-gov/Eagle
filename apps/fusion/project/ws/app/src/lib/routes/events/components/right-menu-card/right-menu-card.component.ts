import { Component, OnDestroy, OnInit } from '@angular/core'
// import { ActivatedRoute } from '@angular/router'
// import { ConfigurationsService } from '@ws-widget/utils'
// import { NSProfileDataV2 } from '../../models/profile-v2.model'

@Component({
  selector: 'app-right-menu-card',
  templateUrl: './right-menu-card.component.html',
  styleUrls: ['./right-menu-card.component.scss'],
  /* tslint:disable */
  host: { class: 'flex flex-1' },
  /* tslint:enable */
})
export class RightMenuCardComponent implements OnInit, OnDestroy {
  // completedPercent!: number
  // badgesSubscription: any
  // portalProfile!: NSProfileDataV2.IProfile
  // badges!: NSProfileDataV2.IBadgeResponse
  // currentEvent!: any
  constructor(
    // private route: ActivatedRoute,
    // configSvc: ConfigurationsService,
  ) {
    // this.currentEvent = configSvc.userProfile && configSvc.userProfile.eventId
    // this.badgesSubscription = this.route.data.subscribe(response => {
    //   this.badges = response && response.badges && response.badges.data
    //   this.portalProfile = response && response.profile && response.profile.data[0]
    //   this.completedPercent = this.calculatePercent(this.portalProfile || null)
    // })
  }
  ngOnInit(): void {
    // this.completedPercent = 86
  }
  // calculatePercent(profile: NSProfileDataV2.IProfile | null): number {
  //   let count = 30
  //   if (!profile) {
  //     return count
  //   }
  //   if (profile.academics && profile.academics[0] && (profile.academics[0].nameOfInstitute || profile.academics[0].nameOfQualification)) {
  //     count += 23
  //   }
  //   // if (profile.employmentDetails && profile.employmentDetails.departmentName) {
  //   //   count += 11.43
  //   // }
  //   if (profile.personalDetails && profile.personalDetails.nationality) {
  //     count += 11.43
  //   }
  //   if (profile.photo) {
  //     count += 11.43
  //   }
  //   if (profile.professionalDetails && profile.professionalDetails[0] && profile.professionalDetails[0].designation) {
  //     count += 11.43
  //   }
  //   if (profile.skills && profile.skills.additionalSkills) {
  //     count += 11.43
  //   }
  //   if (profile.interests && profile.interests.hobbies && profile.interests.hobbies.length > 0) {
  //     count += 11.43
  //   }
  //   if (count > 100) {
  //     count = 100
  //   }
  //   return Math.round(count || 0)
  // }

  ngOnDestroy() {
    // if (this.badgesSubscription) {
    //   this.badgesSubscription.unsubscribe()
    // }
  }
}
