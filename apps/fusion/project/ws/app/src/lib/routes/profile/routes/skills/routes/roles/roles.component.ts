import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core'
import { ISkillList, ICreateObj, ICreateNewObj, IAuthResponse } from '../../models/competency-model'
import { TFetchStatus, ValueService } from '@ws-widget/utils'
import { MatSnackBar } from '@angular/material'
import { NsSkills } from '../../models/skills.model'
import { Subscription } from 'rxjs'
import { SkillsService } from '../../services/skills.service'
import { MatAutocomplete } from '@angular/material/autocomplete'
import { ActivatedRoute } from '@angular/router'

@Component({
  selector: 'ws-app-roles',
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.scss'],
})
export class RolesComponent implements OnInit, OnDestroy {
  isLinear = true
  searchText = ''
  horizon = ''
  category = ''
  createType = ''
  pageNo = 1
  counter = 0
  rolesData: NsSkills.IRoles[] = []
  createdRolesData: NsSkills.IRoles[] = []
  assignedRolesData: NsSkills.IRoles[] = []
  addRoleObj: ICreateObj | null = null
  createRole: ICreateNewObj | null = null
  allSkillsData: ISkillList[] = []
  existingRolesData: NsSkills.IExistingRoles[] = []
  existingGlobalRolesData: NsSkills.IExistingRoles[] = []
  existingRolesDataLess: NsSkills.IExistingRoles[] = []
  numLength = 6
  userRole = ''
  sliceArray = 6
  viewMore = false
  isRolePresent = false
  create = false
  selectedSkills: any = []
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  filteredOptions: ISkillList[] = []
  apiExistingFetchStatus: TFetchStatus = 'fetching'
  apiFetchStatus: TFetchStatus = 'fetching'
  authFetchStatus: TFetchStatus = 'fetching'
  apiCreatedFetchStatus: TFetchStatus = 'fetching'
  apiAssignedFetchStatus: TFetchStatus = 'fetching'
  removable = true
  selectable = true
  skillGroup = ''
  authData: IAuthResponse | null = null
  isRoleManager = false
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  @ViewChild('skillInput', { static: false }) skillInput: ElementRef<HTMLInputElement> | null = null
  @ViewChild('auto', { static: false }) matAutocomplete: MatAutocomplete | null = null
  constructor(
    private meSrvApi: SkillsService,
    public matSnackBar: MatSnackBar,
    private valueSvc: ValueService,
    // private skillsSvc: SkillsService,
    private activatedRoute: ActivatedRoute,
    // private router: Router,
  ) { }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    // this.skillsSvc.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     this.skillGroup = role.skill_group
    //     this.userRole = role.role_name
    //     if (role.role === 'skill_manager' || role.role === 'competency_manager') {
    //       this.isRoleManager = true
    //       this.getCreatedRoles()
    //     }
    //   })
    //   this.authFetchStatus = 'done'
    // })
    this.getRoles()
    // if (this.rolesData.length === 0 && this.createdRolesData.length === 0 && this.assignedRolesData.length === 0) {
    //   this.router.navigate(['/app/profile/skills/add-role'])
    // }
  }

  getRoles() {
    this.apiFetchStatus = 'fetching'
    this.meSrvApi.roles().subscribe(
      (roles: any) => {
        this.rolesData = roles.user_roles
        this.assignedRolesData = roles.assigned_roles
        this.apiFetchStatus = 'done'
      },
      () => {
        this.apiFetchStatus = 'error'
      },
    )
  }
  getCreatedRoles() {
    this.apiCreatedFetchStatus = 'fetching'
    this.meSrvApi.createdRoles().subscribe(
      (roles: any) => {
        this.createdRolesData = roles
        if (this.createdRolesData.length === 0) {
          this.counter = this.counter + 1
        }
        this.apiCreatedFetchStatus = 'done'
      },
      () => {
        this.apiCreatedFetchStatus = 'error'
      })
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
}
