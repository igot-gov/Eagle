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
  selector: 'ws-auth-content-self-curation',
  templateUrl: './content-self-curation.component.html',
  styleUrls: ['./content-self-curation.component.scss'],
  /* tslint:disable */
  encapsulation: ViewEncapsulation.None,
  /* tslint:enable */
})
export class ContentSelfCurationComponent implements OnInit, OnDestroy, AfterViewInit {
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
  displayedColumns: string[] = ['name', 'response', 'score', 'help']
  dataSource = ELEMENT_DATA
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
    this.contentService.changeActiveCont.subscribe(data => {
      // if (this.contentMeta && this.canUpdate) {
      //   this.storeData()
      // }
      this.contentMeta = this.contentService.getUpdatedMeta(data)
      setTimeout(
        () => {
          this.getProgress()
        },
        2000)
    })
  }
  getProgress() {
    // this.leftmenudata = []
    const response: NSISelfCuration.ISelfCurationData[] = []
    if (this.contentMeta.children) {
      _.each(this.contentMeta.children, (element: NSContent.IContentMeta) => {
        response.push(this.curationService.getOriginalData(element.identifier))
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
