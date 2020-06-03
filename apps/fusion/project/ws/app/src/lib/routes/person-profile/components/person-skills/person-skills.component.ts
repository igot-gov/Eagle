import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core'
import { PersonProfileService } from '../../services/person-profile.service'
import { IMySkills } from '../../../profile/routes/skills/models/competency-model'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils/src/public-api'

@Component({
  selector: 'ws-app-person-skills',
  templateUrl: './person-skills.component.html',
  styleUrls: ['./person-skills.component.scss'],
})
export class PersonSkillsComponent implements OnInit {
  @Input() wid = ''
  @Output() isSkillsEarned: EventEmitter<number> = new EventEmitter()

  myskills: IMySkills[] = []
  suggestionlimit = 6
  mySkillsDisplay: IMySkills[] = []
  defaultThumbnail = ''
  skillFetchStatus: TFetchStatus = 'none'

  constructor(private personprofileSvc: PersonProfileService, private configSvc: ConfigurationsService) {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
  }

  ngOnInit() {
    this.skillFetchStatus = 'fetching'
    this.personprofileSvc.fetchUserSkills(this.wid).subscribe((result: IMySkills[]) => {
      if (result) {
        this.fetchMySkills(result)
        this.skillFetchStatus = 'done'

      }
    })

  }

  fetchMySkills(myskills: IMySkills[]) {
    this.myskills = myskills
    if (this.myskills.length > 0) {
      this.isSkillsEarned.emit(1)
    }
  }

}
