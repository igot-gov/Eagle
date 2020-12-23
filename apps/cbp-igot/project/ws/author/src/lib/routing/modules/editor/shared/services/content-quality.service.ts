import { Injectable } from '@angular/core'
import { Observable } from 'rxjs'
import { tap } from 'rxjs/operators'
import { HttpClient } from '@angular/common/http'
import { NSIQuality } from '../../../../../interface/content-quality'
/* tslint:disable */
import _ from 'lodash'
/* tslint:enable */
const PROTECTED_SLAG_V8 = '/apis/protected/v8'

const API_END_POINTS = {
  GET_SCORE: `${PROTECTED_SLAG_V8}/scroing/fetch`,
  GET_CALC: `${PROTECTED_SLAG_V8}/scroing/calculate`,
  GET_JSON: `${PROTECTED_SLAG_V8}/scroing/struct`,
}
@Injectable()
export class ContentQualityService {
  curationData: { [key: string]: NSIQuality.IQualityResponse } = {}
  currentContent!: string
  jSONStructure!: any
  constructor(
    // private apiService: ApiService,
    // private accessService: AccessControlService,
    private http: HttpClient,
    // private configSvc: ConfigurationsService,
  ) {
    // this.fetchJSON().subscribe()
  }
  getScore(id: string): NSIQuality.IQualityResponse {
    if (this.curationData[id]) {
      return this.curationData[id]
    }  // backend responding wrong
    return this.curationData[id.replace('.img', '')]
  }

  calculateScore(meta: any) {
    const result = _.get(meta, 'result.resources')
    _.each(result, (r: NSIQuality.IQualityResponse) => {
      this.curationData[r.resourceId] = JSON.parse(JSON.stringify(r))
    })

  }

  setJSONStruct(data: any) {
    this.jSONStructure = JSON.parse(JSON.stringify(data))
  }

  // fetchJSON() {
  //   debugger
  //   // // this.http.post<NSIQuality.IContentQualityConfig>(`${API_END_POINTS.GET_JSON}`, {}).pipe(tap(v => this.setJSONStruct(v)))
  //   // return this.apiService.get<NSIQuality.IContentQualityConfig>(
  //   //   `${this.configSvc.baseUrl}/feature/auth-content-quality.json`,
  //   // ).pipe(tap(v => this.setJSONStruct(v)))
  // }

  // getJSONStruct() {
  //   if (this.JSONStructure) {
  //     return this.JSONStructure
  //   } else {
  //     return this.fetchJSON().subscribe(response => {
  //       return response
  //     }, () => {
  //       return null
  //     })
  //   }
  // }

  fetchresult(data: any): Observable<NSIQuality.IQualityResponse> {
    return this.http.post<NSIQuality.IQualityResponse>(`${API_END_POINTS.GET_SCORE}`, data)
      .pipe(tap(v => this.calculateScore(v)))
  }

  postResponse(data: any) {
    return this.http.post<NSIQuality.IQualityResponse>(`${API_END_POINTS.GET_CALC}`, data)
    // .pipe(tap(v => this.calculateScore(v)))
  }

  reset() {
    this.curationData = {}
    this.currentContent = ''
  }
}
