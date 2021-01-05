import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout'
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewEncapsulation } from '@angular/core'
import { FormGroup } from '@angular/forms'
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
  qualityForm!: FormGroup
  currentContent!: string
  viewMode = 'meta'
  mimeTypeRoute = ''
  isResultExpend = false
  selectedKey = ''
  selectedIndex = 0
  lastQ = false
  displayResult = false
  selectedQIndex = 0
  leftmenudata!: any[]
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
    this.qualityForm = new FormGroup({})
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
    //   setTimeout(
    //     () => {
    this.getProgress()
    //     },
    //     2000)
    // })
  }
  getProgress() {
    // this.leftmenudata = []
    const response: NSISelfCuration.ISelfCurationData[] = []
    if (this.contentMeta && this.contentMeta) {
      _.each(this.contentMeta, (element: NSContent.IContentMeta) => {
        if (element.artifactUrl && element.mimeType.indexOf('application/pdf') >= 0) {
          // response.push()
          this.curationService.fetchresult(element.identifier).subscribe(data => {
            console.log(data)
          })
        }
      })
    }
    if (response.length > 0) {
      // this.leftmenudata = _.map(response, (i: NSISelfCuration.ISelfCurationData[]) => _.first(i.).profanityWordList.length)
    }
  }
  ngOnDestroy(): void {
    this.loaderService.changeLoad.next(false)
  }
  ngAfterViewInit(): void {
  }
  sidenavClose() {
    setTimeout(() => (this.leftArrow = true), 500)
  }

}
