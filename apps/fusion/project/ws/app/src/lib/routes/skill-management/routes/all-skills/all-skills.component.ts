import { Component, OnInit } from '@angular/core'
import { SkillService } from '../../services/skill.service'
import { ISkillList, IAuthResponse } from '../../models/competency-model'
import { TFetchStatus } from '@ws-widget/utils'
import { FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms'
import { NsSkills } from '../../models/skills.model'
import { ICardSkill } from '../../components/card-skill/card-skill.component'
import { distinctUntilChanged } from 'rxjs/operators'
import { ActivatedRoute } from '@angular/router'
@Component({
  selector: 'ws-app-all-skills',
  templateUrl: './all-skills.component.html',
  styleUrls: ['./all-skills.component.scss'],
})
export class AllSkillsComponent implements OnInit {
  searchText = ''
  value1 = ''
  value2 = ''
  pageNo = 1
  key1 = ''
  key2 = ''
  isLinear = false
  firstFormGroup: FormGroup
  secondFormGroup: FormGroup
  authData: IAuthResponse | null = null
  authFetchStatus: TFetchStatus = 'fetching'
  isSkillManager = false
  skillForm: FormGroup | null = null
  allSkillsData: any | null = null
  filteredOptions: ISkillList[] = []
  categoryList: string[] = ['None']
  horizonList: string[] = ['None']
  groupList: string[] = ['None']
  skillsList: ICardSkill[] = []
  allSkillsFetchStatus: TFetchStatus = 'fetching'
  skillControl = new FormControl()
  options: string[] = []
  selected = ''
  isClient = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs.allSkills
    .client
  enabledTabs = this.activatedRoute.snapshot.data.pageData.data.enabledTabs.skills.subTabs
  constructor(
    private skillSrv: SkillService,
    private activatedRoute: ActivatedRoute,
    private form: FormBuilder,
  ) {
    this.skillControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
      this.autoCompleteSkills(value, 'skill')
    })
    this.skillForm = this.form.group({
      skillControl: [''],
      horizon: [''],
      category: [''],
      group: [''],
    })
    this.firstFormGroup = this.form.group({
      firstCtrl: ['', Validators.required],
    })
    this.secondFormGroup = this.form.group({
      secondCtrl: ['', Validators.required],
    })
  }
  ngOnInit() {
    if (this.isClient) {
      this.key1 = 'skill_relevance'
      this.key2 = 'skill_category'
    } else {
      this.key1 = 'horizon'
      this.key2 = 'category'
    }
    // this.skillSrv.isApprover().subscribe(
    //   (access: IAuthResponse) => {
    //     this.authData = access
    //     this.authData.roles.forEach((role: IApprover) => {
    //       if (role.role === 'skill_manager') {
    //         this.isSkillManager = true
    //       }
    //     })
    //     this.authFetchStatus = 'done'
    //   },
    //   // tslint:disable-next-line:align
    //   () => {
    //     this.authFetchStatus = 'error'
    //   },
    // )
    this.fetchAllSkills(
      this.searchText,
      this.key1,
      this.key2,
      this.value1,
      this.value2,
      this.pageNo,
    )
  }
  // getAllSkills(searchText: string, value1: string, value2: string, pageNo: number) {
  //   this.allSkillsFetchStatus = 'fetching'
  //   this.skillSrv.allSkills(searchText, value1, value2, pageNo).subscribe((allSkillsResponse: IAllSkills) => {
  //     this.allSkillsData = allSkillsResponse
  //     this.skillsList = this.allSkillsData.skill_list
  //     this.allSkillsFetchStatus = 'done'
  //   })
  // }
  searchAllSkills(skillName: string) {
    if (this.isClient) {
      this.key1 = 'skill_relevance'
      this.key2 = 'skill_category'
    } else {
      this.key1 = 'horizon'
      this.key2 = 'category'
    }
    this.searchText = skillName
    this.fetchAllSkills(
      this.searchText,
      this.key1,
      this.key2,
      this.value1,
      this.value2,
      this.pageNo,
    )
  }
  private fetchAllSkills(
    searchText: string,
    key1: string,
    key2: string,
    value1: string,
    value2: string,
    pageNo: number,
  ) {
    this.allSkillsFetchStatus = 'fetching'
    this.horizonList = ['None']
    this.categoryList = ['None']
    this.groupList = ['None']
    this.skillSrv.allSkills(searchText, key1, key2, value1, value2, pageNo).subscribe(
      data => {
        this.allSkillsData = data
        if (this.allSkillsData.horizon_wise_skill_count) {
          this.allSkillsData.horizon_wise_skill_count.forEach((drop: any) => {
            this.horizonList.push(drop.key)
          })
        }
        if (this.isClient) {
          this.skillSrv.categoryList('skill_category').subscribe((list: any) => {
            list.distinct_values.forEach((category: any) => {
              this.categoryList.push(category)
            })
          })
          this.skillSrv.categoryList('skill_relevance').subscribe((list: any) => {
            list.distinct_values.forEach((relevance: any) => {
              this.groupList.push(relevance)
            })
          })
        } else {
          this.allSkillsData.category_wise_skill_count.forEach((drop: any) => {
            this.categoryList.push(drop.key)
          })
        }
        this.skillsList = (data.skill_list || []).map(content => this.convertToCardData(content))
        this.allSkillsFetchStatus = this.skillsList.length ? 'done' : 'none'
      },
      () => {
        this.allSkillsFetchStatus = 'error'
      },
    )
  }
  private convertToCardData(content: NsSkills.IRecommendedSkill | NsSkills.ISkill): ICardSkill {
    return {
      category: content.skill_category,
      certificationCount: content.certification_count || 0,
      courseCount: content.course_count || 0,
      horizon: content.horizon,
      group: content.skill_relevance,
      level: content.skill_level,
      id: `${content.skill_id}`,
      imageUrl: content.image_url,
      navigationUrl: `/app/profile/skills/skill-details/${content.skill_id}`,
      title: content.skill_name,
      description: content.skill_description,
      manager: content.skill_manager,
      relevance: content.skill_relevance,
      status: content.status,
    }
  }
  applyFilter(filterName: string, type: string) {
    const filterType = filterName === 'None' ? '' : filterName
    this.allSkillsFetchStatus = 'fetching'
    if (type === 'category') {
      this.value2 = filterType ? filterType : ''
    } else {
      this.value1 = filterType ? filterType : ''
    }
    if (this.isClient) {
      this.key1 = 'skill_relevance'
      this.key2 = 'skill_category'
    } else {
      this.key1 = 'horizon'
      this.key2 = 'category'
    }
    this.fetchAllSkills(
      this.searchText,
      this.key1,
      this.key2,
      this.value1,
      this.value2,
      this.pageNo,
    )
  }
  autoCompleteSkills(value: string, field: string) {
    this.skillSrv.autoCompleteSkills(value, field).subscribe((res: any) => {
      this.filteredOptions = []
      this.filteredOptions = res
    })
  }
  onPress(event: any) {
    if (event.keyCode === 13) {
      this.searchAllSkills(event.target.value)
    }
  }
  reset() {
    this.allSkillsFetchStatus = 'fetching'
    // this .searchText = '';
    // this.skillForm.reset();
    // this.skillForm.get('skillControl').setValue('');
    if (this.skillForm) {
      this.skillForm.controls.category.setValue('')
      this.skillForm.controls.horizon.setValue('')
      this.skillForm.controls.group.setValue('')
      this.value1 = ''
      this.value2 = ''
      this.pageNo = 1
      this.searchText = ''
      if (this.isClient) {
        this.key1 = 'skill_relevance'
        this.key2 = 'skill_category'
      } else {
        this.key1 = 'horizon'
        this.key2 = 'category'
      }
      this.fetchAllSkills(
        this.searchText,
        this.key1,
        this.key2,
        this.value1,
        this.value2,
        this.pageNo,
      )
    }
  }
  more() {
    this.pageNo += 1
    if (this.isClient) {
      this.key1 = 'skill_relevance'
      this.key2 = 'skill_category'
    } else {
      this.key1 = 'horizon'
      this.key2 = 'category'
    }
    this.value1 = ''
    this.value2 = ''
    this.searchText = ''
    this.fetchAllSkills(
      this.searchText,
      this.key1,
      this.key2,
      this.value1,
      this.value2,
      this.pageNo,
    )
  }
}
