import { Injectable } from '@angular/core'
import { HttpClient } from '@angular/common/http'
import { TFetchStatus } from '@ws-widget/utils'
import { ReplaySubject, Observable } from 'rxjs'
import { IAnalyticsResponse } from '../models/maq-app.model'

const API_SERVER_BASE = '/apis'

const PROXIES_SLAG_V8 = `${API_SERVER_BASE}/proxies/v8`

@Injectable({
  providedIn: 'root',
})
export class MaqAppService {
  analyticsFetchStatus: TFetchStatus = 'none'
  private analyticsReplaySubject: ReplaySubject<any> = new ReplaySubject(0)
  constructor(private http: HttpClient) { }

  fetchContentAnalyticsData(tagName: string) {
    if (this.analyticsFetchStatus !== 'fetching' && this.analyticsFetchStatus !== 'done') {
      this.getContentAnalytics(tagName)
    }
    return this.analyticsReplaySubject
  }
  getContentAnalytics(tagName: string): Observable<IAnalyticsResponse> {
    this.analyticsFetchStatus = 'fetching'
    // tslint:disable-next-line: max-line-length
    const url = `${PROXIES_SLAG_V8}/LA/LA/api/participants?aggsSize=1000&endDate=${this.endDate}&startDate=${this.startDate}&from=0&refinementfilter=${encodeURIComponent('"source":["LEX","Learning Hub"]')}$${encodeURIComponent(`"topics": ["${tagName}"]`)}`
    return this.http.get<IAnalyticsResponse>(url)
  }

  private get endDate() {
    return `${new Date().getFullYear()}-${`0${new Date().getMonth() + 1}`.slice(-2)}-${`0${new Date().getDate()}`.slice(-2)}`
  }
  private get startDate() {
    return `2018-04-01`
  }
}
