import { Component, OnInit } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { NsSkills } from '../../models/skills.model'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { IAuthResponse } from '../../models/competency-model'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-pending-skills',
  templateUrl: './pending-skills.component.html',
  styleUrls: ['./pending-skills.component.scss'],
})
export class PendingSkillsComponent implements OnInit {
  requiredSkillsData: ICardSkill[] = []
  authData: IAuthResponse | null = null
  isSkillManager = false
  userRole = ''
  userName = ''
  skillGroup = ''
  status = 'shortlisted'
  skillsLimit = 15
  requiredSkillsFetchStatus: TFetchStatus = 'fetching'
  authFetchStatus: TFetchStatus = 'fetching'
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills
    .client
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs

  constructor(
    private skillsSvc: SkillsService,
    private activatedRoute: ActivatedRoute,
    private configSvc: ConfigurationsService,
  ) { }

  ngOnInit() {
    // if (this.isClient) {
    //   this.skillsSvc.isApprover().subscribe(
    //     (access: IAuthResponse) => {
    //       this.authData = access
    //       this.authData.roles.forEach((role: IApprover) => {
    //         this.skillGroup = role.skill_group
    //         this.userRole = role.role_name
    //         if (role.role === 'skill_manager') {
    //           this.isSkillManager = true
    //         }
    //       })
    //       this.authFetchStatus = 'done'
    //     },
    //     // tslint:disable-next-line:align
    //     () => {
    //       this.authFetchStatus = 'error'
    //     },
    //   )
    // }
    if (this.configSvc.userProfile) {
      this.userName = this.configSvc.userProfile.givenName || ''
    }
    setTimeout(
      () => {
        this.fetchRequiredSkills()
      },
      // tslint:disable-next-line:align
      1000,
    )
  }
  private fetchRequiredSkills() {
    this.skillsSvc.requiredSKills(this.status).subscribe(
      data => {
        this.requiredSkillsData = (data || []).map(content => this.convertToCardData(content))
        this.requiredSkillsFetchStatus = this.requiredSkillsData.length ? 'done' : 'none'
      },
      () => {
        this.requiredSkillsFetchStatus = 'error'
      },
    )
  }

  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      category: content.category,
      certificationCount: content.certification_count || 0,
      courseCount: content.course_count || 0,
      horizon: content.horizon,
      group: content.skill_group,
      level: content.skill_level,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      description: content.skill_description,
      manager: content.skill_manager,
      relevance: content.skill_relevance,
      status: content.status,
      endorsementData: content,
    }
  }
}
