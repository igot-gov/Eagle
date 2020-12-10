import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout'
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core'
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms'
import { ActivatedRoute } from '@angular/router'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { debounceTime, map } from 'rxjs/operators'
import { LoaderService } from '../../../../../../services/loader.service'
import { EditorContentService } from '../../../services/editor-content.service'
/* tslint:disable */
import _ from 'lodash'
import { AuthInitService } from '../../../../../../services/init.service'
import { IQualityQuestion, IQualityQuestionOption, IQuestionConfig } from '../../../../../../interface/content-quality'
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
  questionData!: IQuestionConfig[]
  selectedIndex = 0
  lastQ = false
  displayResult = false
  selectedQIndex = 0
  mediumScreen = false
  sideBarOpened = true
  mediumSizeBreakpoint$ = this.breakpointObserver
    .observe([Breakpoints.XSmall, Breakpoints.Small])
    .pipe(map((res: BreakpointState) => res.matches))
  mode$ = this.mediumSizeBreakpoint$.pipe(map(isMedium => (isMedium ? 'over' : 'side')))
  leftArrow = true
  menus!: any
  wData: any
  displayedColumns: string[] = ['name', 'response', 'score', 'help']
  dataSource = ELEMENT_DATA
  constructor(
    private contentService: EditorContentService,
    private activateRoute: ActivatedRoute,
    private breakpointObserver: BreakpointObserver,
    private loaderService: LoaderService,
    private authInitService: AuthInitService,
    private formBuilder: FormBuilder
  ) {
    this.fillData()
  }
  ngOnInit(): void {
    this.contentService.changeActiveCont.subscribe(data => {
      this.currentContent = data
      if (this.contentService.getUpdatedMeta(data).contentType !== 'Resource') {
        this.viewMode = 'meta'
      }
    })
    if (this.activateRoute.parent && this.activateRoute.parent.parent) {
      // this.leftmenues = _.get(this.activateRoute.parent.snapshot.data, 'questions')
      this.createForm()
    }
  }
  createForm() {
    this.qualityForm = this.formBuilder.group({
      questions: this.formBuilder.array([]),
    })
    if (this.questionData && this.questionData[this.selectedIndex] && this.questionData[this.selectedIndex].questions.length) {
      this.questionData[this.selectedIndex].questions.forEach((v: IQualityQuestion) => {
        if (v) {
          this.createQuestionControl(v)
        }
      })
    }
    this.qualityForm.valueChanges.pipe(debounceTime(100)).subscribe(() => {
      // this.value.emit(JSON.parse(JSON.stringify(this.qualityForm.value)))
    })
  }

  createQuestionControl(questionObj: IQualityQuestion) {
    const noWhiteSpace = new RegExp('\\S')
    const newControl = this.formBuilder.group({
      question: [questionObj.question || '', [Validators.required, Validators.pattern(noWhiteSpace)]],
      position: [questionObj.position || 0],
      options: this.createOptionControl(questionObj.options),
    })
    const optionsArr = this.qualityForm.controls['questions'] as FormArray
    optionsArr.push(newControl)
  }
  createOptionControl(optionObj: IQualityQuestionOption[]) {
    return optionObj.map(v => {
      return this.formBuilder.group({
        name: [v.name],
        weight: [v.weight],
        selected: [v.selected || false, [Validators.required]],
      })
    })
  }
  ngOnDestroy(): void {
    this.loaderService.changeLoad.next(false)

  }
  ngAfterViewInit(): void {
  }
  sidenavClose() {
    setTimeout(() => (this.leftArrow = true), 500)
  }
  fillData() {
    if (this.authInitService.contentQuality && this.authInitService.contentQuality.questionsData) {
      this.questionData = this.authInitService.contentQuality.questionsData
      // this.menus = _.map(this.authInitService.contentQuality.questionsData, q => {
      //   return {
      //     name: q.name,
      //     desc: q.desc,
      //     key: q.type
      //   }
      // })
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
  submitResult(qualityForm: any) {
    if (qualityForm) {

    }
    this.displayResult = true
  }
  showHideResult() {
    this.isResultExpend = !this.isResultExpend
  }
  takeAgain() {
    this.displayResult = false
    this.selectedIndex = 0
    this.selectedQIndex = 0
    this.selectedKey = this.questionData[0].type
  }
}
