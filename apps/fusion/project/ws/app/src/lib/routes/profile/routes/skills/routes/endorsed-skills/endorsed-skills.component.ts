import { Component, OnInit } from '@angular/core'
import { SkillsService } from '../../services/skills.service'
import { TFetchStatus } from '@ws-widget/utils'
import { ActivatedRoute } from '@angular/router'
import { NsSkills } from '../../models/skills.model'
import { ICardSkill } from '../../components/card-skill/card-skill.component'

@Component({
  selector: 'ws-app-endorsed-skills',
  templateUrl: './endorsed-skills.component.html',
  styleUrls: ['./endorsed-skills.component.scss'],
})
export class EndorsedSkillsComponent implements OnInit {
  apiFetchStatus: TFetchStatus = 'fetching'
  pendingFilter = false
  approvedFilter = false
  allFilter = false
  type = 'user'
  endorsementList: any
  status = 'approved'
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills.client
  constructor(
    private skillsSvc: SkillsService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.apiFetchStatus = 'fetching'
    if (this.isClient) {
      this.fetchEndorsedSkills()
    } else {
      this.fetchRequiredSkills()
    }
  }

  private fetchEndorsedSkills() {
    this.skillsSvc.endorsementList(this.type).subscribe(
      (list: any) => {
        this.endorsementList = (list.approved_request || []).map((content: any) => this.convertToCardData(content))
        this.apiFetchStatus = this.endorsementList.length ? 'done' : 'none'
      },
      () => {
        this.apiFetchStatus = 'error'
      })
  }
  private fetchRequiredSkills() {
    this.skillsSvc.requiredSKills(this.status).subscribe(
      data => {
        this.endorsementList = (data || []).map(content => this.convertToCardData(content))
        this.apiFetchStatus = this.apiFetchStatus.length ? 'done' : 'none'
      },
      () => {
        this.apiFetchStatus = 'error'
      },
    )
  }
  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      level: content.skill_level,
      levelId: content.skill_level_id,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      status: content.status,
      endorsementData: content,
    }
  }
  onFilter(type: string) {
    this.apiFetchStatus = 'fetching'
    this.skillsSvc.endorsementList(this.type).subscribe(
      (list: any) => {
        if (type === 'pending') {
          this.pendingFilter = true
          this.endorsementList = list.pending_request
        } else if (type === 'approved') {
          this.approvedFilter = true
          this.endorsementList = list.approved_request
        } else {
          this.allFilter = true
          this.endorsementList = list.all_request
        }
        this.apiFetchStatus = 'done'
      },
      () => {
        this.apiFetchStatus = 'error'
      })
  }
}
