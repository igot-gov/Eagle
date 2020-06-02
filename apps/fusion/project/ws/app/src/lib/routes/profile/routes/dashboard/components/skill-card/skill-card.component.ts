import { Component, Input, OnInit } from '@angular/core'
import { Router } from '@angular/router'
import { ConfigurationsService } from '@ws-widget/utils'

export interface ICardSkill {
  category?: string
  certificationCount?: number
  courseCount?: number
  horizon?: string
  group?: string
  level?: string
  user_level?: string
  id: string
  imageUrl: string
  navigationUrl: string
  title: string
  progress?: string
  skills?: string[]
  recommendedBy?: string
}
@Component({
  selector: 'ws-app-skill-card',
  templateUrl: './skill-card.component.html',
  styleUrls: ['./skill-card.component.scss'],
})
export class SkillCardComponent implements OnInit {
  @Input() cardData!: ICardSkill
  fetchImage = false
  defaultThumbnail = ''

  constructor(
    private configSvc: ConfigurationsService,
    private router: Router,
  ) { }

  ngOnInit() {
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
      // //console.log('instanceConfig logos >', instanceConfig.logos.defaultContent)
    }
    this.fetchImage = true
  }
  onCardClick(skillName: string) {
    this.router.navigateByUrl(`/app/search/learning?q=${skillName}`)
  }
}
