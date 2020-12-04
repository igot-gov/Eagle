import { AccessControlService } from '@ws/author'
import { Component, Input, OnDestroy, OnInit } from '@angular/core'
import { DomSanitizer, SafeHtml } from '@angular/platform-browser'
import { ActivatedRoute, Data } from '@angular/router'
import { NsContent } from '@ws-widget/collection'
import { ConfigurationsService } from '@ws-widget/utils'
import { Observable, Subscription } from 'rxjs'
import { share } from 'rxjs/operators'
// import { TrainingApiService } from '../../../infy/routes/training/apis/training-api.service'
// import { TrainingService } from '../../../infy/routes/training/services/training.service'
import { NsAppToc } from '../../models/app-toc.model'
import { AppTocService } from '../../services/app-toc.service'
import { BtnMailUserDialogComponent } from '@ws-widget/collection/src/lib/btn-mail-user/btn-mail-user-dialog/btn-mail-user-dialog.component'
import { IBtnMailUser } from '@ws-widget/collection/src/lib/btn-mail-user/btn-mail-user.component'
import { MatDialog } from '@angular/material'
import { TitleTagService } from '@ws/app/src/lib/routes/app-toc/services/title-tag.service'

@Component({
  selector: 'ws-app-app-toc-single-page',
  templateUrl: './app-toc-single-page.component.html',
  styleUrls: ['./app-toc-single-page.component.scss'],
})
export class AppTocSinglePageComponent implements OnInit, OnDestroy {
  contentTypes = NsContent.EContentTypes
  showMoreGlance = false
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
  content: NsContent.IContent | null = null
  routeSubscription: Subscription | null = null
  @Input() forPreview = false
  tocConfig: any = null
  loggedInUserId!: any

  constructor(
    private route: ActivatedRoute,
    private tocSharedSvc: AppTocService,
    public configSvc: ConfigurationsService,
    // private trainingApi: TrainingApiService,
    // private trainingSvc: TrainingService,
    private domSanitizer: DomSanitizer,
    private authAccessControlSvc: AccessControlService,
    private dialog: MatDialog,
    private titleTagService: TitleTagService,
  ) {
    if (this.configSvc.restrictedFeatures) {
      this.askAuthorEnabled = !this.configSvc.restrictedFeatures.has('askAuthor')
      this.trainingLHubEnabled = !this.configSvc.restrictedFeatures.has('trainingLHub')
    }
  }

  ngOnInit() {
    if (!this.forPreview) {
      this.forPreview = window.location.href.includes('/author/')
    }
    if (this.route && this.route.parent) {
      this.routeSubscription = this.route.parent.data.subscribe((data: Data) => {
        this.initData(data)
        this.tocConfig = data.pageData.data
      })
    }
    if (this.configSvc && this.configSvc.userProfile && this.configSvc.userProfile.userId) {
      this.loggedInUserId = this.configSvc.userProfile.userId
    }
  }

  detailUrl(data: any) {
    // let locationOrigin = environment.sitePath ? `https://${environment.sitePath}` : location.origin
    let locationOrigin = location.origin
    if (this.configSvc.activeLocale && this.configSvc.activeLocale.path) {
      locationOrigin += `/${this.configSvc.activeLocale.path}`
    }
    switch (data.contentType) {
      case NsContent.EContentTypes.CHANNEL:
        return `${locationOrigin}${data.artifactUrl}`
      case NsContent.EContentTypes.KNOWLEDGE_BOARD:
        return `${locationOrigin}/app/knowledge-board/${data.identifier}`
      case NsContent.EContentTypes.KNOWLEDGE_ARTIFACT:

        return `${locationOrigin}/app/toc/${data.identifier}/overview`
      default:
        return `${locationOrigin}/app/toc/${data.identifier}/overview`
    }
  }

  ngOnDestroy() {
    if (this.routeSubscription) {
      this.routeSubscription.unsubscribe()
    }
  }

  get showSubtitleOnBanner() {
    return this.tocSharedSvc.subtitleOnBanners
  }
  get showDescription() {
    if (this.content && !this.content.body) {
      return true
    }
    return this.tocSharedSvc.showDescription
  }

  setSocialMediaMetaTags(data: any) {
    this.titleTagService.setSocialMediaTags(
      this.detailUrl(data),
      data.name,
      data.description,
      data.appIcon)
  }

  private initData(data: Data) {
    const initData = this.tocSharedSvc.initData(data)
    this.content = initData.content
    this.setSocialMediaMetaTags(this.content)
    this.body = this.domSanitizer.bypassSecurityTrustHtml(
      this.content && this.content.body
        ? this.forPreview
          ? this.authAccessControlSvc.proxyToAuthoringUrl(this.content.body)
          : this.content.body
        : '',
    )
    this.contentParents = {}
    this.resetAndFetchTocStructure()
    this.getTrainingCount()
    this.getContentParent()
  }

  getContentParent() {
    if (this.content) {
      const contentParentReq: NsAppToc.IContentParentReq = {
        fields: ['contentType', 'name'],
      }
      this.tocSharedSvc
        .fetchContentParent(this.content.identifier, contentParentReq, this.forPreview)
        .subscribe(
          res => {
            this.parseContentParent(res)
          },
          _err => {
            this.contentParents = {}
          },
        )
    }
  }

  parseContentParent(content: NsAppToc.IContentParentResponse) {
    content.collections.forEach(collection => {
      if (!this.contentParents.hasOwnProperty(collection.contentType)) {
        this.contentParents[collection.contentType] = []
      }
      this.contentParents[collection.contentType].push(collection)
      this.parseContentParent(collection)
    })
  }

  resetAndFetchTocStructure() {
    this.tocStructure = {
      assessment: 0,
      course: 0,
      handsOn: 0,
      interactiveVideo: 0,
      learningModule: 0,
      other: 0,
      pdf: 0,
      podcast: 0,
      quiz: 0,
      video: 0,
      webModule: 0,
      webPage: 0,
      youtube: 0,
    }
    if (this.content) {
      this.hasTocStructure = false
      this.tocStructure.learningModule = this.content.contentType === 'Collection' ? -1 : 0
      this.tocStructure.course = this.content.contentType === 'Course' ? -1 : 0
      this.tocStructure = this.tocSharedSvc.getTocStructure(this.content, this.tocStructure)
      for (const progType in this.tocStructure) {
        if (this.tocStructure[progType] > 0) {
          this.hasTocStructure = true
          break
        }
      }
    }
  }

  // For Learning Hub trainings
  private getTrainingCount() {
    // if (
    //   this.trainingLHubEnabled &&
    //   this.content &&
    //   this.trainingSvc.isValidTrainingContent(this.content) &&
    //   !this.forPreview
    // ) {
    //   this.trainingLHubCount$ = this.trainingApi
    //     .getTrainingCount(this.content.identifier)
    //     .pipe(retry(2))
    // }
  }

  openQueryMailDialog(content: any, data: any) {
    const emailArray = []
    emailArray.push(data.email)
    const dialogdata = {
      content,
      user: data,
      emails: emailArray,
    }
    dialogdata.user.isAuthor = true
    this.dialog.open<BtnMailUserDialogComponent, IBtnMailUser>(
      BtnMailUserDialogComponent,
      {
        // width: '50vw',
        minWidth: '40vw',
        maxWidth: '80vw',
        data: dialogdata,
      }
    )
  }
}
