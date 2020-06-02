import { Component, OnInit } from '@angular/core'
import { Router, ActivatedRoute } from '@angular/router'
import { MatSnackBar } from '@angular/material'
import { ILpHomeTile, ICdpLandTile } from '../../models/career-development-path.model'
import { ValueService, ConfigurationsService, NsPage } from '@ws-widget/utils/src/public-api'
import { Subscription } from 'rxjs/internal/Subscription'
import { WidgetContentService, NsContent, NsContentStripMultiple, ROOT_WIDGET_CONFIG, NsError } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
@Component({
  selector: 'ws-app-cdp-landing',
  templateUrl: './cdp-landing.component.html',
  styleUrls: ['./cdp-landing.component.scss'],
})
export class CdpLandingComponent implements OnInit {
  tiles1: ILpHomeTile[] = this.activatedRoute.snapshot.data.pageData.data.first.tileData1
  tiles2: ILpHomeTile[] = this.activatedRoute.snapshot.data.pageData.data.first.tileData2
  tiles3: ILpHomeTile[] = this.activatedRoute.snapshot.data.pageData.data.first.tileData3
  tiles = [
    ...this.activatedRoute.snapshot.data.pageData.data.first.tileData1,
    ...this.activatedRoute.snapshot.data.pageData.data.first.tileData2,
    ...this.activatedRoute.snapshot.data.pageData.data.first.tileData3,
  ]
  defaultThumbnail = ''
  tileObj = {
    titleKey: '',
    displayName: '',
    cols: 2,
    rows: 1,
    imageUrl: '',
    width: '270px',
    type: '',
    opacity: 1,
    greyScale: 'grayscale(0%)',
  }
  courseIds: string[] = this.activatedRoute.snapshot.data.pageData.data.first.courseIds
  tiles4: ICdpLandTile[] = this.activatedRoute.snapshot.data.pageData.data.first.tileData6
  finalTile = [
    // ...this.activatedRoute.snapshot.data.pageData.data.first.tileData5,
    ...this.activatedRoute.snapshot.data.pageData.data.first.tileData6,
  ]
  widgetResolverCourseData: NsWidgetResolver.IRenderConfigWithTypedData<
    NsContentStripMultiple.IContentStripMultiple
  > | null = null
  errorWidget: NsWidgetResolver.IRenderConfigWithTypedData<NsError.IWidgetErrorResolver> = {
    widgetType: ROOT_WIDGET_CONFIG.errorResolver._type,
    widgetSubType: ROOT_WIDGET_CONFIG.errorResolver.errorResolver,
    widgetData: {
      errorType: 'internalServer',
    },
  }
  courseTile: ILpHomeTile[] = this.activatedRoute.snapshot.data.pageData.data.first.tileData4
  show = false
  value = ''
  searchResult: any
  courseSearchResult: any
  screenSizeIsLtMedium = false
  defaultSideNavBarOpenedSubscription: Subscription | null = null
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  isLtMedium$ = this.valueSvc.isLtMedium$
  filteredItems: ILpHomeTile[] = []
  constructor(
    private router: Router,
    public matSnackBar: MatSnackBar,
    private valueSvc: ValueService,
    private configSvc: ConfigurationsService,
    private activatedRoute: ActivatedRoute,
    private contentSvc: WidgetContentService,
  ) { }

  ngOnInit() {
    this.assignCopy()
    this.defaultSideNavBarOpenedSubscription = this.isLtMedium$.subscribe((isLtMedium: boolean) => {
      this.screenSizeIsLtMedium = isLtMedium
    })
    const instanceConfig = this.configSvc.instanceConfig
    if (instanceConfig) {
      this.defaultThumbnail = instanceConfig.logos.defaultContent
    }
    this.contentSvc
      .search({
        filters: {
          contentType: [NsContent.EContentTypes.LEARNING_JOURNEY],
        },
      })
      .subscribe((response: any) => {
        this.searchResult = response.result
        // this.getSearchData(this.searchResult)
      })
    // this.contentSvc
    //   .search({
    //     filters: {
    //       // sourceShortName: ['Learning World'],
    //       contentType: [NsContent.EContentTypes.LEARNING_JOURNEY],
    //     },
    //   })
    //   .subscribe((searchResponse: any) => {
    //     this.courseSearchResult = searchResponse.result
    //     this.getCourseSearchData(this.courseSearchResult)

    //   })

    this.widgetResolverCourseData = {
      widgetData: {
        strips: [
          {
            key: 'learning_path_courses',
            title: '',
            request: {
              ids: this.courseIds,
            },
          },
        ],
        loader: true,
      },
      widgetType: ROOT_WIDGET_CONFIG.contentStrip._type,
      widgetSubType: ROOT_WIDGET_CONFIG.contentStrip.multiStrip,
      widgetHostClass: 'block sm:-mx-10 -mx-6',
    }
  }

  getCourseSearchData(courseSearchResult: any) {
    courseSearchResult.forEach((journey: any) => {
      this.tileObj = {
        titleKey: journey.identifier,
        displayName: journey.name === 'Leadership' ? 'Leadership & Management' : journey.name,
        cols: 2,
        rows: 1,
        imageUrl: journey.appIcon,
        width: '250px',
        type: 'course',
        opacity: 1,
        greyScale: 'grayscale(0%)',
      }
      this.courseTile.push(this.tileObj)
    })
  }
  getSearchData(searchResult: any) {
    searchResult.forEach((journey: any) => {
      this.tileObj = {
        titleKey: journey.identifier,
        displayName: journey.name === 'Leadership' ? 'Leadership & Management' : journey.name,
        cols: 2,
        rows: 1,
        imageUrl: journey.appIcon,
        width: '250px',
        type: journey.resourceType === 'Dynamic Learning Paths' ? 'bubble' : 'tile',
        opacity: 1,
        greyScale: 'grayscale(0%)',
      }
        // this.finalTile.push(this.tileObj)
    })
  }
  getPathDetails(obj: ILpHomeTile | ICdpLandTile) {
    if (obj.type === 'bubble') {
      if (this.activatedRoute.snapshot.data.pageData.data.dlp[obj.titleKey]) {
        this.router.navigate([`/app/learning-journey/dlp/${obj.titleKey}/0`])
      } else {
        this.openSnackBar()
      }
    } else if (obj.type === 'link') {
      // tslint:disable-next-line:max-line-length
      window.open(`${obj.link}`, '_blank')
    } else {
      if (this.activatedRoute.snapshot.data.pageData.data.second[obj.titleKey].length > 0) {
        this.router.navigate(['/app/learning-journey/cdp', obj.titleKey])
      } else {
        this.openSnackBar()
      }
    }
  }

  routeToCourse(titleKey: string) {
    this.router.navigate(['/app/toc/', titleKey, 'overview'])
  }
  openSnackBar() {
    this.matSnackBar.open('Coming Soon...', 'close', {
      duration: 2000,
    })
  }
  assignCopy() {
    this.filteredItems = Object.assign([], this.tiles)
  }
  filterItem(value: string) {
    if (!value) {
      this.assignCopy()
    }
    this.finalTile = Object.assign([], this.tiles).filter(
      (item: ILpHomeTile) => item.displayName.toLowerCase().indexOf(value.toLowerCase()) > -1,
    )
  }
}
