import { Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeStyle, SafeHtml } from '@angular/platform-browser'
import { ActivatedRoute, Event, NavigationEnd, Router } from '@angular/router'
import {
  NsContent,
  viewerRouteGenerator,
  WidgetContentService,
  NsPlaylist,
  ContentProgressService,
} from '@ws-widget/collection'
import { ConfigurationsService, TFetchStatus } from '@ws-widget/utils'
import { UtilityService } from '@ws-widget/utils/src/lib/services/utility.service'
import { Subscription } from 'rxjs'
import { NsAnalytics } from '../../models/app-toc-analytics.model'
import { NsAppToc } from '../../models/app-toc.model'
import { AppTocService } from '../../services/app-toc.service'
import { AccessControlService } from '../../../../../../../author/src/public-api'
import { AppTocPathfindersService } from '../../services/app-toc-pathfinders.service'
import { MobileAppsService } from '../../../../../../../../../src/app/services/mobile-apps.service'

@Component({
  selector: 'ws-app-app-toc-banner-pathfinders',
  templateUrl: './app-toc-banner-pathfinders.component.html',
  styleUrls: ['./app-toc-banner-pathfinders.component.scss'],
})
export class AppTocBannerPathfindersComponent implements OnInit, OnChanges, OnDestroy {
  @Input() forPreview = false
  @Input() banners: NsAppToc.ITocBanner | null = null
  @Input() content: NsContent.IContent | null = null
  @Input() resumeData: NsContent.IContinueLearningData | null = null
  @Input() analytics: NsAnalytics.IAnalytics | null = null
  contentProgress = 0
  bannerUrl: SafeStyle | null = null
  routePath = 'overview'
  validPaths = new Set(['overview', 'contents', 'analytics', 'classmates'])
  routerParamSubscription: Subscription | null = null
  routeSubscription: Subscription | null = null
  firstResourceLink: { url: string; queryParams: { [key: string]: any } } | null = null
  resumeDataLink: { url: string; queryParams: { [key: string]: any } } | null = null
  isAssessVisible = false
  isPracticeVisible = false
  analyticsData: NsAnalytics.IAnalyticsResponse | null = null
  analyticsDataClient: any = null
  isRegistrationSupported = false
  checkRegistrationSources: Set<string> = new Set([
    'SkillSoft Digitalization',
    'SkillSoft Leadership',
    'Pluralsight',
  ])
  isUserRegistered = false
  actionBtnStatus = 'wait'

  externalContentFetchStatus: TFetchStatus = 'done'
  registerForExternal = false
  isStartButtonAvailable = false
  body: SafeHtml | null = null
  btnPlaylistConfig: NsPlaylist.IBtnPlaylist | null = null
  contextId?: string
  contextPath?: string

  constructor(
    private sanitizer: DomSanitizer,
    private router: Router,
    private route: ActivatedRoute,
    private tocSvc: AppTocService,
    private configSvc: ConfigurationsService,
    private tocPathfindersSvc: AppTocPathfindersService,
    private progressSvc: ContentProgressService,
    private mobileAppsSvc: MobileAppsService,
    private contentSvc: WidgetContentService,
    private utilitySvc: UtilityService,
    private authAccessControlSvc: AccessControlService,
  ) {}

  ngOnInit() {
    this.initData()
    this.routeSubscription = this.route.queryParamMap.subscribe(qParamsMap => {
      const contextId = qParamsMap.get('contextId')
      const contextPath = qParamsMap.get('contextPath')
      if (contextId && contextPath) {
        this.contextId = contextId
        this.contextPath = contextPath
      }
    })
    this.routerParamSubscription = this.router.events.subscribe((routerEvent: Event) => {
      if (routerEvent instanceof NavigationEnd) {
        this.assignPathAndUpdateBanner(routerEvent.url)
      }
    })

    if (this.content) {
      this.btnPlaylistConfig = {
        contentId: this.content.identifier,
        contentName: this.content.name,
        contentType: this.content.contentType,
        mode: 'dialog',
      }
    }
  }

  getRatingIcon(ratingIndex: number): 'star' | 'star_border' | 'star_half' {
    if (this.content && this.content.averageRating) {
      const avgRating = this.content.averageRating
      const ratingFloor = Math.floor(avgRating)
      if (ratingIndex <= ratingFloor) {
        return 'star'
      }
      if (ratingFloor === ratingIndex - 1 && avgRating % 1 > 0) {
        return 'star_half'
      }
    }
    return 'star_border'
  }

  private initData() {
    this.body = this.sanitizer.bypassSecurityTrustHtml(
      this.content && this.content.body
        ? this.forPreview
          ? this.authAccessControlSvc.proxyToAuthoringUrl(this.content.body)
          : this.content.body
        : '',
    )
    const userProfile = this.configSvc.userProfile

    if (this.content && this.content.learningMode === 'Closed') {
      if (
        !this.forPreview &&
        userProfile &&
        this.content.creatorDetails.map(user => user.id).indexOf(userProfile.userId) === -1
      ) {
        this.checkStartButton()
      } else {
        this.isStartButtonAvailable = this.checkIfArtifactUrlAvailable()
      }
    } else if (this.content && this.content.learningMode === 'Open') {
      this.isStartButtonAvailable = this.checkIfArtifactUrlAvailable()
    }
  }

  checkIfArtifactUrlAvailable(): boolean {
    return this.content && this.content.artifactUrl.length > 0 ? true : false
  }
  checkStartButton() {
    if (this.content) {
      this.tocPathfindersSvc
        .verifyAttendedUsers(this.content.identifier)
        .subscribe((response: { [key: string]: boolean }) => {
          if (response && this.content) {
            this.isStartButtonAvailable =
              this.checkIfArtifactUrlAvailable() && response[this.content.identifier]
                ? response[this.content.identifier]
                : false
          }
        })
    }
  }

  get showStart() {
    return this.tocSvc.showStartButton(this.content)
  }

  get isMobile(): boolean {
    return this.utilitySvc.isMobile
  }

  get showSubtitleOnBanner() {
    return this.tocSvc.subtitleOnBanners
  }

  ngOnChanges() {
    this.assignPathAndUpdateBanner(this.router.url)
    if (this.content) {
      // // this.content.status = 'Deleted'
      // this.fetchExternalContentAccess()
      this.modifySensibleContentRating()
      this.assignPathAndUpdateBanner(this.router.url)
      this.getLearningUrls()
      if (this.routePath === 'analytics' && this.analytics && this.analytics.courseAnalytics) {
        this.tocSvc.fetchContentAnalyticsData(this.content.identifier)
        this.tocSvc.analyticsReplaySubject.subscribe((data: NsAnalytics.IAnalyticsResponse) => {
          this.analyticsData = data
        })
      }
      if (
        this.routePath === 'analytics' &&
        this.analytics &&
        this.analytics.courseAnalyticsClient
      ) {
        this.tocSvc.fetchContentAnalyticsClientData(this.content.identifier)
        this.tocSvc.analyticsReplaySubject.subscribe((data: any) => {
          this.analyticsDataClient = data
        })
      }
    }
    if (this.resumeData && this.content) {
      this.resumeDataLink = viewerRouteGenerator(
        this.resumeData.identifier,
        this.resumeData.mimeType,
        this.isResource ? undefined : this.content.identifier,
        this.isResource ? undefined : this.content.contentType,
      )
    }
  }

  get isResource() {
    if (this.content) {
      const isResource =
        this.content.contentType === NsContent.EContentTypes.KNOWLEDGE_ARTIFACT ||
        this.content.contentType === NsContent.EContentTypes.RESOURCE
      if (isResource) {
        this.mobileAppsSvc.sendViewerData(this.content)
      }
      return isResource
    }
    return false
  }
  ngOnDestroy() {
    this.tocSvc.analyticsFetchStatus = 'none'
    if (this.routerParamSubscription) {
      this.routerParamSubscription.unsubscribe()
    }
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }

  private modifySensibleContentRating() {
    if (
      this.content &&
      this.content.averageRating &&
      typeof this.content.averageRating !== 'number'
    ) {
      this.content.averageRating = (this.content.averageRating as any)[this.configSvc.rootOrg || '']
    }
    if (this.content && this.content.totalRating && typeof this.content.totalRating !== 'number') {
      this.content.totalRating = (this.content.totalRating as any)[this.configSvc.rootOrg || '']
    }
  }

  private getLearningUrls() {
    if (this.content) {
      if (!this.forPreview) {
        this.progressSvc.getProgressFor(this.content.identifier).subscribe(data => {
          this.contentProgress = data
        })
      }
      const firstPlayableContent = this.contentSvc.getFirstChildInHierarchy(this.content)
      this.firstResourceLink = viewerRouteGenerator(
        firstPlayableContent.identifier,
        firstPlayableContent.mimeType,
        this.isResource ? undefined : this.content.identifier,
        this.isResource ? undefined : this.content.contentType,
      )
    }
  }
  private assignPathAndUpdateBanner(url: string) {
    const path = url.split('/').pop()
    if (path && this.validPaths.has(path)) {
      this.routePath = path
      this.updateBannerUrl()
    }
    if (
      this.routePath === 'analytics' &&
      this.content &&
      this.analytics &&
      this.analytics.courseAnalyticsClient
    ) {
      this.tocSvc.fetchContentAnalyticsClientData(this.content.identifier)
      this.tocSvc.analyticsReplaySubject.subscribe((data: any) => {
        this.analyticsDataClient = data
      })
    }
    if (
      this.routePath === 'analytics' &&
      this.content &&
      this.analytics &&
      this.analytics.courseAnalytics
    ) {
      this.tocSvc.fetchContentAnalyticsData(this.content.identifier)
      this.tocSvc.analyticsReplaySubject.subscribe((data: NsAnalytics.IAnalyticsResponse) => {
        this.analyticsData = data
      })
    }
  }
  private updateBannerUrl() {
    if (this.banners) {
      this.bannerUrl = this.sanitizer.bypassSecurityTrustStyle(
        `url(${this.banners[this.routePath]})`,
      )
    }
  }

  generateQuery(type: 'RESUME' | 'START_OVER' | 'START'): { [key: string]: string } {
    if (this.firstResourceLink && (type === 'START' || type === 'START_OVER')) {
      let qParams: { [key: string]: string } = {
        ...this.firstResourceLink.queryParams,
        viewMode: type,
      }
      if (this.contextId && this.contextPath) {
        qParams = {
          ...qParams,
          collectionId: this.contextId,
          collectionType: this.contextPath,
        }
      }
      return qParams
    }
    if (this.resumeDataLink && type === 'RESUME') {
      let qParams: { [key: string]: string } = {
        ...this.resumeDataLink.queryParams,
        viewMode: 'RESUME',
      }
      if (this.contextId && this.contextPath) {
        qParams = {
          ...qParams,
          collectionId: this.contextId,
          collectionType: this.contextPath,
        }
      }
      return qParams
    }
    return {
      viewMode: type,
    }
  }
}
