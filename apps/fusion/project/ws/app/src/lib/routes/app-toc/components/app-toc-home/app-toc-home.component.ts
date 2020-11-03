import { Component, OnDestroy, OnInit, AfterViewInit, AfterViewChecked, HostListener, ElementRef, ViewChild } from '@angular/core'
import { ActivatedRoute, Data } from '@angular/router'
import { NsContent, WidgetContentService } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ConfigurationsService, LoggerService, NsPage } from '@ws-widget/utils'
import { Subscription, Observable } from 'rxjs'
import { share } from 'rxjs/operators'
import { NsAppToc } from '../../models/app-toc.model'
import { AppTocService } from '../../services/app-toc.service'
import { SafeHtml, DomSanitizer } from '@angular/platform-browser'
import { AccessControlService } from '@ws/author/src/public-api'

export enum ErrorType {
  internalServer = 'internalServer',
  serviceUnavailable = 'serviceUnavailable',
  somethingWrong = 'somethingWrong',
}
@Component({
  selector: 'ws-app-app-toc-home',
  templateUrl: './app-toc-home.component.html',
  styleUrls: ['./app-toc-home.component.scss'],
})
export class AppTocHomeComponent implements OnInit, OnDestroy, AfterViewChecked, AfterViewInit {
  banners: NsAppToc.ITocBanner | null = null
  content: NsContent.IContent | null = null
  errorCode: NsAppToc.EWsTocErrorCode | null = null
  resumeData: NsContent.IContinueLearningData | null = null
  routeSubscription: Subscription | null = null
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  isCohortsRestricted = false
  sticky = false
  isInIframe = false
  forPreview = window.location.href.includes('/author/')
  analytics = this.route.snapshot.data.pageData.data.analytics
  errorWidgetData: NsWidgetResolver.IRenderConfigWithTypedData<any> = {
    widgetType: 'errorResolver',
    widgetSubType: 'errorResolver',
    widgetData: {
      errorType: 'internalServer',
    },
  }
  isAuthor = false
  authorBtnWidget: NsPage.INavLink = {
    actionBtnId: 'feature_authoring',
    config: {
      type: 'mat-button',
    },
  }
  tocConfig: any = null
  contentTypes = NsContent.EContentTypes
  askAuthorEnabled = true
  trainingLHubEnabled = false
  trainingLHubCount$?: Observable<number>
  body: SafeHtml | null = null
  viewMoreRelatedTopics = false
  hasTocStructure = false
  tocStructure: NsAppToc.ITocStructure | null = null
  contentParents: { [key: string]: NsAppToc.IContentParentResponse[] } = {}
  objKeys = Object.keys
  fragment!: string
  activeFragment = this.route.fragment.pipe(share())
  currentFragment = 'overview'
  showScroll!: boolean
  showScrollHeight = 300
  hideScrollHeight = 10
  elementPosition: any
  @ViewChild('stickyMenu', { static: true }) menuElement!: ElementRef
  @HostListener('window:scroll', ['$event'])
  handleScroll() {
    const windowScroll = window.pageYOffset
    if (windowScroll >= this.elementPosition - 100) {
      this.sticky = true
    } else {
      this.sticky = false
    }
  }

  constructor(
    private route: ActivatedRoute,
    private contentSvc: WidgetContentService,
    private tocSvc: AppTocService,
    private loggerSvc: LoggerService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
    private authAccessControlSvc: AccessControlService,
  ) {
  }

  ngOnInit() {
    // this.route.fragment.subscribe(fragment => { this.fragment = fragment })
    try {
      this.isInIframe = window.self !== window.top
    } catch (_ex) {
      this.isInIframe = false
    }
    if (this.route) {
      this.routeSubscription = this.route.data.subscribe((data: Data) => {
        this.banners = data.pageData.data.banners
        this.tocSvc.subtitleOnBanners = data.pageData.data.subtitleOnBanners || false
        this.tocSvc.showDescription = data.pageData.data.showDescription || false
        this.tocConfig = data.pageData.data
        this.initData(data)
      })
    }
    this.currentFragment = 'overview'
    this.route.fragment.subscribe((fragment: string) => {
      this.currentFragment = fragment || 'overview'
    })
  }
  ngAfterViewInit() {
    this.elementPosition = this.menuElement.nativeElement.parentElement.offsetTop
  }
  ngOnDestroy() {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }

  ngAfterViewChecked(): void {
    try {
      if (this.fragment) {
        // tslint:disable-next-line: no-non-null-assertion
        document!.querySelector(`#${this.fragment}`)!.scrollTo({
          top: 80,
          behavior: 'smooth',
        })
      }
    } catch (e) { }
  }

  get enableAnalytics(): boolean {
    if (this.configSvc.restrictedFeatures) {
      return !this.configSvc.restrictedFeatures.has('tocAnalytics')
    }
    return false
  }

  private initData(data: Data) {
    const initData = this.tocSvc.initData(data)
    this.content = initData.content
    this.errorCode = initData.errorCode
    switch (this.errorCode) {
      case NsAppToc.EWsTocErrorCode.API_FAILURE: {
        this.errorWidgetData.widgetData.errorType = ErrorType.internalServer
        break
      }
      case NsAppToc.EWsTocErrorCode.INVALID_DATA: {
        this.errorWidgetData.widgetData.errorType = ErrorType.internalServer
        break
      }
      case NsAppToc.EWsTocErrorCode.NO_DATA: {
        this.errorWidgetData.widgetData.errorType = ErrorType.internalServer
        break
      }
      default: {
        this.errorWidgetData.widgetData.errorType = ErrorType.somethingWrong
        break
      }
    }
    if (this.content && this.content.identifier && !this.forPreview) {
      this.getContinueLearningData(this.content.identifier)
    }
    this.body = this.domSanitizer.bypassSecurityTrustHtml(
      this.content && this.content.body
        ? this.forPreview
          ? this.authAccessControlSvc.proxyToAuthoringUrl(this.content.body)
          : this.content.body
        : '',
    )
    this.contentParents = {}
  }

  private getContinueLearningData(contentId: string) {
    this.resumeData = null
    this.contentSvc.fetchContentHistory(contentId).subscribe(
      data => {
        this.resumeData = data
      },
      (error: any) => {
        this.loggerSvc.error('CONTENT HISTORY FETCH ERROR >', error)
      },
    )
  }

  // @HostListener('window:scroll', [])
  // onWindowScroll() {
  //   if ((window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop) > this.showScrollHeight) {
  //     this.showScroll = true
  //   } else if (this.showScroll && (window.pageYOffset || document.documentElement.scrollTop
  //     || document.body.scrollTop) < this.hideScrollHeight) {
  //     this.showScroll = false
  //   }
  // }

  scrollToTop() {
    (function smoothscroll() {
      const currentScroll = document.documentElement.scrollTop || document.body.scrollTop
      if (currentScroll > 0) {
        // window.requestAnimationFrame(smoothscroll)
        // window.scrollTo(0, currentScroll - (currentScroll / 5))
        window.scrollTo({
          top: 0,
          behavior: 'smooth',
        })
      }
    })()
  }
}
