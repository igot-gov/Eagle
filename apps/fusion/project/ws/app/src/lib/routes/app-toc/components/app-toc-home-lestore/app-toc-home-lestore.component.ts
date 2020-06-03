import { Component, OnDestroy, OnInit } from '@angular/core'
import { ActivatedRoute, Data } from '@angular/router'
import { NsContent, WidgetContentService } from '@ws-widget/collection'
import { NsWidgetResolver } from '@ws-widget/resolver'
import { ConfigurationsService, LoggerService, NsPage } from '@ws-widget/utils'
import { Subscription } from 'rxjs'
import { NsAppToc } from '../../models/app-toc.model'
import { AppTocService } from '../../services/app-toc.service'

export enum ErrorType {
  internalServer = 'internalServer',
  serviceUnavailable = 'serviceUnavailable',
  somethingWrong = 'somethingWrong',
}
@Component({
  selector: 'ws-app-app-toc-home-lestore',
  templateUrl: './app-toc-home-lestore.component.html',
  styleUrls: ['./app-toc-home-lestore.component.scss'],
})
export class AppTocHomeLestoreComponent implements OnInit, OnDestroy {
  banners: NsAppToc.ITocBanner | null = null
  content: NsContent.IContent | null = null
  assetUrl: string | null = null
  errorCode: NsAppToc.EWsTocErrorCode | null = null
  resumeData: NsContent.IContinueLearningData | null = null
  routeSubscription: Subscription | null = null
  pageNavbar: Partial<NsPage.INavBackground> = this.configSvc.pageNavBar
  isCohortsRestricted = false
  analytics = this.route.snapshot.data.pageData.data.analytics
  forPreview = window.location.href.includes('/author/')
  errorWidgetData: NsWidgetResolver.IRenderConfigWithTypedData<any> = {
    widgetType: 'errorResolver',
    widgetSubType: 'errorResolver',
    widgetData: {
      errorType: 'internalServer',
    },
  }
  isInIframe = false
  isAuthor = false
  authorBtnWidget: NsPage.INavLink = {
    actionBtnId: 'feature_authoring',
    config: {
      type: 'mat-button',
    },
  }

  constructor(
    private route: ActivatedRoute,
    private contentSvc: WidgetContentService,
    private tocSvc: AppTocService,
    private loggerSvc: LoggerService,
    private configSvc: ConfigurationsService,
  ) {
    if (this.configSvc.restrictedFeatures) {
      this.isCohortsRestricted = this.configSvc.restrictedFeatures.has('cohorts')
    }
  }

  ngOnInit() {
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
        this.initData(data)
      })
    }
  }

  private checkIfEditEnabled() {
    const userProfile = this.configSvc.userProfile
    const restrictedFeatures = this.configSvc.restrictedFeatures
    if (userProfile && this.content && restrictedFeatures) {
      if (
        !restrictedFeatures.has('editContent') ||
        (!restrictedFeatures.has('editContentAuthor') &&
          this.content.creatorContacts &&
          this.content.creatorContacts.some(creator => creator.id === userProfile.userId))
      ) {
        this.isAuthor = true
      }
    }
  }

  ngOnDestroy() {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }

  private initData(data: Data) {
    const initData = this.tocSvc.initData(data)
    this.content = initData.content
    if ((initData.content != null) && (initData.content.children.length > 0)) {
      initData.content.children.forEach(children => {
        if (children.name.indexOf('assetUrl: ') > -1) {
          this.assetUrl = children.artifactUrl
        }
      })
    }
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
    if (this.content && this.content.identifier) {
      this.checkIfEditEnabled()
      this.getContinueLearningData(this.content.identifier)
    }
  }

  private getContinueLearningData(contentId: string) {
    this.resumeData = null
    this.contentSvc.fetchContentHistory(contentId).subscribe(
      data => {
        this.resumeData = data
      },
      (error: any) => {
        this.loggerSvc.error('CONTENT  HISTORY FETCH ERROR >', error)
      },
    )
  }
}
