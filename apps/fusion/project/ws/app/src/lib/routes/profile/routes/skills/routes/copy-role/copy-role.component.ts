import { Component, OnInit, ElementRef, ViewChild } from '@angular/core'
import { ISkillList, ICreateNewObj, IAuthResponse } from '../../models/competency-model'
import { TFetchStatus } from '@ws-widget/utils'
import { FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms'
import { MatSnackBar, MatChipInputEvent } from '@angular/material'
import { distinctUntilChanged } from 'rxjs/operators'
import { SkillsService } from '../../services/skills.service'
import { MatAutocomplete } from '@angular/material/autocomplete'
import { ActivatedRoute, Router } from '@angular/router'
@Component({
  selector: 'ws-app-copy-role',
  templateUrl: './copy-role.component.html',
  styleUrls: ['./copy-role.component.scss'],
})
export class CopyRoleComponent implements OnInit {
  filteredOptions: ISkillList[] = []
  removable = true
  selectable = true
  isRolePresent = false
  description = ''
  roleId = ''
  skillGroup = ''
  editForm: FormGroup
  updateRole!: ICreateNewObj
  selectedSkills: any = []
  roleQuotientData: any | null = null
  editControl = new FormControl()
  roleName = ''
  authData: IAuthResponse | null = null
  editRoleFetchStatus: TFetchStatus = 'fetching'
  isClient = this.route.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  @ViewChild('skillInput', { static: false }) skillInput: ElementRef<HTMLInputElement> | null = null
  @ViewChild('auto', { static: false }) matAutocomplete: MatAutocomplete | null = null
  @ViewChild('selectContent', { static: true })
  selectContentMessage!: ElementRef<any>
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  @ViewChild('addContent', { static: true })
  addContentMessage!: ElementRef<any>
  constructor(
    private _formBuilder: FormBuilder,
    public matSnackBar: MatSnackBar,
    private route: ActivatedRoute,
    private skillSvc: SkillsService,
    private router: Router,

  ) {
    this.editControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
      this.autoCompleteSkills(value, 'skill')
    })
    this.editForm = this._formBuilder.group({
      roleName: ['', Validators.required],
      description: ['', Validators.required],
      jobFamily: ['', Validators.required],
      subJobFamily: [''],
      positionType: ['', Validators.required],
      responsibilities: ['', Validators.required],
      skillGroup: '',
      editControl: ['', Validators.required],
    })
  }

  ngOnInit() {
    this.editRoleFetchStatus = 'fetching'
    this.roleId = this.route.snapshot.params.id
    // this.skillSvc.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     this.skillGroup = role.skill_group
    //   })
    // })
    this.roleQuotient()
    this.editRoleFetchStatus = 'done'
  }
  roleQuotient() {
    this.skillSvc.roleQuotient(this.roleId).subscribe((roleQuotient: any) => {
      this.roleQuotientData = roleQuotient
      if (this.isClient) {
        this.roleName = this.roleQuotientData.role_name
        this.roleId = this.roleQuotientData.role_id
        if (this.editForm) {
          this.editForm.controls.roleName.setValue(this.roleQuotientData.role_name)
          this.editForm.controls.description.setValue(this.roleQuotientData.role_headline)
          this.editForm.controls.jobFamily.setValue(this.roleQuotientData.job_family)
          if (this.roleQuotientData.sub_job_family) {
            this.editForm.controls.subJobFamily.setValue(this.roleQuotientData.sub_job_family)
          }
          this.editForm.controls.positionType.setValue(this.roleQuotientData.position_type)
          this.editForm.controls.responsibilities.setValue(this.roleQuotientData.role_responsibilities)
          this.editForm.controls.skillGroup.setValue(this.skillGroup)
        }
        this.roleQuotientData.skills.map((cur: any) => {
          this.selectedSkills.push({
            skill_id: cur.skill_id,
            skill_name: cur.skill_name,
            skill_level: cur.skill_level,
            skill_level_id: cur.skill_level_id,
          })
        })
      } else {
        this.roleName = this.roleQuotientData.role_quotient_details.role_name
        this.roleId = this.roleQuotientData.role_quotient_details.role_id
        this.roleQuotientData.role_quotient_details.skills.map((cur: any) => {
          this.selectedSkills.push({
            skill_id: cur.skill_id,
            skill_name: cur.skill_name,
          })
        })
      }
    })
  }
  searchAllSkills(skill: any) {
    this.selectedSkills.push(skill)
  }
  autoCompleteSkills(value: string, field: string) {
    this.skillSvc.autoCompleteSkills(value, field).subscribe((res: any) => {
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
    this.selectedSkills = this.selectedSkills.filter((s: any) => s.skill_level_id !== skill.skill_level_id)

    // if (index >= 0) {
    //   this.selectedSkills.splice(index, 1)
    // }
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
    this.editControl.setValue(null)
  }
  openSnackBar() {
    this.matSnackBar.open(this.emptyContentMessage.nativeElement.value)
  }

  getRoles() {
    this.skillSvc.existingRoles('Global').subscribe((existingRoles: any) => {
      existingRoles.forEach((roleDetails: any) => {
        if (roleDetails.role_name === this.editForm.controls.roleName.value) {
          this.isRolePresent = true
        } else {
          this.isRolePresent = false
        }
      })
    })
  }
  onCopyRole() {
    if (this.editForm) {
      if (this.editForm.controls.roleName.value !== '' && this.editForm.controls.description.value !== ''
        && this.editForm.controls.jobFamily.value !== '' && this.editForm.controls.positionType.value
        && this.editForm.controls.responsibilities.value !== '' && this.selectedSkills.length > 0) {
        this.updateRole = {
          role_name: this.editForm.controls.roleName.value,
          skill_id: this.selectedSkills.map((s: any) => s.skill_id),
          skill_level_id: this.selectedSkills.map((s: any) => s.skill_level_id),
          type: 'user',
          role_headline: this.editForm.controls.description.value,
          job_family: this.editForm.controls.jobFamily.value,
          sub_job_family: this.editForm.controls.subJobFamily.value,
          position_type: this.editForm.controls.positionType.value,
          role_responsibilities: this.editForm.controls.responsibilities.value,
          skill_group: this.skillGroup,
        }
        this.skillSvc.createRole(this.updateRole).subscribe(() => {
          this.matSnackBar.open(this.successContentMessage.nativeElement.value)
          this.router.navigate(['/app/profile/skills/roles'])
        },
        // tslint:disable-next-line:align
        () => {
          this.matSnackBar.open(this.existsContentMessage.nativeElement.value)
        })
      } else if (this.selectedSkills.length === 0) {
        this.matSnackBar.open(this.addContentMessage.nativeElement.value)
      } else {
        this.openSnackBar()
      }
    }
  }
}
