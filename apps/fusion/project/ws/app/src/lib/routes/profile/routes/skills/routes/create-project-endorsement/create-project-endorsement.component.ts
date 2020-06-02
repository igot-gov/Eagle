import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { distinctUntilChanged } from 'rxjs/operators'
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms'
import { SkillsService } from '../../services/skills.service'
import { MatSnackBar } from '@angular/material'
import { Router, ActivatedRoute } from '@angular/router'
import { IEndorsementObj, IEndorsementCreateObj } from '../../models/competency-model'
import { UserAutocompleteService, NsAutoComplete } from '@ws-widget/collection'

@Component({
  selector: 'ws-app-create-project-endorsement',
  templateUrl: './create-project-endorsement.component.html',
  styleUrls: ['./create-project-endorsement.component.scss'],
})
export class CreateProjectEndorsementComponent implements OnInit {
  createFetchStatus: TFetchStatus = 'fetching'
  skillControl = new FormControl()
  emailControl = new FormControl()
  emailIdControl = new FormControl()
  projectControl = new FormControl()
  endorseControl = new FormControl()
  skillId: any
  skillName = ''
  option: any
  skillLevelId = ''
  skillLevel = ''
  filteredOptionsSkill: any
  filteredOptionsEmail: any
  filteredOptionsProject: any
  endorsementObj: IEndorsementObj | IEndorsementCreateObj | null = null
  filteredOptions: any
  emailIdList: string[] = []
  userEmailId = ''
  createForm: FormGroup | null = null
  skillLevelList = ['Starter', 'Basic', 'Advanced', 'Expert']
  @ViewChild('selectContent', { static: true })
  selectContentMessage!: ElementRef<any>
  @ViewChild('validContent', { static: true })
  validContentMessage!: ElementRef<any>
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('errorContent', { static: true })
  errorContentMessage!: ElementRef<any>
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.skills.client
  constructor(
    private form: FormBuilder,
    private skillSvc: SkillsService,
    private router: Router,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    private userId: UserAutocompleteService,
    private configSvc: ConfigurationsService,
  ) {
    if (this.isClient) {
      this.endorseControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoCompleteSkills(value, 'skill_level')
      })
      this.emailControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoCompleteEmailId(value)
      })
      this.createForm = this.form.group({
        endorseControl: ['', Validators.required],
        emailControl: ['', Validators.required],
        message: '',
      })
    } else {
      this.skillControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoComplete(value, 'skill')
      })
      this.projectControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoComplete(value, 'project_code')
      })
      this.emailIdControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
        this.autoComplete(value, 'employee')
      })
      this.createForm = this.form.group({
        skillControl: ['', Validators.required],
        emailIdControl: ['', Validators.required],
        projectControl: ['', Validators.required],
        message: '',
      })
    }
  }

  ngOnInit() {
    this.createFetchStatus = 'fetching'
    this.skillId = this.activatedRoute.snapshot.params.skillId
    this.skillLevelId = this.activatedRoute.snapshot.params.skillLevelId
    this.skillSvc.skillQuotient(this.skillId).subscribe((response: any) => {
      if (this.isClient) {
        const skillLevel = response.skill_quotient.skill_levels.filter((id: any) => id.skill_level_id === this.skillLevelId)
        this.skillName = response.skill_quotient.skill_name
        this.skillLevel = skillLevel[0].skill_level
        // this.option = {
        //   skill_level_id: skillLevel[0].skill_level_id,
        //   skill_level: skillLevel[0].skill_level,
        //   skill_level_description_english: response.skill_quotient.skill_level_description_english,
        //   skill_level_description_german: null,
        //   skill_id: response.skill_quotient.skill_id,
        //   skill_category: response.skill_quotient.skill_category,
        //   skill_group: response.skill_quotient.skill_group,
        //   skill_name: response.skill_quotient.skill_name,
        //   skill_description: response.skill_quotient.skill_description,
        //   skill_title_german:  response.skill_quotient.skill_title_german,
        //   skill_description_german: response.skill_quotient.skill_description_german,
        //   skill_relevance: response.skill_quotient.skill_relevance,
        //   skill_manager: response.skill_quotient.skill_manager,
        //   content_contact: null,
        //   keywords: null,
        // }
        // if (this.createForm) {
        //   this.createForm.controls.endorseControl.setValue(`${this.skillName}-${skillLevel[0].skill_level}`)
        // }
      }
    })
    if (this.configSvc.userProfile) {
      this.userEmailId = this.configSvc.userProfile.email || ''
    }
    this.createFetchStatus = 'done'
  }
  autoComplete(text: string, field: string) {
    this.skillSvc.autoCompleteSkills(text, field).subscribe((res: any) => {
      this.filteredOptionsSkill = []
      this.filteredOptionsEmail = []
      if (field === 'skill') {
        this.filteredOptionsSkill = res
      } else if (field === 'employee') {
        this.filteredOptionsEmail = res
      } else {
        this.filteredOptionsProject = res
      }
    })
  }
  autoCompleteSkills(value: string, field: string) {
    this.filteredOptions = []
    this.skillSvc.autoCompleteSkills(value, field).subscribe((res: any) => {
      this.filteredOptions = res
    })
  }
  openSnackBar() {
    this.snackBar.open(this.selectContentMessage.nativeElement.value)
  }
  displayFn(obj: any) {
    if (obj) {
      return `${obj.skill_name} - ${obj.skill_level}`
    }
    return ''
  }

  displayFnSkill(obj: any) {
    if (obj) {
      return `${obj.skill_name}`
    }
    return ''
  }

  displayFnEmail(obj: any) {
    if (obj) {
      return `${obj.email}`
    }
    return ''
  }
  displayFnEmailId(obj: any) {
    if (obj) {
      return `${obj.emailId}`
    }
    return ''
  }
  displayFnProjectCode(obj: any) {
    if (obj) {
      return `${obj.project_code}`
    }
    return ''
  }
  autoCompleteEmailId(email: string) {
    this.userId.fetchAutoComplete(email).subscribe((emailObj: NsAutoComplete.IUserAutoComplete[]) => {
      this.filteredOptionsEmail = emailObj
    })
  }
  getId(skillObj: any) {
    this.skillId = skillObj.skill_id
  }
  onCreateEndorsement() {
    if (this.isClient) {
      // tslint:disable-next-line:max-line-length
      if (this.createForm && this.emailControl.value.wid !== undefined && this.emailControl.value.email !== this.userEmailId) {
        this.endorsementObj = {
          skill_name: this.skillName,
          skill_id: this.skillId,
          skill_level: this.skillLevel,
          skill_level_id: this.skillLevelId,
          approver_id: this.emailControl.value.wid,
          message: this.createForm.controls.message.value,
        }
        this.skillSvc.addEndorsement(this.endorsementObj).subscribe(() => {
          this.openSnackBar()
          this.router.navigate(['/app/profile/skills/my-skills/endorsement'])
        })
      } else if (this.emailControl.value.email === this.userEmailId) {
        this.snackBar.open(this.existsContentMessage.nativeElement.value)
      } else if (this.emailControl.value.wid === undefined) {
        this.snackBar.open(this.validContentMessage.nativeElement.value)
      } else if (this.emailControl.value.skill_name === undefined) {
        this.snackBar.open(this.emptyContentMessage.nativeElement.value)
      } else {
        this.snackBar.open(this.errorContentMessage.nativeElement.value)
      }
    } else {
      // tslint:disable-next-line:max-line-length
      if (this.createForm && this.projectControl !== undefined && this.skillControl.value.skill_name !== undefined && this.emailIdControl.value.emailId !== this.userEmailId) {
        this.endorsementObj = {
          skill_name: this.skillControl.value.skill_name,
          skill_id: this.skillId,
          project_code: this.projectControl.value,
          approver_id: this.emailIdControl.value.emailId,
          message: this.createForm.controls.message.value,
        }
        this.skillSvc.addEndorsement(this.endorsementObj).subscribe(() => {
          this.openSnackBar()
          this.router.navigate(['/app/profile/skills/my-skills/endorsement'])
        })
      } else if (this.emailIdControl.value.email === this.userEmailId) {
        this.snackBar.open(this.existsContentMessage.nativeElement.value)
      } else if (this.skillControl.value.skill_name === undefined) {
        this.snackBar.open(this.emptyContentMessage.nativeElement.value)
      } else {
        this.snackBar.open(this.errorContentMessage.nativeElement.value)
      }
    }
  }
}
