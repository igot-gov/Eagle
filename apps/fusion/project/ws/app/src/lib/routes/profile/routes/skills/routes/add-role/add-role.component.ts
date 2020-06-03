import { Component, OnInit, OnDestroy, ElementRef, ViewChild } from '@angular/core'
import { ISkillList, ICreateObj, ICreateNewObj, IAuthResponse } from '../../models/competency-model'
import { TFetchStatus, ValueService } from '@ws-widget/utils'
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms'
import { MatSnackBar, MatChipInputEvent } from '@angular/material'
import { NsSkills } from '../../models/skills.model'
import { Subscription } from 'rxjs'
import { distinctUntilChanged } from 'rxjs/operators'
import { SkillsService } from '../../services/skills.service'
import { MatAutocomplete } from '@angular/material/autocomplete'
import { ActivatedRoute, Router } from '@angular/router'
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper'

@Component({
  selector: 'ws-app-add-role',
  templateUrl: './add-role.component.html',
  styleUrls: ['./add-role.component.scss'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: { showError: true },
  }],
})
export class AddRoleComponent implements OnInit, OnDestroy {
  isLinear = true
  firstFormGroup: FormGroup
  secondFormGroup: FormGroup
  searchText = ''
  horizon = ''
  category = ''
  createType = ''
  pageNo = 1
  rolesData: NsSkills.IRoles[] = []
  addRoleObj: ICreateObj | null = null
  createRole: ICreateNewObj | null = null
  allSkillsData: ISkillList[] = []
  existingRolesData: NsSkills.IExistingRoles[] = []
  existingGlobalRolesData: NsSkills.IExistingRoles[] = []
  existingRolesDataLess: NsSkills.IExistingRoles[] = []
  numLength = 6
  sliceArray = 6
  viewMore = false
  isRolePresent = false
  create = false
  selectedSkills: any = []
  roleAddControl = new FormControl()
  roleNameControl = new FormControl()
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  filteredOptions: ISkillList[] = []
  apiExistingFetchStatus: TFetchStatus = 'fetching'
  apiFetchStatus: TFetchStatus = 'fetching'
  authFetchStatus: TFetchStatus = 'fetching'
  removable = true
  selectable = true
  submitted = false
  skillGroup = ''
  authData: IAuthResponse | null = null
  isRoleManager = false
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  @ViewChild('selectContent', { static: true })
  selectContentMessage!: ElementRef<any>
  @ViewChild('errorContent', { static: true })
  errorContentMessage!: ElementRef<any>
  @ViewChild('success', { static: true })
  successMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  @ViewChild('skillInput', { static: false }) skillInput: ElementRef<HTMLInputElement> | null = null
  @ViewChild('auto', { static: false }) matAutocomplete: MatAutocomplete | null = null
  constructor(
    private meSrvApi: SkillsService,
    private _formBuilder: FormBuilder,
    public matSnackBar: MatSnackBar,
    private valueSvc: ValueService,
    private skillsSvc: SkillsService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
  ) {
    if (this.isClient) {
      this.firstFormGroup = this._formBuilder.group({
        roleName: ['', Validators.required],
        description: ['', Validators.required],
        jobFamily: ['', Validators.required],
        subJobFamily: [''],
        positionType: ['', Validators.required],
        responsibilities: ['', Validators.required],
      })
      this.secondFormGroup = this._formBuilder.group({
        skillGroup: '',
        roleAddControl: ['', Validators.required],
      })
      this.roleAddControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoCompleteSkills(value, 'skill_level')
      })
    } else {
      this.firstFormGroup = this._formBuilder.group({
        roleName: ['', Validators.required],
      })
      this.secondFormGroup = this._formBuilder.group({
        roleAddControl: ['', Validators.required],
      })
      this.roleAddControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoCompleteSkills(value, 'skill')
      })
    }
  }

  ngOnInit() {
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    // this.skillsSvc.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     this.skillGroup = role.skill_group
    //     if (role.role === 'skill_manager' || role.role === 'competency_manager') {
    //       this.isRoleManager = true
    //     }
    //   })
    this.onCreateRole()
    //   this.authFetchStatus = 'done'
    // })
  }

  onCreateRole() {
    this.apiExistingFetchStatus = 'fetching'
    this.create = true
    this.meSrvApi.existingRoles(this.skillGroup).subscribe(
      (existingRoles: any) => {
        this.existingRolesData = existingRoles
        this.apiExistingFetchStatus = 'done'
      },
      () => {
        this.apiExistingFetchStatus = 'error'
      },
    )
    // this.meSrvApi.existingRoles('Global').subscribe(
    //   (existingRoles: any) => {
    //     this.existingGlobalRolesData = existingRoles
    //     this.apiExistingFetchStatus = 'done'
    //   },
    //   () => {
    //     this.apiExistingFetchStatus = 'error'
    //   },
    // )
  }
  onViewMore() {
    this.viewMore = !this.viewMore
    this.numLength = this.viewMore ? this.existingRolesData.length : this.sliceArray
  }
  onClose() {
    this.create = false
  }
  onAddRole(cardId: string) {
    if (this.createType !== 'user') {
      this.addRoleObj = {
        role_id: cardId,
        type: 'user_common',
      }
      this.skillsSvc.createRole(this.addRoleObj).subscribe(() => {
        this.matSnackBar.open(this.successMessage.nativeElement.value)
        this.create = false
        this.router.navigate(['/app/profile/skills/roles'])
      },
        // tslint:disable-next-line: align
        () => {
          this.matSnackBar.open(this.errorContentMessage.nativeElement.value)
        })
    }
  }
  addRole() {
    const role = this.firstFormGroup.controls.roleName.value
    if (this.isClient) {
      this.createRole = {
        role_name: role,
        skill_id: this.selectedSkills.map((s: any) => s.skill_id),
        skill_level_id: this.selectedSkills.map((s: any) => s.skill_level_id),
        type: 'user',
        role_headline: this.firstFormGroup.controls.description.value,
        job_family: this.firstFormGroup.controls.jobFamily.value,
        sub_job_family: this.firstFormGroup.controls.subJobFamily.value,
        position_type: this.firstFormGroup.controls.positionType.value,
        role_responsibilities: this.firstFormGroup.controls.responsibilities.value,
        skill_group: this.skillGroup,
      }
      if (this.selectedSkills.length === 0) {
        this.submitted = true
      } else if (role !== undefined && this.selectedSkills.length > 0 && /\S/.test(role)) {
        this.skillsSvc.createRole(this.createRole).subscribe(
          () => {
            this.matSnackBar.open(this.successContentMessage.nativeElement.value)
            this.create = false
            setTimeout(() => {
              this.router.navigate(['/app/profile/skills/roles'])
            },
              // tslint:disable-next-line:align
              1500,
            )
          },
          () => {
            this.matSnackBar.open(this.existsContentMessage.nativeElement.value)
          })
      }
    } else {
      this.createRole = {
        role_name: role,
        skill_id: this.selectedSkills.map((s: any) => s.skill_id),
        type: 'user',
      }
      if (role !== undefined && this.selectedSkills.length > 0 && /\S/.test(role)) {
        this.skillsSvc.createRole(this.createRole).subscribe(
          () => {
            this.matSnackBar.open(this.successContentMessage.nativeElement.value)
            this.create = false
            setTimeout(() => {
              this.router.navigate(['/app/profile/skills/roles'])
            },
              // tslint:disable-next-line:align
              1500,
            )
          },
          () => {
            this.matSnackBar.open(this.existsContentMessage.nativeElement.value)
          })
      }
    }

  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
  searchAllSkills(skill: any) {
    this.selectedSkills.push(skill)
  }
  autoCompleteSkills(value: string, field: string) {
    this.skillsSvc.autoCompleteSkills(value, field).subscribe((res: any) => {
      this.filteredOptions = []
      this.filteredOptions = res
    })
  }
  displayFn() {
    return ''
  }
  add(event: MatChipInputEvent): void {
    // Add fruit only when MatAutocomplete is not open
    // To make sure this does not conflict with OptionSelected Event
    if (this.matAutocomplete && !this.matAutocomplete.isOpen) {
      const input = event.input
      const value = event.value

      // Add our fruit
      if ((value || '').trim()) {
        this.selectedSkills.push(value.trim())
      }

      // Reset the input value
      if (input) {
        input.value = ''
      }

      this.selectedSkills.setValue(null)
    }
  }
  remove(skill: ISkillList): void {
    this.selectedSkills = this.selectedSkills.filter((s: any) => s.skill_id !== skill.skill_id)
  }
  onOptionSelected(skill: ISkillList) {
    if (this.isClient) {
      if (this.selectedSkills.findIndex((skillObj: any) => skillObj.skill_id === skill.skill_id) === -1) {
        this.selectedSkills.push(skill)
      } else {
        this.matSnackBar.open(this.selectContentMessage.nativeElement.value)
      }
    } else {
      this.selectedSkills.push(skill)
    }
    if (this.skillInput) {
      this.skillInput.nativeElement.value = ''
    }
    this.roleAddControl.setValue(null)
  }
  // onDelete(event: any) {
  //   if (event) {
  //     setTimeout(() => {
  //       this.getRoles()
  //     },
  //       // tslint:disable-next-line:align
  //       500,
  //     )
  //   }
  // }
}
