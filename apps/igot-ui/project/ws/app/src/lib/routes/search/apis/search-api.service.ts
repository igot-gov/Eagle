import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { Observable } from 'rxjs'
import { ISocialSearchRequest, ISocialSearchResult, ISearchAutoComplete } from '../models/search.model'
import { KeycloakService } from 'keycloak-angular'
import { NSSearch } from '@ws-widget/collection'
import { map } from 'rxjs/operators'
const PROTECTED_SLAG_V8 = '/apis/protected/v8'
const API_END_POINTS = {
  SOCIAL_VIEW_SEARCH_RESULT: `${PROTECTED_SLAG_V8}/social/post/search`,
  SEARCH_AUTO_COMPLETE: `${PROTECTED_SLAG_V8}/content/searchAutoComplete`,
  SEARCH_V6: `http://localhost:3003/proxies/v8/sunbirdigot/search`,
}
@Injectable({
  providedIn: 'root',
})
export class SearchApiService {
  constructor(private http: HttpClient, private keycloakSvc: KeycloakService) { }
  getSearchResults(request: ISocialSearchRequest): Observable<ISocialSearchResult> {
    return this.http.post<ISocialSearchResult>(API_END_POINTS.SOCIAL_VIEW_SEARCH_RESULT, request)
  }

  getSearchAutoCompleteResults(params: { q: string, l: string }): Observable<ISearchAutoComplete[]> {
    return this.http.get<ISearchAutoComplete[]>(API_END_POINTS.SEARCH_AUTO_COMPLETE, { params })
  }

  get userId(): string | undefined {
    const kc = this.keycloakSvc.getKeycloakInstance()
    if (!kc) {
      return
    }
    return (kc.tokenParsed && kc.tokenParsed.sub) || (kc.idTokenParsed && kc.idTokenParsed.sub)
  }

  getSearchV6Results(body: NSSearch.ISearchV6RequestV2): Observable<NSSearch.ISearchV6ApiResultV2> {
    return this.http.post<NSSearch.ISearchV6ApiResultV2>(API_END_POINTS.SEARCH_V6, body).pipe(map((res: NSSearch.ISearchV6ApiResultV2) => {
      res.filters = [
        {
          displayName: 'Mode',
          type: 'learningMode',
          content: [
            {
              displayName: 'Self-Paced',
              type: 'Self-Paced',
              count: 16,
              id: '',
            },
          ],
        },
        {
          displayName: 'Duration',
          type: 'duration',
          content: [
            {
              displayName: 'Less than 20 mins',
              type: '-inf-20',
              count: 2,
              id: '',
            },
            {
              displayName: 'More than 180 mins',
              type: '180-inf',
              count: 14,
              id: '',
            },
          ],
        },
        {
          displayName: 'Content Type',
          type: 'contentType',
          content: [
            {
              displayName: 'Resource',
              type: 'Resource',
              count: 13,
              id: '',
            },
            {
              displayName: 'Module',
              type: 'Collection',
              count: 1,
              id: '',
            },
            {
              displayName: 'Course',
              type: 'Course',
              count: 1,
              id: '',
            },
            {
              displayName: 'Program',
              type: 'Learning Path',
              count: 1,
              id: '',
            },
          ],
        },
        {
          displayName: 'Costs',
          type: 'exclusiveContent',
          content: [
            {
              displayName: 'Free',
              type: 'false',
              count: 16,
              id: '',
            },
          ],
        },
        {
          displayName: 'Format',
          type: 'resourceType',
          content: [
            {
              displayName: 'Lecture',
              type: 'Lecture',
              count: 7,
              id: '',
            },
            {
              displayName: 'Assessment',
              type: 'Assessment',
              count: 6,
              id: '',
            },
            {
              displayName: 'Lesson',
              type: 'Lesson',
              count: 3,
              id: '',
            },
          ],
        },
        {
          displayName: 'Published Date',
          type: 'lastUpdatedOn',
          content: [
            {
              displayName: 'Last Week',
              type: 'week',
              count: 0,
              id: '',
            },
            {
              displayName: 'Last Month',
              type: 'month',
              count: 0,
              id: '',
            },
            {
              displayName: 'Last Year',
              type: 'year',
              count: 0,
              id: '',
            },
            {
              displayName: 'Older',
              type: 'older',
              count: 0,
              id: '',
            },
          ],
        },
      ]
      for (const filter of res.filters) {
        if (filter.type === 'catalogPaths') {
          if (filter.content.length === 1) {
            filter.content = filter.content[0].children || []
          }
          break
        }
      }
      return res
    }))
  }
}
