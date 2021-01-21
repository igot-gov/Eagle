import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout'
import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core'
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { debounceTime, map } from 'rxjs/operators'
import { LoaderService } from '../../../../../../services/loader.service'
import { EditorContentService } from '../../../services/editor-content.service'
/* tslint:disable */
import _ from 'lodash'
// import { AuthInitService } from '../../../../../../services/init.service'
import { NSIQuality } from '../../../../../../interface/content-quality'
// import { SelfCurationService } from '../../services/self-curation.service'
import { ContentQualityService } from '../../services/content-quality.service'
import { ConfigurationsService } from '../../../../../../../../../../../library/ws-widget/utils/src/public-api'
import { ActivatedRoute } from '@angular/router'
import { AuthInitService } from '../../../../../../services/init.service'
import { MatSnackBar } from '@angular/material'

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
  fieldsToDisplay = ''
  mimeTypeRoute = ''
  isResultExpend = false
  showParentLoader = false
  selectedKey = ''
  questionData!: NSIQuality.IQuestionConfig[]
  qualityResponse!: NSIQuality.IQualityResponse
  selectedIndex = 0
  startQ = false
  lastQ = false
  displayResult = false
  selectedQIndex = 0
  minPassPercentage = 10
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
  // dataSource = ELEMENT_DATA
  constructor(
    // private valueSvc: ValueService,
    private changeDetector: ChangeDetectorRef,
    private contentService: EditorContentService,
    private activateRoute: ActivatedRoute,
    private _configurationsService: ConfigurationsService,
    private breakpointObserver: BreakpointObserver,
    private loaderService: LoaderService,
    private authInitService: AuthInitService,
    private formBuilder: FormBuilder,
    private _qualityService: ContentQualityService,
    private snackBar: MatSnackBar,
  ) {
    this.getJSON()
    if (this.authInitService.authAdditionalConfig.contentQuality) {
      this.minPassPercentage = this.authInitService.authAdditionalConfig.contentQuality.passPercentage
    }
    this.contentService.changeActiveCont.subscribe(data => {
      // this.currentContent = data.replace('.img', '')
      if (data) {
        this.currentContent = data
        this.fillResponseData()
      }
    })
  }
  ngOnInit(): void {
    this.sidenavSubscribe()

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
  romanize(num: number) {
    const numeralCodes = [['', 'i', 'ii', 'iii', 'iv', 'v', 'vi', 'vii', 'viii', 'ix'],         // Ones
    ['', 'x', 'xx', 'xxx', 'xl', 'l', 'lx', 'lxx', 'lxxx', 'xc'],   // Tens
    ['', 'c', 'cc', 'ccc', 'cd', 'd', 'dc', 'dcc', 'dccc', 'cm']]        // Hundreds
    let numeral = ''
    const digits = num.toString().split('').reverse()
    for (let i = 0; i < digits.length; i += 1) {
      // tslint:disable-next-line
      numeral = numeralCodes[i][parseInt(digits[i])] + numeral
    }
    return numeral
  }
  getJSON() {
    if (this.activateRoute.parent && this.activateRoute.parent.parent
      && this.activateRoute.parent.parent.snapshot && this.activateRoute.parent.parent.snapshot.data) {
      this.fieldsToDisplay = _.map(this.activateRoute.parent.parent.snapshot.data.qualityJSON.criteria, (cr, idx) => {
        // tslint:disable-next-line
        return ` ${this.romanize(parseInt(idx + 1))}) ${cr.criteria} `
      }).join(',')

      const qData = _.map(this.activateRoute.parent.parent.snapshot.data.qualityJSON.criteria, cr => {
        return {
          type: (cr.criteria || '').replace(' ', ''),
          name: cr.criteria,
          desc: cr.description || 'desc',
          questions: _.map(cr.qualifiers, (q, idx1: number) => {
            return {
              question: q.description,
              type: q.qualifier,
              position: idx1,
              options: _.map(q.options, op => {
                return {
                  name: op.name,
                  weight: op.name,
                  selected: false,
                }
              }),
            }
          }),
        }
      })
      qData.splice(0, 0, {
        name: 'Instructions',
        desc: 'Instructions',
        questions: [],
        type: 'instructions',
      })
      this.questionData = qData
    }
  }
  // logs(val: any) {
  //   console.log(val)
  // }
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
        type: v.type,
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
      const reqObj = {
        resourceId: this.currentContent,
        resourceType: 'content',
        userId: this._configurationsService.userProfile.userId,
        getLatestRecordEnabled: true,
      }
      this._qualityService.fetchresult(reqObj).subscribe((result: any) => {
        if (result && result.result && result.result.resources) {
          const rse = result.result.resources || []
          if (rse.length === 1) {
            this.qualityResponse = rse[0]
            this.displayResult = true
            this.changeDetector.detectChanges()
          }
        }
      })
    }
  }
  get UnQualidiedSections() {
    const sections: NSIQuality.ICriteriaModels[] = []
    _.map(this.qualityResponse.criteriaModels, qr => {
      if (!qr.qualifiedMinCriteria) {
        sections.push(qr)
      }
    })
    return sections
  }

  checkUnQualidied(idx: number): boolean {
    const obj = this.qualityResponse.criteriaModels[idx]
    return obj && obj.qualifiedMinCriteria
  }

  get getQualityPercent() {
    const score = this.qualityResponse.finalWeightedScore || 0
    return score.toFixed(2)
  }
  getFirstHeadingName(idx: number) {
    return this.qualityResponse.criteriaModels[idx || 0].criteria
  }
  getTableData(idx: number) {
    return _.map(this.qualityResponse.criteriaModels[idx].qualifiers, row => {
      return row
    })
  }

  download() {
    const data = _.map(this.qualityResponse.criteriaModels, ii => ii.qualifiers)
    this._qualityService.getFile({ ...data }, `Content-Quality-Report`, true)
  }
  start() {
    if (Object.keys(this.contentService.originalContent).length > 1) {
      if (this.questionData && this.questionData[1] && this.questionData[1].type) {
        this.selectedIndex = 1
        this.selectedKey = this.questionData[1].type
        this.startQ = true
        // this.createForm()
      }
    } else {
      this.snackBar.open(`To start content quality check, minimum one resourse/child is required`)
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
        this.selectedQIndex = this.questionData[this.selectedIndex].questions ?
          this.questionData[this.selectedIndex].questions.length - 1 : 0
        this.lastQ = false
      }
    }
  }
  get getCurrentQuestions() {
    return this.questionData[this.selectedIndex].questions
  }
  isTouched(key: string, index: number) {
    // let data = this.qualityForm.value
    let returnValue = false
    if (key === 'menu') {
      if (this.displayResult) {
        returnValue = true
      } else if (this.selectedIndex !== 0 && index === 0) {
        returnValue = true
      } else if (this.selectedIndex > index) {
        returnValue = true
      }
    } else if (this.selectedQIndex !== 0 && index === 0) {
      returnValue = true
    } else if (this.selectedQIndex > index) {
      returnValue = true
    }
    return returnValue
  }
  getCount(index: number): string {
    let res = '_000'
    if (index <= 9) {
      res = `_00${index}`
    } else if (index > 9 && index <= 99) {
      res = `_0${index}`
    } else if (index > 99 && index <= 999) {
      res = `_${index}`
    }
    return res
  }
  submitResult(qualityForm: any) {
    this.showParentLoader = true

    if (qualityForm && this._configurationsService.userProfile) {
      // todo:  start loader
      /* tslint:disable */
      // console.log(qualityForm)
      /* tslint:disable */
      let responses = _.map(_.get(qualityForm, 'questionsArray'), (p: NSIQuality.IQuestionConfig, cid: number) => {
        return {
          criteria: p.name,
          qualifiers: _.map(_.get(p, 'ques'), (q: NSIQuality.IQualityQuestion, qid: number) => {
            const defaultOp = this.questionData[cid].questions[qid].options
            const index = defaultOp.length <= 3 ? 1 : defaultOp.length - 2
            const defaultNo = this.questionData[cid].questions[qid].options[index].weight
            return {
              name: q.type,
              evaluated: q.options || defaultNo
            }
          })
        }
      })
      responses.splice(0, 1)

      const data = {
        resourceId: this.currentContent,
        templateId: 'content_scoring_template',
        resourceType: 'content',
        userId: this._configurationsService.userProfile.userId,
        criteriaModels: responses
      }
      // console.log(data)

      this._qualityService.postResponse(data).subscribe(response => {
        if (response) {
          setTimeout(() => {
            this.fillResponseData()
            this.showParentLoader = false
          },
            1500
          )
          this.startQ = false
        } else {
          // need to check tost
          // this.displayResult = true
          this.showParentLoader = false
        }
      })
    } else {
      this.showParentLoader = false
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
  showMinDialogue() {
    this.snackBar.open(`To proceed further minimum quality score must be  ${this.minPassPercentage}% or greater, and need to qualify in all the sections`)
  }

  selectMenu(key: string, index: number) {
    if (this.startQ) {
      this.selectedKey = key
      this.selectedIndex = index
      this.selectedQIndex = 0
    }
  }

  isLinkActive(key: string, index: number) {
    if (!this.selectedIndex && this.selectedQIndex === 0 && index === 0) {
      return true
    }
    if (key && index >= 0) {
      if (this.selectedKey === key) {
        return true
      }
      return false
    }
    return false
  }
}
