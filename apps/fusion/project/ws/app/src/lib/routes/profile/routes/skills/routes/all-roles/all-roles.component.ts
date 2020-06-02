import { Component, OnInit } from '@angular/core'
import { TFetchStatus } from '@ws-widget/utils'
import { SkillsService } from '../../services/skills.service'
import { IAuthResponse, IApprover } from '../../models/competency-model'
import { NsSkills } from '../../models/skills.model'
import { ActivatedRoute } from '@angular/router'
@Component({
  selector: 'ws-app-all-roles',
  templateUrl: './all-roles.component.html',
  styleUrls: ['./all-roles.component.scss'],
})
export class AllRolesComponent implements OnInit {
  allSkillsFetchStatus: TFetchStatus = 'fetching'
  skillGroup = ''
  authData: IAuthResponse | null = null
  isRoleManager = false
  clickRole = true
  skillType = 'Global'
  authObj: IApprover | null = null
  existingRolesData: NsSkills.IExistingRoles[] = []
  existingRolesDataLess: NsSkills.IExistingRoles[] = []
  apiExistingFetchStatus: TFetchStatus = 'fetching'
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  constructor(
    private skillSrv: SkillsService,
    private activatedRoute: ActivatedRoute,
  ) { }
  ngOnInit() {
    // this.skillSrv.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     this.authObj = role
    //     this.skillGroup = role.skill_group
    //     if (role.role === 'skill_manager') {
    //       this.isRoleManager = true
    //     }
    //   })
    // })
    this.fetchRoles('Global')
  }
  fetchRoles(skillGroup: string) {
    this.apiExistingFetchStatus = 'fetching'
    this.skillSrv.existingRoles(skillGroup).subscribe((existingRoles: any) => {
      this.existingRolesData = existingRoles
      if (this.existingRolesData.length === 0) {
        this.apiExistingFetchStatus = 'none'
      } else {
        this.apiExistingFetchStatus = 'done'
      }
    },
      // tslint:disable-next-line:align
      () => {
        this.apiExistingFetchStatus = 'error'
      })
  }
  onStatusChange(clickRole: boolean) {
    if (clickRole) {
      this.skillType = 'Global'
      this.skillGroup = this.authObj ? this.authObj.skill_group : this.skillGroup
      this.fetchRoles(this.skillType)
    } else {
      this.skillType = this.authObj ? this.authObj.skill_group : this.skillGroup
      this.skillGroup = 'Global'
      this.fetchRoles(this.skillType)
    }
  }
  changeRoles() {
    this.clickRole = !this.clickRole
    this.onStatusChange(this.clickRole)
  }
}
