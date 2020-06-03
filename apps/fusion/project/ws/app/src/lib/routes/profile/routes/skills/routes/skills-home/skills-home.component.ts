import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { IAuthResponse } from '../../models/competency-model'
// import { SkillsService } from '../../services/skills.service'

@Component({
  selector: 'ws-app-skills-home',
  templateUrl: './skills-home.component.html',
  styleUrls: ['./skills-home.component.scss'],
})
export class SkillsHomeComponent implements OnInit {
  authData: IAuthResponse | null = null
  isSkillManager = false
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills
    .client
  constructor(
    private activatedRoute: ActivatedRoute,
    // private skillsSvc: SkillsService,
  ) { }

  ngOnInit() {
    // this.skillsSvc.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     if (role.role === 'skill_manager') {
    //       this.isSkillManager = true
    //     }
    //   })
    // })
  }
}
