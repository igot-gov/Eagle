import { Component, OnInit } from '@angular/core'
import { SkillService } from '../../services/skill.service'
import { TFetchStatus } from '@ws-widget/utils'
import { NsSkills } from '../../../profile/routes/skills/models/skills.model'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { ActivatedRoute } from '@angular/router'
@Component({
  selector: 'ws-app-created-skills',
  templateUrl: './created-skills.component.html',
  styleUrls: ['./created-skills.component.scss'],
})
export class CreatedSkillsComponent implements OnInit {
  createdSkillsData: ICardSkill[] = []
  createdSkillsFetchStatus: TFetchStatus = 'fetching'
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills.client
  constructor(
    private skillsSvc: SkillService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.fetchCreatedSkills()
  }
  private fetchCreatedSkills() {
    this.skillsSvc.createdSkills().subscribe(
      (data: any) => {
        this.createdSkillsData = (data || []).map((content: any) => this.convertToCardData(content))
        this.createdSkillsFetchStatus = this.createdSkillsData.length ? 'done' : 'none'
      },
      () => {
        this.createdSkillsFetchStatus = 'error'
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
