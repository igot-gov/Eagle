import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { ConfigurationsService, NsUser } from 'library/ws-widget/utils/src/public-api'
import { Observable } from 'rxjs'
import { map } from 'rxjs/operators'
const PROTECTED_SLAG_V8 = `/apis/protected/v8`
const API_ENDPOINTS = {
  searchCompetency: `${PROTECTED_SLAG_V8}/competency/searchCompetency`,
  addCompetency: `${PROTECTED_SLAG_V8}/competency/addCompetency`,
}
/* this page needs refactor*/
@Injectable({
  providedIn: 'root',
})
export class CompetenceService {
  usr: any
  constructor(
    private http: HttpClient, private configSvc: ConfigurationsService) {
    this.usr = this.configSvc.userProfile
  }

  get getUserProfile(): NsUser.IUserProfile {
    return this.usr
  }
  appendPage(page: any, url: string) {
    if (page) {
      return `${url}?page=${page}`
    }
    return url
  }

  fetchCompetency(searchData: any): Observable<any> {
    return this.http.post<any>(API_ENDPOINTS.searchCompetency, searchData)
  }
  createPost(data: any) {
    return this.http.post(`${API_ENDPOINTS.addCompetency}`, data)
  }
  fetchAutocompleteCompetencyV2(query: string): Observable<any[]> {
    // if (query === undefined || query === null || query.length <= 0) {
    //   query = ''
    // }

    const comp = 'COMPETENCY'
    const arrayData = []

    const firstRequest = {
      type: comp,
      field: 'name',
      keyword: query,
    }
    const secondObj = {
      type: comp,
      field: 'status',
      keyword: 'VERIFIED',
    }
    arrayData.push(firstRequest)
    arrayData.push(secondObj)
    const finalObj = { searches: arrayData }
    return this.http
      .post<any>(`${API_ENDPOINTS.searchCompetency}`, finalObj)
      .pipe(
        map(data => data.responseData.map((item: { name: any }) => item))
      )
  }
}
