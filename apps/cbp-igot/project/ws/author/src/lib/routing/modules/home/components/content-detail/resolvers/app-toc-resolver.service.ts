import { Injectable } from '@angular/core'
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router'
import { NsContent, WidgetContentService } from '@ws-widget/collection'
// import { IResolveResponse } from '@ws-widget/utils/src/public-api'
import { Observable, of } from 'rxjs'
import { catchError } from 'rxjs/operators'

const ADDITIONAL_FIELDS_IN_CONTENT = [
  'averageRating',
  'body',
  'creatorContacts',
  'creatorDetails',
  'curatedTags',
  'contentType',
  'collections',
  'hasTranslations',
  'expiryDate',
  'exclusiveContent',
  'introductoryVideo',
  'introductoryVideoIcon',
  'isInIntranet',
  'isTranslationOf',
  'keywords',
  'learningMode',
  'license',
  'playgroundResources',
  'price',
  'registrationInstructions',
  'region',
  'registrationUrl',
  'resourceType',
  'subTitle',
  'softwareRequirements',
  'studyMaterials',
  'systemRequirements',
  'totalRating',
  'uniqueLearners',
  'viewCount',
  'labels',
  'sourceUrl',
  'sourceName',
  'sourceShortName',
  'sourceIconUrl',
  'locale',
  'hasAssessment',
  'preContents',
  'postContents',
  'kArtifacts',
  'equivalentCertifications',
  'certificationList',
  'posterImage',
]
@Injectable()
export class AppTocResolverService
  implements
  Resolve<
  Observable<NsContent.IContent> | null
  > {
  constructor(
    private contentSvc: WidgetContentService,
    // private routePipe: PipeContentRoutePipe,
    private router: Router,
  ) { }

  resolve(
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot,
  ): Observable<NsContent.IContent> | null {
    const contentId = route.paramMap.get('contentId') || null
    if (contentId) {
      const forPreview = true // window.location.href.includes('/author/')
      return (forPreview
        ? this.contentSvc.fetchAuthoringContent(contentId)
        : this.contentSvc.fetchContent(contentId, 'detail', ADDITIONAL_FIELDS_IN_CONTENT)
      ).pipe(
        catchError((v: any) => {
          this.router.navigateByUrl('/error-somethings-wrong')
          return of(v)
        }))
    }
    return null
  }
  //         // map(data => ({ data, error: null })),
  //         // tap(resolveData => {
  //           // return resolveData && resolveData.data
  //           // let currentRoute: string[] | string = window.location.href.split('/')
  //           // currentRoute = currentRoute[currentRoute.length - 1]
  //           // if (forPreview && currentRoute !== 'contents' && currentRoute !== 'overview') {
  //           //   this.router.navigate([
  //           //     `${forPreview ? '/author' :
  //            '/app'}/toc/${resolveData.data.identifier}/${resolveData.data.children.length ?
  //                        'contents' : 'overview'
  //           //     }`,
  //           //   ])
  //           // } else if (
  //           //   currentRoute === 'contents' &&
  //           //   resolveData.data &&
  //           //   !resolveData.data.children.length
  //           // ) {
  //           //   this.router.navigate([
  //           //     `${forPreview ? '/author' : '/app'}/toc/${resolveData.data.identifier}/overview`,
  //           //   ])
  //           // } else if (
  //           //   resolveData.data &&
  //           //   !forPreview &&
  //           //   (resolveData.data.contentType === NsContent.EContentTypes.CHANNEL ||
  //           //     resolveData.data.contentType === NsContent.EContentTypes.KNOWLEDGE_BOARD)
  //           // ) {
  //           //   const urlObj = this.routePipe.transform(resolveData.data, forPreview)
  //           //   this.router.navigate([urlObj.url], { queryParams: urlObj.queryParams })
  //           // }
  //         // }),
  //         catchError((error: any) => of({ error, data: null })),
  //       )
  //     }
  //     return null
  //   }
  // }

}
