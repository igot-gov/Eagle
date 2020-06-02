import { Component, OnInit } from '@angular/core'
import { ActivatedRoute } from '@angular/router'
import { IAuthResponse } from '../../models/competency-model'
// import { SkillsService } from '../../services/skills.service'

@Component({
  selector: 'ws-app-my-skills',
  templateUrl: './my-skills.component.html',
  styleUrls: ['./my-skills.component.scss'],
})
export class MySkillsComponent implements OnInit {
  authData: IAuthResponse | null = null
  isSkillManager = false
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills
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
