import { Component, OnInit, ViewEncapsulation, ElementRef, ViewChild } from '@angular/core'
import { FormControl } from '@angular/forms'
import { ActivatedRoute, Router } from '@angular/router'
import { ConfigurationsService, NsPage, TFetchStatus } from '@ws-widget/utils'
import { debounceTime, distinctUntilChanged } from 'rxjs/operators'
import { SearchServService } from '../../../search/services/search-serv.service'
import { ISearchAutoComplete, ISearchQuery, ISuggestedFilters, ISearchRequest } from '../../../search/models/search.model'
import { NSSearch, WidgetContentService, NsContent } from '@ws-widget/collection'
import { MatSnackBar } from '@angular/material'
import { ICourseObj, ISkillList } from '../../models/competency-model'
import { SkillService } from '../../services/skill.service'

@Component({
  selector: 'ws-app-add-learning-path',
  templateUrl: './add-learning-path.component.html',
  styleUrls: ['./add-learning-path.component.scss'],
  // tslint:disable-next-line:use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
})
export class AddLearningPathComponent implements OnInit {

  query: FormControl = new FormControl('')
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  autoCompleteResults: ISearchAutoComplete[] = []
  searchQuery: ISearchQuery = {
    l: this.getActivateLocale(),
    q: '',
  }
  fetchSearchContents: TFetchStatus = 'none'
  apiFetchStatus: TFetchStatus = 'fetching'
  searchResults: NSSearch.ISearchApiResult = {
    totalHits: 0,
    result: [],
    filters: [],
    filtersUsed: [],
  }
  searchRequestObject: ISearchRequest = {
    filters: {},
    query: '',
    pageNo: 0,
    pageSize: 1,
    locale: [],
    sort: [],
    instanceCatalog: true,
    isStandAlone: true,
  }
  visible = true
  selectable = true
  removable = true
  addOnBlur = true
  checked = false
  skillId = ''
  searchResultText = ''
  skillLevel = ''
  skillLevelId = ''
  skillName = ''
  searchTitle = ''
  showLevel = false
  filteredOptions: ISkillList[] = []
  skillControl = new FormControl()
  @ViewChild('selectContent', { static: true })
  selectContentMessage!: ElementRef<any>
  @ViewChild('successContent', { static: true })
  successContentMessage!: ElementRef<any>
  @ViewChild('emptyContent', { static: true })
  emptyContentMessage!: ElementRef<any>
  @ViewChild('addContent', { static: true })
  addContentMessage!: ElementRef<any>
  @ViewChild('existsContent', { static: true })
  existsContentMessage!: ElementRef<any>
  @ViewChild('skillContent', { static: true })
  skillContentMessage!: ElementRef<any>
  @ViewChild('skillLevelContent', { static: true })
  skillLevelContentMessage!: ElementRef<any>
  lexIdList: ICourseObj[] = []
  selectedType = 'Mandatory'
  courseType = ['Pre-requisites', 'Mandatory', 'Desirable']
  chipList: any[] = []
  languageSearch: string[] = []
  suggestedFilters: ISuggestedFilters[] = []
  selectedLevel = ''
  levelList = []
  constructor(
    private configSvc: ConfigurationsService,
    private contentSvc: WidgetContentService,
    private router: Router,
    private route: ActivatedRoute,
    private searchSvc: SearchServService,
    private skillSrv: SkillService,
    public matSnackBar: MatSnackBar,
  ) {
    this.skillControl.valueChanges.pipe(distinctUntilChanged()).subscribe((value: string) => {
      this.autoCompleteSkills(value, 'skill')
    })
    const isAutoCompleteAllowed = this.route.snapshot.data.pageData.data.search.isAutoCompleteAllowed
    if (typeof isAutoCompleteAllowed === 'undefined' ||
      (typeof isAutoCompleteAllowed === 'boolean' && isAutoCompleteAllowed)) {
      this.query.valueChanges.pipe(
        debounceTime(200),
        distinctUntilChanged(),
      ).subscribe(q => {
        this.searchQuery.q = q
        this.getAutoCompleteResults()
      })
    }
  }

  search(searchText?: string) {
    this.searchTitle = searchText || ''
    this.searchResultText = searchText || ''
    const req: NSSearch.ISearchV6Request = {
      filters: [{
        andFilters: [{
          contentType: [NsContent.EContentTypes.COURSE, NsContent.EContentTypes.RESOURCE, NsContent.EContentTypes.MODULE],
        }],
      }],
      query: this.searchTitle || this.searchQuery.q,
      pageNo: 0,
      pageSize: 1,
      locale: [this.searchQuery.l],
      sort: [],
      isStandAlone: true,
    }
    this.fetchSearchContents = 'fetching'
    this.contentSvc.searchV6(req).subscribe(
      (response: NSSearch.ISearchV6ApiResult) => {
        if (response) {
          this.searchResults = response
          this.fetchSearchContents = 'done'
        }

      },
      _ => {
        this.fetchSearchContents = 'error'
      })
  }
  autoCompleteSkills(value: string, field: string) {
    this.skillSrv.autoCompleteSkills(value, field).subscribe((res: any) => {
      this.filteredOptions = []
      this.filteredOptions = res
    })
  }
  onAddCourse() {
    if (this.searchResults.result.length > 0) {
      if (this.selectedType !== '') {
        this.searchResults.result.forEach((res: any) => {
          if (this.searchResultText === res.name) {
            if (this.chipList.findIndex((skillObj: any) => skillObj.identifier === res.identifier) === -1) {
              this.chipList.push({ ...res, course_type: this.selectedType })
            } else {
              this.matSnackBar.open(this.selectContentMessage.nativeElement.value)
            }
          } else {
            this.matSnackBar.open(this.existsContentMessage.nativeElement.value)
          }
        })
      } else {
        this.matSnackBar.open(this.emptyContentMessage.nativeElement.value)
      }
    } else {
      this.matSnackBar.open(this.addContentMessage.nativeElement.value)
    }
  }
  remove(chip: any): void {
    this.chipList = this.chipList.filter((s: any) => s.identifier !== chip.identifier)
  }
  createLearningPath() {
    let courseObj = {
      sequence_id: 0,
      course_type: '',
      content_id: '',
      content_name: '',
    }
    this.chipList.forEach((chip: any) => {
      courseObj = {
        sequence_id: this.chipList.indexOf(chip),
        course_type: chip.course_type,
        content_id: chip.identifier,
        content_name: chip.name,
      }
      this.lexIdList.push(courseObj)
    })
    this.levelList.forEach((level: any) => {
      if (level.skill_level === this.selectedLevel) {
        this.skillLevelId = level.skill_level_id
      }
    })
    if (this.skillId !== '' && this.skillLevelId !== '') {
      const reqObj = {
        skill_id: this.skillId,
        skill_level_id: this.skillLevelId,
        course_list: this.lexIdList,
      }
      this.skillSrv.addLearningPath(reqObj).subscribe(() => {
        this.matSnackBar.open(this.successContentMessage.nativeElement.value)
      })
    } else {
      this.matSnackBar.open(this.skillContentMessage.nativeElement.value)
    }
  }
  getSkillDetails(skillObj: ISkillList) {
    this.skillId = skillObj.skill_id
    this.skillSrv.skillQuotient(skillObj.skill_id).subscribe((response: any) => {
      this.levelList = response.skill_quotient.skill_levels
      this.showLevel = true
    })
  }
  searchWithFilter(filter: any): void {
    const objType = filter.contentType ? { contentType: [filter.contentType] } :
      filter.resourceType ? { resourceType: [filter.resourceType] } : filter.combinedType === 'learningContent' ?
        { contentType: ['Collection', 'Learning Path', 'Course'] } : ''
    this.router.navigate(['/app/search/home'], {
      queryParams: { lang: this.searchQuery.l, q: this.searchQuery.q },
    }).then(() => {
      this.router.navigate(['/app/search/learning'], {
        queryParams: {
          q: this.searchQuery.q,
          lang: this.searchQuery.l,
          f: JSON.stringify(objType),
        },
      })
    })
  }

  getActivateLocale(): string {
    const locale = (this.configSvc.activeLocale && this.configSvc.activeLocale.locals[0]) || 'en'
    return this.searchSvc.getLanguageSearchIndex(locale)
  }

  getAutoCompleteResults(): void {
    this.searchSvc.searchAutoComplete(this.searchQuery).then((results: ISearchAutoComplete[]) => {
      this.autoCompleteResults = results
    }).catch(() => {

    })
  }

  searchLanguage(lang: string): void {
    this.router.navigate([], {
      queryParams: { lang, q: this.searchQuery.q },
      queryParamsHandling: 'merge',
      relativeTo: this.route.parent,
    })
  }

  ngOnInit() {
    // this.skillId = this.route.snapshot.params.id
    // this.skillLevelId = this.route.snapshot.params.levelId
    // this.skillSrv.skillQuotient(this.skillId).subscribe((response: any) => {
    //   this.skillName = response.skill_quotient.skill_name
    //   response.skill_quotient.skill_levels.forEach((level: any) => {
    //     if (this.skillLevelId === level.skill_level_id) {
    //       this.skillLevel = level.skill_level
    //     }
    //   })
    //   this.apiFetchStatus = 'done'
    // })
    this.searchSvc.getSearchConfig().then(res => {
      this.suggestedFilters = res.search && res.search.suggestedFilters

    })
  }
}
