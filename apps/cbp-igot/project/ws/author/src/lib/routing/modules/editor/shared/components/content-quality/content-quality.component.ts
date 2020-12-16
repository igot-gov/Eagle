import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout'
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core'
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { debounceTime, map } from 'rxjs/operators'
import { LoaderService } from '../../../../../../services/loader.service'
import { EditorContentService } from '../../../services/editor-content.service'
/* tslint:disable */
import _ from 'lodash'
import { AuthInitService } from '../../../../../../services/init.service'
import { NSIQuality } from '../../../../../../interface/content-quality'
import { SelfCurationService } from '../../services/self-curation.service'
import { ContentQualityService } from '../../services/content-quality.service'
import { ConfigurationsService } from '../../../../../../../../../../../library/ws-widget/utils/src/public-api'
import { ActivatedRoute } from '@angular/router'
// import { ValueService } from '../../../../../../../../../../../library/ws-widget/utils/src/public-api'
/* tslint:enable */
const ELEMENT_DATA: any[] = [
  { response: 1, name: 'Hydrogen', score: 1.0079, help: 'H' },
  { response: 2, name: 'Helium', score: 4.0026, help: 'He' },
  { response: 3, name: 'Lithium', score: 6.941, help: 'Li' },
  { response: 4, name: 'Beryllium', score: 9.0122, help: 'Be' },
  { response: 5, name: 'Boron', score: 10.811, help: 'B' },
  { response: 6, name: 'Carbon', score: 12.0107, help: 'C' },
  { response: 7, name: 'Nitrogen', score: 14.0067, help: 'N' },
  { response: 8, name: 'Oxygen', score: 15.9994, help: 'O' },
  { response: 9, name: 'Fluorine', score: 18.9984, help: 'F' },
  { response: 10, name: 'Neon', score: 20.1797, help: 'Ne' },
]
@Component({
  selector: 'ws-auth-content-quality',
  templateUrl: './content-quality.component.html',
  styleUrls: ['./content-quality.component.scss'],
  /* tslint:disable */
  encapsulation: ViewEncapsulation.None,
  /* tslint:enable */
})
export class ContentQualityComponent implements OnInit, OnDestroy, AfterViewInit {
  contentMeta!: NSContent.IContentMeta
  @Output() data = new EventEmitter<string>()
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  @Input() parentContent: string | null = null
  qualityForm!: FormGroup
  currentContent!: string
  viewMode = 'meta'
  mimeTypeRoute = ''
  isResultExpend = false
  selectedKey = ''
  questionData!: NSIQuality.IQuestionConfig[]
  selectedIndex = 0
  lastQ = false
  displayResult = false
  selectedQIndex = 0
  /**for side nav */
  mediumScreen = false
  sideBarOpened = false
  mediumSizeBreakpoint$ = this.breakpointObserver
    .observe([Breakpoints.XSmall, Breakpoints.Small])
    .pipe(map((res: BreakpointState) => res.matches))
  mode$ = this.mediumSizeBreakpoint$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  leftArrow = true
  /**for side nav: END */
  menus!: any
  wData: any
  displayedColumns: string[] = ['name', 'response', 'score', 'help']
  dataSource = ELEMENT_DATA
  constructor(
    // private valueSvc: ValueService,
    private contentService: EditorContentService,
    private activateRoute: ActivatedRoute,
    private _configurationsService: ConfigurationsService,
    private breakpointObserver: BreakpointObserver,
    private loaderService: LoaderService,
    private authInitService: AuthInitService,
    private formBuilder: FormBuilder,
    private _qualityService: ContentQualityService
  ) {
    this.getJSON()
    this.fillResponseData()
  }
  ngOnInit(): void {
    this.sidenavSubscribe()
    this.contentService.changeActiveCont.subscribe(data => {
      this.currentContent = data
      if (this.contentService.getUpdatedMeta(data).contentType !== 'Resource') {
        this.viewMode = 'meta'
      }
    })
    this.qualityForm = new FormGroup({})
    this.createForm()
    // if (this.activateRoute.parent && this.activateRoute.parent.parent) {
    // this.leftmenues = _.get(this.activateRoute.parent.snapshot.data, 'questions')

    // }
  }
  sidenavSubscribe() {
    this.mediumSizeBreakpoint$.subscribe(isLtMedium => {
      this.mediumScreen = isLtMedium
      this.sideBarOpened = !isLtMedium
    })
  }
  getJSON() {
    if (this.activateRoute.parent && this.activateRoute.parent.parent
      && this.activateRoute.parent.parent.snapshot && this.activateRoute.parent.parent.snapshot.data) {
      this.questionData = _.map(this.activateRoute.parent.parent.snapshot.data.qualityJSON.criteria, cr => {
        return {
          type: (cr.criteria || '').replace(' ', ''),
          name: cr.criteria,
          desc: '',
          questions: _.map(cr.qualifiers, (q, idx1: number) => {
            return {
              question: q.description,
              position: idx1,
              options: _.map(q.options, op => {
                return {
                  name: op.name,
                  weight: op.name,
                  selected: false
                }
              }),
            }
          })
        }
      })
    }
  }
  logs(val: any) {
    console.log(val)
  }
  createForm() {
    this.qualityForm = this.formBuilder.group({
      questionsArray: this.formBuilder.array([]),
    })
    if (this.questionData && this.questionData.length) {
      this.questionData.forEach((v: NSIQuality.IQuestionConfig) => {
        if (v) {
          this.createQuestionControl(v)
        }
      })
    }
    this.qualityForm.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      // this.value.emit(JSON.parse(JSON.stringify(this.qualityForm.value)))
    })
  }

  createQuestionControl(questionObj: NSIQuality.IQuestionConfig) {
    // const noWhiteSpace = new RegExp('\\S')
    const newControl = this.formBuilder.group({
      name: new FormControl(questionObj.name),
      type: new FormControl(questionObj.type),
      desc: new FormControl(questionObj.desc),
      ques: new FormArray(this.createQuesControl(questionObj.questions)),
    })
    const optionsArr = this.qualityForm.controls['questionsArray'] as FormArray
    optionsArr.push(newControl)
  }
  createQuesControl(optionObj: NSIQuality.IQualityQuestion[]) {
    return optionObj.map(v => {
      return this.formBuilder.group({
        questionText: [v.question],
        questionPosition: [v.position],
        options: new FormControl(),
      })
    })
  }
  // createOptionControl(optionObj: IQualityQuestionOption[]) {
  //   return optionObj.map(v => {
  //     return this.formBuilder.group({
  //       optionName: [v.name],
  //       OptionValue: [v.weight],
  //       optionSelected: [v.selected || false],
  //     })
  //   })
  // }

  ngOnDestroy(): void {
    this.loaderService.changeLoad.next(false)
  }
  ngAfterViewInit(): void {
  }
  sidenavClose() {
    setTimeout(() => (this.leftArrow = true), 500)
  }
  fillResponseData() {
    if (this._configurationsService.userProfile) {
      let reqObj = {
        resourceId: this.currentContent,
        resourceType: 'content',
        userId: this._configurationsService.userProfile.userId,
        identifier: 'lex_score_1608039795785'
      }
      this._qualityService.fetchresult(reqObj).subscribe(result => {
        if (result) {

        }
      })
    }
  }
  selectMenu(key: string, index: number) {
    this.selectedKey = key
    this.selectedIndex = index
    this.selectedQIndex = 0
  }
  isLinkActive(key: string, index: number) {
    // if (!this.selectedKey && index === 0) {
    //   return true
    // }
    if (key && index >= 0) {
      if (this.selectedKey === key) {
        return true
      }
      return false
    }
    return false
  }
  start() {
    if (this.questionData && this.questionData[1] && this.questionData[1].type) {
      this.selectedIndex = 1
      this.selectedKey = this.questionData[1].type
      // this.createForm()
    }
  }
  nextQ() {
    const totalset = this.questionData.length - 1
    const currentQSet = this.questionData[this.selectedIndex].questions.length - 1
    if (this.selectedQIndex === currentQSet) {
      if (this.selectedIndex === totalset) {
        // next tab
        this.lastQ = true
      } else {
        this.selectedIndex += 1
        this.selectedKey = this.questionData[this.selectedIndex].type
        this.selectedQIndex = 0
      }
    } else {
      this.selectedQIndex += 1
      if (this.selectedIndex === totalset && currentQSet === this.selectedQIndex) {
        this.lastQ = true
      }
    }
  }
  previousQ() {
    if (this.selectedQIndex > 0) {
      this.selectedQIndex -= 1
      this.lastQ = false
    } else {
      if (this.selectedIndex > 0) {
        this.selectedIndex -= 1
        this.selectedKey = this.questionData[this.selectedIndex].type
        this.selectedQIndex = 0
        this.lastQ = false
      }
    }
  }
  get getCurrentQuestions() {
    return this.questionData[this.selectedIndex].questions
  }
  isTouched(key: string, index: number) {
    // let data = this.qualityForm.value
    if (key === 'menu') {
      if (this.displayResult) {
        return true
      } else if (this.selectedIndex != 0 && index === 0) {
        return true
      } else if (this.selectedIndex > index) {
        return true
      }
    } else {
      if (this.selectedQIndex != 0 && index === 0) {
        return true
      } else if (this.selectedQIndex > index) {
        return true
      }
    }
    return false
  }
  getCount(index: number): string {
    if (index <= 9) {
      return `_00${index}`
    } else if (index > 9 && index <= 99) {
      return `_0${index}`
    } else if (index > 99 && index <= 999) {
      return `_${index}`
    }
    return `_000`
  }
  submitResult(qualityForm: any) {
    if (qualityForm && this._configurationsService.userProfile) {
      /* tslint:disable */
      console.log(qualityForm)
      /* tslint:disable */
      let responses = _.map(_.get(qualityForm, 'questionsArray'), (p: NSIQuality.IQuestionConfig) => {
        return {
          criteria: p.name,
          qualifiers: _.map(_.get(p, 'ques'), (q: NSIQuality.IQualityQuestion, qid: number) => {
            return {
              name: p.name.replace(/&/, '').replace(/  /g, '_').replace(/ /g, '_') + this.getCount(qid + 1),
              evaluated: q.options
            }
          })
        }
      })

      const data = {
        resourceId: this.currentContent,
        templateId: 'content_scoring_template',
        resourceType: 'content',
        userId: this._configurationsService.userProfile.userId,
        criteriaModels: responses
      }
      console.log(data)

      // this._qualityService.postResponse(data).subscribe(response => {
      //   if (response) {
      //     this.displayResult = true
      //   } else {
      //     // need to check
      //     this.displayResult = true
      //   }
      // })
    }

  }
  showHideResult() {
    this.isResultExpend = !this.isResultExpend
  }
  takeAgain() {
    this.displayResult = false
    this.lastQ = false
    this.selectedIndex = 0
    this.selectedQIndex = 0
    this.selectedKey = this.questionData[0].type
  }
}
