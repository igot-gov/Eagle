import { Component, OnInit } from '@angular/core'
import { ConfigurationsService } from '@ws-widget/utils/src/public-api'
import { ProfileService } from '../../../../services/profile.service'

@Component({
  selector: 'ws-app-profile-settings',
  templateUrl: './profile-settings.component.html',
  styleUrls: ['./profile-settings.component.scss'],
})
export class ProfileSettingsComponent implements OnInit {
  hiddenFeatures: string[] = []
  showAchievementsTab = true
  showContributionsTab = true
  showSocialTab = true

  interestEnabled = true
  badgesEnabled = true
  skillsEnabled = false
  knowledgeBoardEnabled = true
  playlistEnabled = true
  goalsEnabled = true
  authorEnabled = true
  reviewEnabled = true
  blogsEnabled = true
  qnaEnabled = true

  constructor(private configSvc: ConfigurationsService,
              private profileSvc: ProfileService
  ) { }

  ngOnInit() {
    if (this.configSvc.profileSettings) {
      this.hiddenFeatures = this.configSvc.profileSettings
    }
    this.featureEnable()
  }

  featureEnable() {
    this.profileSvc.fetchConfigFile().subscribe((data: any) => {
      if (data) {
        this.badgesEnabled = data.enabledTabs.achievements.available
        this.skillsEnabled = data.enabledTabs.skills.available
        this.interestEnabled = data.enabledTabs.interests.available
      }
    })
    if (this.configSvc.restrictedFeatures) {
      if (this.configSvc.restrictedFeatures.has('knowledgeBoard')) {
        this.knowledgeBoardEnabled = false
      }
      if (this.configSvc.restrictedFeatures.has('playlist')) {
        this.playlistEnabled = false
      }
      /* if (this.configSvc.restrictedFeatures.has('goals')) {
        this.goalsEnabled = false
      } */
      if (this.configSvc.restrictedFeatures.has('blogs')) {
        this.blogsEnabled = false
      }
      if (this.configSvc.restrictedFeatures.has('qna')) {
        this.qnaEnabled = false
      }
      if (this.configSvc.restrictedFeatures.has('review')) {
        this.reviewEnabled = false
      }
      if (this.configSvc.restrictedFeatures.has('create')) {
        this.authorEnabled = false
      }
    }
    this.showAchievementsTab = this.badgesEnabled || this.skillsEnabled
    this.showContributionsTab = this.badgesEnabled || this.qnaEnabled || this.authorEnabled || this.reviewEnabled
    this.showSocialTab = this.knowledgeBoardEnabled || this.playlistEnabled
  }

  updateStatus(feature: string) {
    if (this.hiddenFeatures.includes(feature)) {
      this.hiddenFeatures = this.hiddenFeatures.filter(obj => obj !== feature)
    } else {
      this.hiddenFeatures.push(feature)
    }
    this.configSvc.prefChangeNotifier.next({ profileSettings: this.hiddenFeatures })
  }

}
