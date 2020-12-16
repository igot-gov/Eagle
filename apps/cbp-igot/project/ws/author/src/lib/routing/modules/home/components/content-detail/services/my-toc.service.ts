import { Injectable } from '@angular/core'
import { Data } from '@angular/router'
import { Subject } from 'rxjs'
import { HttpClient } from '@angular/common/http'
import { TFetchStatus, ConfigurationsService } from '@ws-widget/utils'
import { NsAppToc } from '../interface/app-toc.model'
import { NsContent } from '@ws-widget/collection/src/lib/_services/widget-content.model'
import { IAtGlanceComponentData } from '../../../../../../../../../../../library/ws-widget/collection/src/public-api'
import { NSContent } from '../../../../../../interface/content'
// TODO: move this in some common place
// const PROTECTED_SLAG_V8 = '/apis/protected/v8'
const PROXY_SLAG_V8 = '/apis/proxies/v8'
// const API_END_POINTS = {
//   CONTENT_PARENTS: `${PROTECTED_SLAG_V8}/content/parents`,
//   CONTENT_NEXT: `${PROTECTED_SLAG_V8}/content/next`,
//   CONTENT_PARENT: (contentId: string) => `${PROTECTED_SLAG_V8}/content/${contentId}/parent`,
//   CONTENT_AUTH_PARENT: (contentId: string, rootOrg: string, org: string) =>
//     `/apis/authApi/action/content/parent/hierarchy/${contentId}?rootOrg=${rootOrg}&org=${org}`,
//   COHORTS: (cohortType: NsCohorts.ECohortTypes, contentId: string) =>
//     `${PROTECTED_SLAG_V8}/cohorts/${cohortType}/${contentId}`,
//   EXTERNAL_CONTENT: (contentId: string) =>
//     `${PROTECTED_SLAG_V8}/content/external-access/${contentId}`,
//   COHORTS_GROUP_USER: (groupId: number) => `${PROTECTED_SLAG_V8}/cohorts/${groupId}`,
//   RELATED_RESOURCE: (contentId: string, contentType: string) =>
//     `${PROTECTED_SLAG_V8}/khub/fetchRelatedResources/${contentId}/${contentType}`,
//   POST_ASSESSMENT: (contentId: string) =>
//     `${PROTECTED_SLAG_V8}/user/evaluate/post-assessment/${contentId}`,
// }
@Injectable({
  providedIn: 'root',
})
export class MyTocService {
  analyticsReplaySubject: Subject<any> = new Subject()
  analyticsFetchStatus: TFetchStatus = 'none'
  private showSubtitleOnBanners = false
  private canShowDescription = false

  constructor(
    private http: HttpClient,
    private configSvc: ConfigurationsService
  ) {
  }

  get subtitleOnBanners(): boolean {
    return this.showSubtitleOnBanners
  }
  set subtitleOnBanners(val: boolean) {
    this.showSubtitleOnBanners = val
  }
  get showDescription(): boolean {
    return this.canShowDescription
  }
  set showDescription(val: boolean) {
    this.canShowDescription = val
  }
  showStartButton(content: NsContent.IContent | null): { show: boolean; msg: string } {
    const status = {
      show: false,
      msg: '',
    }
    if (content) {
      if (
        content.artifactUrl.match(/youtu(.)?be/gi) &&
        this.configSvc.userProfile &&
        this.configSvc.userProfile.country === 'China'
      ) {
        status.show = false
        status.msg = 'youtubeForbidden'
        return status
      }
      if (content.resourceType !== 'Certification') {
        status.show = true
        return status
      }
    }
    return status
  }
  initData(data: Data): NsAppToc.IWsTocResponse {
    let content: NsContent.IContent | null = null
    let errorCode: NsAppToc.EWsTocErrorCode | null = null
    if (data && data.content && data.content.identifier) {
      content = data.content
    } else {
      if (data.error) {
        errorCode = NsAppToc.EWsTocErrorCode.API_FAILURE
      } else {
        errorCode = NsAppToc.EWsTocErrorCode.NO_DATA
      }
    }
    return {
      content,
      errorCode,
    }
  }
  fetchContentAnalyticsData(contentId: string) {
    if (this.analyticsFetchStatus !== 'fetching' && this.analyticsFetchStatus !== 'done') {
      this.getContentAnalytics(contentId)
    }
  }
  private getContentAnalytics(contentId: string) {
    this.analyticsFetchStatus = 'fetching'
    // tslint:disable-next-line: max-line-length
    const url = `${PROXY_SLAG_V8}/LA/LA/api/Users?refinementfilter=${encodeURIComponent(
      '"source":["Wingspan","Learning Hub"]',
    )}$${encodeURIComponent(`"courseCode": ["${contentId}"]`)}`
    this.http.get(url).subscribe(
      result => {
        this.analyticsFetchStatus = 'done'
        this.analyticsReplaySubject.next(result)
      },
      () => {
        this.analyticsReplaySubject.next(null)
        this.analyticsFetchStatus = 'done'
      },
    )
  }

  fetchContentAnalyticsClientData(contentId: string) {
    if (this.analyticsFetchStatus !== 'fetching' && this.analyticsFetchStatus !== 'done') {
      this.getContentAnalyticsClient(contentId)
    }
  }
  private getContentAnalyticsClient(contentId: string) {
    this.analyticsFetchStatus = 'fetching'
    const url = `${PROXY_SLAG_V8}/LA/api/la/contentanalytics?content_id=${contentId}&type=course`
    this.http.get(url).subscribe(
      result => {
        this.analyticsFetchStatus = 'done'
        this.analyticsReplaySubject.next(result)
      },
      () => {
        this.analyticsReplaySubject.next(null)
        this.analyticsFetchStatus = 'done'
      },
    )
  }
  getTocStructure(
    content: NSContent.IContentMeta,
    tocStructure: IAtGlanceComponentData.ICounts,
  ): IAtGlanceComponentData.ICounts {
    if (
      content &&
      !(content.contentType === 'Resource' || content.contentType === 'Knowledge Artifact')
    ) {
      if (content.contentType === 'Course') {
        tocStructure.course += 1
      } else if (content.contentType === 'Collection') {
        tocStructure.learningModule += 1
      }
      content.children.forEach(child => {
        // tslint:disable-next-line: no-parameter-reassignment
        tocStructure = this.getTocStructure(child, tocStructure)
      })
    } else if (
      content &&
      (content.contentType === 'Resource' || content.contentType === 'Knowledge Artifact')
    ) {
      switch (content.mimeType) {
        case NsContent.EMimeTypes.HANDS_ON:
          tocStructure.handsOn += 1
          break
        case NsContent.EMimeTypes.MP3:
          tocStructure.podcast += 1
          break
        case NsContent.EMimeTypes.MP4:
        case NsContent.EMimeTypes.M3U8:
          tocStructure.video += 1
          break
        case NsContent.EMimeTypes.INTERACTION:
          tocStructure.interactiveVideo += 1
          break
        case NsContent.EMimeTypes.PDF:
          tocStructure.pdf += 1
          break
        case NsContent.EMimeTypes.HTML:
          tocStructure.webPage += 1
          break
        case NsContent.EMimeTypes.QUIZ:
          if (content.resourceType === 'Assessment') {
            tocStructure.assessment += 1
          } else {
            tocStructure.quiz += 1
          }
          break
        case NsContent.EMimeTypes.WEB_MODULE:
          tocStructure.webModule += 1
          break
        case NsContent.EMimeTypes.YOUTUBE:
          tocStructure.youtube += 1
          break
        default:
          tocStructure.other += 1
          break
      }
      return tocStructure
    }
    return tocStructure
  }
}
