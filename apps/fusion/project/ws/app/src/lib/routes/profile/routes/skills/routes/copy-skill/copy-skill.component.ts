import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core'
import { TFetchStatus, ValueService } from '@ws-widget/utils'
import { IAddSkillObj } from '../../models/competency-model'
import { Router, ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { FormBuilder, FormGroup, Validators } from '@angular/forms'
import { SkillsService } from '../../services/skills.service'
import { Subscription } from 'rxjs'
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper'

@Component({
  selector: 'ws-app-copy-skill',
  templateUrl: './copy-skill.component.html',
  styleUrls: ['./copy-skill.component.scss'],
  providers: [{
    provide: STEPPER_GLOBAL_OPTIONS, useValue: { showError: true },
  }],
})
export class CopySkillComponent implements OnInit, OnDestroy {
  editSkillFetchStatus: TFetchStatus = 'fetching'
  editSkillObj: IAddSkillObj | null = null
  editSkillForm: FormGroup | null = null
  skillClientData: any
  skillQuotientData: any
  // level1 = ''
  // level2 = ''
  // level3 = ''
  // level4 = ''
  levelDescription = ''
  levelDescription2 = ''
  levelDescription3 = ''
  levelDescription4 = ''
  description = ''
  skillId: any
  selected = ''
  categoryList: string[] = []
  editSkill = ''
  isLinear = false
  value1 = ''
  value2 = ''
  pageNo = 1
  key1 = ''
  key2 = ''
  searchText = ''
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  firstFormGroup: FormGroup
  secondFormGroup: FormGroup
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  isClient = this.route.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills.client
  skillLevelList = ['Starter', 'Basic', 'Advanced', 'Expert']
  constructor(
    private router: Router,
    private snackBar: MatSnackBar,
    private form: FormBuilder,
    private route: ActivatedRoute,
    private skillSrv: SkillsService,
    private valueSvc: ValueService,
  ) {
    this.firstFormGroup = this.form.group({
      skillName: ['', Validators.required],
      description: ['', Validators.required],
      group: '',
      category: ['', Validators.required],
      manager: ['', Validators.required],
      relevance: ['', Validators.required],
    })
    this.secondFormGroup = this.form.group({
      levelDescription1: ['', Validators.required],
      levelDescription2: ['', Validators.required],
      levelDescription3: ['', Validators.required],
      levelDescription4: ['', Validators.required],
    })
    this.editSkillForm = this.form.group({
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
    this.editSkillFetchStatus = 'fetching'
    this.skillId = this.route.snapshot.params.id
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    this.skillSrv.categoryList('skill_category').subscribe((list: any) => {
      this.categoryList = list.distinct_values
    })
    this.skillSrv.skillQuotient(this.skillId).subscribe(res => {
      this.skillClientData = res
      this.editSkill = this.skillClientData.skill_quotient.skill_name
      this.firstFormGroup.controls.skillName.setValue(this.skillClientData.skill_quotient.skill_name)
      this.firstFormGroup.controls.description.setValue(this.skillClientData.skill_quotient.skill_description)
      this.firstFormGroup.controls.group.setValue(this.skillClientData.skill_quotient.skill_group)
      this.firstFormGroup.controls.category.setValue(this.skillClientData.skill_quotient.skill_category)
      this.firstFormGroup.controls.manager.setValue(this.skillClientData.skill_quotient.skill_manager)
      this.firstFormGroup.controls.relevance.setValue(this.skillClientData.skill_quotient.skill_relevance)
      this.skillId = this.skillClientData.skill_quotient.skill_id
      this.skillQuotientData = []
      this.skillClientData.skill_quotient.skill_levels.forEach((response: any) => {
        if (response.skill_level === 'Starter') {
          // this.level1 = response.skill_level
          this.secondFormGroup.controls.levelDescription1.setValue(response.skill_level_description_english)
          this.skillQuotientData.push(response)
        }
      })
      this.skillClientData.skill_quotient.skill_levels.forEach((response: any) => {
        if (response.skill_level === 'Basic') {
          // this.level2 = response.skill_level
          this.secondFormGroup.controls.levelDescription2.setValue(response.skill_level_description_english)
          this.skillQuotientData.push(response)
        }
      })
      this.skillClientData.skill_quotient.skill_levels.forEach((response: any) => {
        if (response.skill_level === 'Advanced') {
          // this.level3 = response.skill_level
          this.secondFormGroup.controls.levelDescription3.setValue(response.skill_level_description_english)
          this.skillQuotientData.push(response)
        }
      })
      this.skillClientData.skill_quotient.skill_levels.forEach((response: any) => {
        if (response.skill_level === 'Expert') {
          // this.level4 = response.skill_level
          this.secondFormGroup.controls.levelDescription4.setValue(response.skill_level_description_english)
          this.skillQuotientData.push(response)
        }
      })

    })
    this.editSkillFetchStatus = 'done'
  }
  ngOnDestroy() {
    if (this.defaultSideNavBarOpenedSubscription) {
      this.defaultSideNavBarOpenedSubscription.unsubscribe()
    }
  }
  openSnackBar() {
    this.snackBar.open(this.successContentMessage.nativeElement.value)
  }
  onCopySkill() {
    if (this.firstFormGroup && this.secondFormGroup) {
      if (this.firstFormGroup.controls.skillName.value !== ''
        && this.firstFormGroup.controls.description.value !== ''
        && this.firstFormGroup.controls.category.value !== ''
        && this.firstFormGroup.controls.manager.value !== ''
        && this.firstFormGroup.controls.relevance.value !== '') {
        this.editSkillObj = {
          skill_name: this.firstFormGroup.controls.skillName.value,
          skill_description: this.firstFormGroup.controls.description.value,
          skill_level_description_Starter: this.secondFormGroup.controls.levelDescription1.value,
          skill_level_description_Basic: this.secondFormGroup.controls.levelDescription2.value,
          skill_level_description_Advanced: this.secondFormGroup.controls.levelDescription3.value,
          skill_level_description_Expert: this.secondFormGroup.controls.levelDescription4.value,
          skill_group: this.skillClientData.skill_quotient.skill_group,
          skill_category: this.firstFormGroup.controls.category.value,
          skill_manager: this.firstFormGroup.controls.manager.value,
          skill_relevance: this.firstFormGroup.controls.relevance.value,
        }
        this.skillSrv.addSkill(this.editSkillObj).subscribe(
          () => {
            this.openSnackBar()
            this.router.navigate(['/app/profile/skills/skills'])
          },
          () => {
            this.snackBar.open(this.existsContentMessage.nativeElement.value)
          })
      }
    } else {
      this.snackBar.open(this.emptyContentMessage.nativeElement.value)
    }
  }
}
