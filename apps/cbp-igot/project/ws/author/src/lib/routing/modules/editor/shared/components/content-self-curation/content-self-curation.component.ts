import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout'
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core'
import { NSContent } from '@ws/author/src/lib/interface/content'
import { map } from 'rxjs/operators'
import { LoaderService } from '../../../../../../services/loader.service'
import { EditorContentService } from '../../../services/editor-content.service'
/* tslint:disable */
import _ from 'lodash'
import { NSISelfCuration } from '../../../../../../interface/self-curation'
import { SelfCurationService } from '../../services/self-curation.service'
/* tslint:enable */
// import { AuthInitService } from '../../../../../../services/init.service'
@Component({
  selector: 'ws-auth-content-self-curation',
  templateUrl: './content-self-curation.component.html',
  styleUrls: ['./content-self-curation.component.scss'],
  /* tslint:disable */
  encapsulation: ViewEncapsulation.None,
  /* tslint:enable */
})
export class ContentSelfCurationComponent implements OnInit, OnDestroy, AfterViewInit {
  contentMeta!: NSContent.IContentMeta[]
  @Output() data = new EventEmitter<string>()
  @Input() isSubmitPressed = false
  @Input() nextAction = 'done'
  @Input() stage = 1
  @Input() type = ''
  @Input() parentContent: string | null = null
  // qualityForm!: FormGroup
  currentContent!: string
  viewMode = 'meta'
  mimeTypeRoute = ''
  qualityData!: NSISelfCuration.ISelfCurationData[]
  isResultExpend = false
  selectedKey = ''
  selectedIndex = 0
  lastQ = false
  displayResult = false
  selectedQIndex = 0
  leftmenudata!: any[]
  pdfCount = 0
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
  constructor(
    private contentService: EditorContentService,
    // private activateRoute: ActivatedRoute,
    private breakpointObserver: BreakpointObserver,
    private loaderService: LoaderService,
    private curationService: SelfCurationService
    // private authInitService: AuthInitService,
    // private formBuilder: FormBuilder
  ) {
  }
  ngOnInit(): void {
    this.sidenavSubscribe()
    this.contentService.changeActiveCont.subscribe(data => {
      this.currentContent = data
      if (this.contentService.getUpdatedMeta(data).contentType !== 'Resource') {
        this.viewMode = 'meta'
      }
    })
    // this.qualityForm = new FormGroup({})
    // if (this.activateRoute.parent && this.activateRoute.parent.parent) {
    // this.leftmenues = _.get(this.activateRoute.parent.snapshot.data, 'questions')

    // }
    this.getMeta()

  }
  sidenavSubscribe() {
    this.mediumSizeBreakpoint$.subscribe(isLtMedium => {
      this.mediumScreen = isLtMedium
      this.sideBarOpened = !isLtMedium
    })
  }
  // logs(val: any) {
  //   console.log(val)
  // }
  getMeta() {
    // this.contentService.changeActiveCont.subscribe(data => {
    // if (this.contentMeta && this.canUpdate) {
    //   this.storeData()
    // }
    // this.contentMeta = this.contentService.getUpdatedMeta(data)
    _.set(this, 'contentMeta', _.map(this.contentService.originalContent))
    //   debugger
    // setTimeout(
    //   () => {
    this.getProgress()
    // },
    // 2000)
    // })
  }
  getProgress() {
    this.curationService.fetchresult(this.contentService.parentContent).subscribe(data => {
      this.qualityData = data
      this.pdfCount = this.qualityData ? this.qualityData.length : 0
      if (this.qualityData.length > 0) {
        this.leftmenudata = [{
          count: this.getCriticalIssues,
          critical: true,
          name: 'Critical Issues',
        },
        {
          count: this.getPotentialIssues,
          potential: true,
          name: 'Potential issues',
        }]
      }
    })
  }
  get getPotentialIssues(): number {
    if (this.qualityData && this.qualityData.length > 0) {
      return _.chain(this.qualityData).map(i => i.profanityWordList)
        .compact().flatten()
        .filter(i => i.category === 'offensive' || i.category === 'lightly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }
  get getCriticalIssues(): number {
    if (this.qualityData && this.qualityData.length > 0) {
      return _.chain(this.qualityData).map('profanityWordList')
        .compact().flatten()
        .filter(i => i.category === 'exptermly offensive')
        .sumBy('no_of_occurrence').value()
    }
    return 0
  }
  get getCleanIssues(): number {
    if (this.qualityData && this.qualityData.length > 0) {
      return _.chain(this.qualityData).map(i => {
        if (i.profanity_word_count === 0) {
          return i
        }
        return null
      }).compact().flatten()
        .value().length
    }
    return 0
  }
  get getProgressPercent(): number {
    if (this.qualityData && this.qualityData.length > 0) {
      const completed = _.chain(this.qualityData).map(i => i.completed)
        .compact().flatten()
        .value().length

      return parseFloat(((completed / this.qualityData.length) * 100).toFixed(2))
    }
    return 0
  }
  ngOnDestroy(): void {
    this.loaderService.changeLoad.next(false)
  }
  ngAfterViewInit(): void {
  }

  getResourseName(id: string) {
    if (id) {
      const resource = _.first(_.filter(this.contentMeta, i => (i.identifier).replace('.img', '') === id || i.identifier === id))
      return resource ? resource.name : 'Untitled Resource'
    }
    return 'Resource'
  }

  sidenavClose() {
    setTimeout(() => (this.leftArrow = true), 500)
  }

}
