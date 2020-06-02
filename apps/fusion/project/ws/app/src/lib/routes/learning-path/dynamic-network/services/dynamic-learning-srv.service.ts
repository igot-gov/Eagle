import { Injectable } from '@angular/core'
import { WidgetContentService, NsContent } from '@ws-widget/collection'
import { Observable } from 'rxjs'
const ADDITIONAL_FIELDS_IN_CONTENT = [
  'averageRating',
  'body',
  'creatorContacts',
  'introductoryVideo',
  'introductoryVideoIcon',
  'playgroundResources',
  'registrationInstructions',
  'subtitle',
  'softwareRequirements',
  'systemRequirements',
  'totalRating',
  'uniqueLearners',
  'viewCount',
]
@Injectable({
  providedIn: 'root',
})
export class DynamicLearningSrvService {
  constructor(private contentSvc: WidgetContentService) {}

  getContent(contentId: string): Observable<NsContent.IContent> {
    try {
      return this.contentSvc.fetchContent(contentId, 'detail', ADDITIONAL_FIELDS_IN_CONTENT)
    } catch (e) {
      throw e
    }
  }
}
