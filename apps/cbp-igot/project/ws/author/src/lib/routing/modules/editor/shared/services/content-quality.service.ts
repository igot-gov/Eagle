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

  downloadFile(url: string, name: string) {
    const a = window.document.createElement('a')
    a.href = url
    a.download = name
    document.body.appendChild(a)
    a.click() // IE: "Access is denied";
    // see: https://connect.microsoft.com/IE/feedback/details/797361/ie-10-treats-blob-url-as-cross-origin-and-denies-access
    document.body.removeChild(a)
  }

  s2ab(s: any) {
    const buf = new ArrayBuffer(s.length)
    const view = new Uint8Array(buf)
    for (let i = 0; i !== s.length; i += 1) {
      /* tslint:disable */
      view[i] = s.charCodeAt(i) & 0xFF
      /* tslint:enable */
    }
    return buf
  }

  getFile(data: any, name: string, needseprate: boolean) {
    import('xlsx').then(XLSX => {
      const wb = XLSX.utils.book_new()
      let ws = null
      if (!needseprate) {
        ws = XLSX.utils.json_to_sheet(data)
        wb.SheetNames.push(name || '')
        wb.Sheets[name || ''] = ws
      } else {
        for (let i = 0; i < Object.keys(data).length; i += 1) {
          ws = XLSX.utils.json_to_sheet(data[Object.keys(data)[i]])
          wb.SheetNames.push(Object.keys(data)[i])
          // wb.SheetNames.push(data[i][0].name)
          wb.Sheets[Object.keys(data)[i]] = ws
        }
      }
      const wbout = XLSX.write(wb, {
        bookType: 'xlsx',
        bookSST: true,
        type: 'binary',
      })
      const url = window.URL.createObjectURL(new Blob([this.s2ab(wbout)], {
        type: 'application/octet-stream',
      }))
      this.downloadFile(url, `${name}.xlsx`)
    })
  }
}
