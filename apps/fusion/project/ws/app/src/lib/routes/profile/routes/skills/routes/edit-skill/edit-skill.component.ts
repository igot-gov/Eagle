import { Component, OnInit, OnDestroy } from '@angular/core'
import { TFetchStatus, ValueService } from '@ws-widget/utils'
import { IAddSkillObj } from '../../models/competency-model'
import { Router, ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { FormBuilder, FormGroup, Validators } from '@angular/forms'
import { SkillsService } from '../../services/skills.service'
import { Subscription } from 'rxjs'
@Component({
  selector: 'ws-app-edit-skill',
  templateUrl: './edit-skill.component.html',
  styleUrls: ['./edit-skill.component.scss'],
})
export class EditSkillComponent implements OnInit, OnDestroy {
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
  editSkill = ''
  isLinear = false
  skillGroup = ''
  private defaultSideNavBarOpenedSubscription: Subscription | null = null
  isLtMedium$ = this.valueSvc.isLtMedium$
  screenSizeIsLtMedium = false
  firstFormGroup: FormGroup
  secondFormGroup: FormGroup
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
      levelDescription1: '',
      levelDescription2: '',
      levelDescription3: '',
      levelDescription4: '',
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
      this.skillGroup = this.skillClientData.skill_quotient.skill_group
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
    this.snackBar.open('Successfully updated skill', 'close', {
      duration: 3000,
    })
  }
  onUpdateSkill() {
    if (this.firstFormGroup && this.secondFormGroup) {
      this.editSkillObj = {
        skill_name: this.firstFormGroup.controls.skillName.value,
        skill_description: this.firstFormGroup.controls.description.value,
        skill_id: this.skillId,
        skill_level_description_Starter: this.secondFormGroup.controls.levelDescription1.value,
        skill_level_description_Basic: this.secondFormGroup.controls.levelDescription2.value,
        skill_level_description_Advanced: this.secondFormGroup.controls.levelDescription3.value,
        skill_level_description_Expert: this.secondFormGroup.controls.levelDescription4.value,
        skill_group: this.skillGroup,
        skill_category: this.firstFormGroup.controls.category.value,
        skill_manager: this.firstFormGroup.controls.manager.value,
        skill_relevance: this.firstFormGroup.controls.relevance.value,
      }
      this.skillSrv.addSkill(this.editSkillObj).subscribe(() => {
        this.openSnackBar()
        this.router.navigate(['/app/profile/skills/skills'])
      })
    }

  }
}
