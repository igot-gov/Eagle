import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core'
import { TFetchStatus, ValueService } from '@ws-widget/utils'
import { IAddSkillObj, IAuthResponse } from '../../models/competency-model'
import { ActivatedRoute, Router } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { FormBuilder, FormGroup, Validators } from '@angular/forms'
import { Subscription } from 'rxjs'
import { SkillService } from '../../services/skill.service'
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper'

@Component({
  selector: 'ws-app-add-skill',
  templateUrl: './add-skill.component.html',
  styleUrls: ['./add-skill.component.scss'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: { showError: true },
  }],
})
export class AddSkillComponent implements OnInit, OnDestroy {
  addSkillFetchStatus: TFetchStatus = 'fetching'
  addSkillObj: IAddSkillObj | null = null
  addSkillForm: FormGroup | null = null
  selected = ''
  value1 = ''
  value2 = ''
  pageNo = 1
  key1 = ''
  key2 = ''
  searchText = ''
  skillLevelList = ['Starter', 'Basic', 'Advanced', 'Expert']
  isLinear = false
  firstFormGroup: FormGroup
  secondFormGroup: FormGroup
  authData: IAuthResponse | null = null
  isSkillManager = false
  skillGroup = ''
  submitted = false
  isSkillPresent = false
  categoryList: string[] = []
  relevanceList: string[] = []
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  @ViewChild('selectContent', { static: true })
  selectContentMessage!: ElementRef<any>
  @ViewChild('errorContent', { static: true })
  errorContentMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills.client
  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private form: FormBuilder,
    private _formBuilder: FormBuilder,
    private valueSvc: ValueService,
    private skillsSvc: SkillService,
    private activatedRoute: ActivatedRoute,

  ) {
    this.firstFormGroup = this._formBuilder.group({
      skillName: ['', [Validators.required]],
      description: ['', Validators.required],
      group: '',
      category: ['', Validators.required],
      manager: ['', Validators.required],
      relevance: ['', Validators.required],
    })
    this.secondFormGroup = this._formBuilder.group({
      levelDescription1: ['', Validators.required],
      levelDescription2: ['', Validators.required],
      levelDescription3: ['', Validators.required],
      levelDescription4: ['', Validators.required],
    })
    this.addSkillForm = this.form.group({
      skillName: [''],
      description: [''],
      // level1: [''],
      // level2: [''],
      // level3: [''],
      // level4: [''],
      levelDescription1: [''],
      levelDescription2: [''],
      levelDescription3: [''],
      levelDescription4: [''],
      group: [''],
      category: [''],
      manager: [''],
      relevance: [''],
    })
  }

  ngOnInit() {
    this.addSkillFetchStatus = 'done'
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    this.skillsSvc.categoryList('skill_category').subscribe((list: any) => {
      this.categoryList = list.distinct_values
    })
    this.skillsSvc.categoryList('skill_relevance').subscribe((list: any) => {
      list.distinct_values.forEach((relevance: any) => {
        this.relevanceList.push(relevance)
      })
    })
    // this.skillsSvc.isApprover().subscribe((access: IAuthResponse) => {
    //   this.authData = access
    //   this.authData.roles.forEach((role: IApprover) => {
    //     this.skillGroup = role.skill_group
    //     if (role.role === 'skill_manager') {
    //       this.isSkillManager = true
    //     }
    //   })
    // })
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
  openSnackBar() {
    this.snackBar.open(this.successContentMessage.nativeElement.value)
  }
  onAddSkill() {
    if (this.secondFormGroup && this.firstFormGroup) {
      if (this.firstFormGroup.controls.skillName.value !== ''
        && this.firstFormGroup.controls.description.value !== ''
        && this.firstFormGroup.controls.category.value !== ''
        && this.firstFormGroup.controls.manager.value !== ''
        && this.firstFormGroup.controls.relevance.value !== ''
        && this.secondFormGroup.controls.levelDescription1.value !== ''
        && this.secondFormGroup.controls.levelDescription2.value !== ''
        && this.secondFormGroup.controls.levelDescription3.value !== ''
        && this.secondFormGroup.controls.levelDescription4.value !== '') {
        this.addSkillObj = {
          skill_name: this.firstFormGroup.controls.skillName.value,
          skill_description: this.firstFormGroup.controls.description.value,
          skill_level_description_Starter: this.secondFormGroup.controls.levelDescription1.value,
          skill_level_description_Basic: this.secondFormGroup.controls.levelDescription2.value,
          skill_level_description_Advanced: this.secondFormGroup.controls.levelDescription3.value,
          skill_level_description_Expert: this.secondFormGroup.controls.levelDescription4.value,
          // skill_group: this.skillGroup,
          skill_category: this.firstFormGroup.controls.category.value,
          skill_manager: this.firstFormGroup.controls.manager.value,
          skill_relevance: this.firstFormGroup.controls.relevance.value,
        }
        this.skillsSvc.addSkill(this.addSkillObj).subscribe(
          () => {
            this.snackBar.open(this.successContentMessage.nativeElement.value)
            this.router.navigate(['/app/skill-management/all-skills'])
          },
          () => {
            this.snackBar.open(this.selectContentMessage.nativeElement.value)
          })
      }
    } else {
      this.snackBar.open(this.successContentMessage.nativeElement.value)
    }
  }
}
