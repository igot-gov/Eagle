import { Component, Input, OnChanges, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeHtml } from '@angular/platform-browser'
import { ActivatedRoute, Data } from '@angular/router'
import { NsContent, viewerRouteGenerator, WidgetContentService } from '@ws-widget/collection'
import { ConfigurationsService, UtilityService } from '@ws-widget/utils'
import { Observable, Subscription } from 'rxjs'
import { MobileAppsService } from '../../../../../../../../../src/app/services/mobile-apps.service'
import { NsAppToc } from '../../models/app-toc.model'
import { AppTocPathfindersService } from '../../services/app-toc-pathfinders.service'
import { AppTocService } from '../../services/app-toc.service'
import { AccessControlService } from '@ws/author'

@Component({
  selector: 'ws-app-app-toc-overview-pathfinders',
  templateUrl: './app-toc-overview-pathfinders.component.html',
  styleUrls: ['./app-toc-overview-pathfinders.component.scss'],
})
export class AppTocOverviewPathfindersComponent implements OnInit, OnChanges, OnDestroy {
  // @Input() content: NsContent.IContent | null = null
  @Input() resumeData: NsContent.IContinueLearningData | null = null
  content: NsContent.IContent | null = null
  routeSubscription: Subscription | null = null
  viewMoreRelatedTopics = false
  hasTocStructure = false
  tocStructure: NsAppToc.ITocStructure | null = null
  askAuthorEnabled = true
  trainingLHubEnabled = false
  isSiemensdetailsShown = false
  ispathfinderdetailsShown = false
  trainingLHubCount$?: Observable<number>
  body: SafeHtml | null = null
  resumeDataLink: { url: string; queryParams: { [key: string]: any } } | null = null
  isStartButtonAvailable = false
  forPreview = window.location.href.includes('/author/')
  firstResourceLink: { url: string; queryParams: { [key: string]: any } } | null = null
  contentTypes = NsContent.EContentTypes
  showMoreGlance = false

  contextId?: string
  contextPath?: string

  constructor(
    private route: ActivatedRoute,
    private tocSharedSvc: AppTocService,
    private configSvc: ConfigurationsService,
    private domSanitizer: DomSanitizer,
    private tocSvc: AppTocPathfindersService,
    private tocService: AppTocService,
    private mobileAppsSvc: MobileAppsService,
    private utilitySvc: UtilityService,
    private contentSvc: WidgetContentService,
    private authAccessControlSvc: AccessControlService,
  ) {
    if (this.configSvc.instanceConfig) {
      if (this.configSvc.instanceConfig.rootOrg === 'Infosys Ltd') {
        this.isSiemensdetailsShown = true
      } else {
        this.isSiemensdetailsShown = false
      }
    }
  }

  ngOnInit() {
    if (this.route && this.route.parent) {
      this.routeSubscription = this.route.parent.data.subscribe((data: Data) => {
        this.initData(data)
        this.getLearningUrls()
      })
    }

    this.routeSubscription = this.route.queryParamMap.subscribe(qParamsMap => {
      const contextId = qParamsMap.get('contextId')
      const contextPath = qParamsMap.get('contextPath')
      if (contextId && contextPath) {
        this.contextId = contextId
        this.contextPath = contextPath
      }
    })
  }

  get showDescription() {
    if (this.content && !this.content.body) {
      return true
    }
    return this.tocSharedSvc.showDescription
  }

  private initData(data: Data) {
    const initData = this.tocSharedSvc.initData(data)
    this.content = initData.content
    this.body = this.domSanitizer.bypassSecurityTrustHtml(
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
      this.tocSvc
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
    return this.tocService.showStartButton(this.content)
  }

  get isMobile(): boolean {
    return this.utilitySvc.isMobile
  }

  get getIconUrl(): string {
    if (this.content && this.content.sourceIconUrl) {
      const splitString = this.content.sourceIconUrl.split('/')
      const temp = splitString[splitString.length - 1].split('.')
      splitString[splitString.length - 1] = `${temp[0]}_big.${temp[1]}`
      return splitString.join('/')
    }
    return ''
  }

  ngOnChanges() {
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

  private getLearningUrls() {
    if (this.content) {
      const firstPlayableContent = this.contentSvc.getFirstChildInHierarchy(this.content)
      this.firstResourceLink = viewerRouteGenerator(
        firstPlayableContent.identifier,
        firstPlayableContent.mimeType,
        this.isResource ? undefined : this.content.identifier,
        this.isResource ? undefined : this.content.contentType,
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

  ngOnDestroy() {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }
}
